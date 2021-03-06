package fsmharelaspects

import fr.inria.diverse.k3.al.annotationprocessor.Aspect

import java.util.ArrayList
import java.util.Hashtable

import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList

import static extension fsmharelaspects.TransitionAspect.*
import static extension fsmharelaspects.StateAspect.*
import static extension fsmharelaspects.TriggerAspect.*

import fsm.AbstractState
import fsm.Pseudostate
import fsm.Transition
import fsm.State
import fsm.Region
import fsm.InitialState
import java.util.List
import fsm.DeepHistory
import fsm.Conditional
import fsm.Fork
import fsm.Join
import fsm.Junction
import fsm.ShallowHistory

// *.*
// ASPECT
@Aspect(className=Region)
class RegionAspect {
	
	private ArrayList<AbstractState> deepHistory;
	private State history;
	
	def public void initRegion(Hashtable<String, Object> context){
		
		// If it is the first time we enter to the composite state, then go to the initial one. 
		if(_self.history == null){
			// If it is the first time we enter to the composite state, then go to the initial one. 
			if(_self.deepHistory == null){
				var ArrayList<AbstractState> regionCurrentState = new ArrayList<AbstractState>();
				context.put(_self.getContextPathByRegion, regionCurrentState)
				
				// Looking for the initial pseudo-state
				var Pseudostate initialPseudostate = _self.subvertex.
								findFirst[ _vertex | (_vertex instanceof Pseudostate) && 
									(_vertex instanceof InitialState)] as Pseudostate
				
				// Dispatching the transitions of the initial pseudo-state
				var ArrayList<AbstractState> initialCurrentState = new ArrayList<AbstractState>()
				var ArrayList<Transition> initialCurrentTransitions = new ArrayList<Transition>()
				for(Transition _transition : initialPseudostate.outgoing){
					initialCurrentTransitions.add(_transition)
					initialCurrentState.add(_transition.target)
				}
				(regionCurrentState as ArrayList<AbstractState>).addAll(initialCurrentState)
				
				initialCurrentTransitions.forEach[ transition |
					transition.evalTransition(context)
				]
			}
			
			// Otherwise, go to the deep history!
			else{
				(context.get("currentState-" + _self.name) as ArrayList<AbstractState>).addAll(_self.deepHistory)
			}
		}
		
		// Otherwise, go to the shallow history!
		else{
			(context.get("currentState-" + _self.name) as ArrayList<AbstractState>).add(_self.history)
		}
	}
	
	def public String getContextPathByRegion(){
		var root = "currentState"
		var ArrayList<Region> parentRegions = new ArrayList<Region>()
		var Region currentRegion = _self
		while(currentRegion.ownerState != null && currentRegion.ownerState.ownerRegion != null){
			parentRegions.add(_self.ownerState.ownerRegion)
			currentRegion = currentRegion.ownerState.ownerRegion
		}
		
		for(var int i = parentRegions.size() - 1; i >= 0; i--){
			root += "-" + parentRegions.get(i).name
		}
		
		return root + "-" + _self.name
	}
	
	/**
	 * Performs a step in the state machine i.e., reads an entry of the input stack and executes it.
	 * If there are several events in the same step they are executed sequentially.  
	 */
	def public void step(Hashtable<String, Object> context, EList<String> events){
		var boolean allJunctionsAttended = false
		
		while(!allJunctionsAttended){
			var boolean allConditionalsAttended = false
			
			while(!allConditionalsAttended){
				
				var ArrayList<AbstractState> currentState = _self.getCurrentState(context, events)
				var ArrayList<Transition> currentTransitions = new ArrayList<Transition>()
				
				var ArrayList<AbstractState> attendedStates = new ArrayList<AbstractState>()
				var ArrayList<AbstractState> newStates = new ArrayList<AbstractState>()
				var EList<Transition> activeTransitions = new BasicEList<Transition>()
				
				for(AbstractState _state : currentState){
					activeTransitions.addAll(_self.getActiveTransitions(_state, events))
				}
				
				for(Transition transition : activeTransitions){
					_self.findOldActiveStates(attendedStates, transition, context)
					_self.findNewActiveTransitions(currentTransitions, transition, context)
					_self.findNewActiveStates(newStates, transition, currentTransitions, context)
				}
				
				for(AbstractState _attendedState : attendedStates){
					if(_attendedState instanceof State)
						(_attendedState as State).exitState(context)
				}
				
				_self.removeStatesFromContext(context, attendedStates)
				_self.addStatesToContext(context, newStates)
				
				activeTransitions.forEach[ transition |
					transition.evalTransition(context)
				]
				
				currentTransitions.forEach[ transition |
					if(!transition.alreadyFired(context))
						transition.evalTransition(context)
				]
				
				newStates.forEach[ state |
						state.outgoing.forEach[ transition | transition.resetFired() ]
				]
				
				val ArrayList<AbstractState> currentConditionalState = new ArrayList<AbstractState>
				
				var _it = context.keySet.iterator
				while(_it.hasNext){
					var String _key = _it.next
					var Object _value = context.get(_key)
					if(_key.startsWith("currentState"))
						(_value as ArrayList<AbstractState>).forEach[ _vertex |
							currentConditionalState.add(_vertex)]
				}
				
				var _conditionalPseudostate = currentConditionalState.findFirst[_vertex | (_vertex instanceof Pseudostate) &&
					(_vertex instanceof Conditional)]
					
				allConditionalsAttended = _conditionalPseudostate == null
			}
			
			var ArrayList<AbstractState> currentState = context.get("currentState-" + _self.name) as ArrayList<AbstractState>
			allJunctionsAttended = !currentState.exists[_vertex | _vertex.outgoing.exists[_outgoing|
				(_outgoing.target instanceof Pseudostate) &&
					(_outgoing.target instanceof Junction)
			]]
		}
	}
	
	def public void removeStatesFromContext(Hashtable<String, Object> context, ArrayList<AbstractState> toRemove){
		for(AbstractState _oldState : toRemove){
			(context.get(_self.getContextPath(_oldState)) as ArrayList<AbstractState>).remove(_oldState)
		}
		
	}
	
	def public void addStatesToContext(Hashtable<String, Object> context, ArrayList<AbstractState> newStates){
		
		for(AbstractState _newState : newStates){
			var String path = _self.getContextPath(_newState)
			
			if(context.get(path) == null)
				context.put(path, new ArrayList<AbstractState>())
			
			if(!(context.get(path) as ArrayList<AbstractState>).contains(_newState))
				(context.get(path) as ArrayList<AbstractState>).add(_newState)
		}
	}
	
	def public String getContextPath(AbstractState _vertex){
		var root = "currentState"
		var ArrayList<Region> parentRegions = new ArrayList<Region>()
		var Region currentRegion = _vertex.ownerRegion
		while(currentRegion != null ){
			parentRegions.add(currentRegion)
			
			if(currentRegion.ownerState != null)
				currentRegion = currentRegion.ownerState.ownerRegion
			else
				currentRegion = null
		}
		
		for(var int i = parentRegions.size() - 1; i >= 0; i--){
			root += "-" + parentRegions.get(i).name
		}
		
		return root
	}
	
	/**
	 * Returns the current state of the machine. It corresponds to the current set of active states.
	 */
	def public ArrayList<AbstractState> getCurrentState(Hashtable<String, Object> context, EList<String> events){
		val ArrayList<AbstractState> currentState = new ArrayList<AbstractState>
			
			var _it = context.keySet.iterator
			while(_it.hasNext){
				var String _key = _it.next
				var Object _value = context.get(_key)
				if(_key.startsWith("currentState-" + _self.name))
					(_value as ArrayList<AbstractState>).forEach[ _vertex |
						currentState.add(_vertex)]
			}
		return currentState
	}
	
	/**
	 * Returns the active transitions of a vertex
	 */
	def public EList<Transition> getActiveTransitions(AbstractState vertex, EList<String> events){
		val res = new BasicEList<Transition>();
		for(Transition transition : vertex.outgoing){
			if( (transition.trigger == null) || 
				(transition.trigger != null && transition.trigger.evalTrigger(events) ||
					(transition.target instanceof Pseudostate)
						&& ((transition.target instanceof Junction)))){
				res.add(transition)
			}
		}
		return res;
	}
	
	/**
	 * Finds the set of states that are active before the step and that will be left after the step. 
	 */
	def public void findOldActiveStates(ArrayList<AbstractState> oldActiveStates, 
		Transition selectedTransition, Hashtable<String, Object> context){
			
		var boolean sourceIsJunction = (selectedTransition.source instanceof Pseudostate) &&
			(selectedTransition.source instanceof Junction)
			
		if(sourceIsJunction){
			var boolean allOutputFired = !selectedTransition.source.outgoing.exists[ _transition | !_transition.alreadyFired(context)]
			var boolean junctionComplete = (sourceIsJunction && allOutputFired)
			
			if(!oldActiveStates.contains(selectedTransition.source) && junctionComplete)
				oldActiveStates.add(selectedTransition.source)
		}else{
			if(!oldActiveStates.contains(selectedTransition.source))
			oldActiveStates.add(selectedTransition.source)
			
			// Getting out of a composite state so leaving all the children states
			val ArrayList<AbstractState> sourceChildren = new ArrayList<AbstractState>()
			_self.getAllChildren(selectedTransition.source, sourceChildren)
			sourceChildren.forEach[_children | 
				if(!oldActiveStates.contains(_children))
					oldActiveStates.add(_children);
			]
			
			// Adding to the old state the parent states of the leaving parents
			val ArrayList<AbstractState> sourceParents = new ArrayList<AbstractState>()
			_self.getAllParents(selectedTransition.source, sourceParents)
			
			val ArrayList<AbstractState> targetParents = new ArrayList<AbstractState>()
			_self.getAllParents(selectedTransition.target, targetParents)
			
			
			var Iterable<AbstractState> leavingParents = sourceParents.filter[ _parent | !targetParents.contains(_parent)]
			oldActiveStates.addAll(leavingParents)
			
			for(Transition _candidate : selectedTransition.target.outgoing){
				if((_candidate.target instanceof Pseudostate)
					&& ((_candidate.target instanceof Join))){
					if(!oldActiveStates.contains(_candidate.source))
						oldActiveStates.add(_candidate.source)
				}
			}
		}
	}
	
	/**
	 * Finds the set of states that will be active after the step.
	 */
	def public void findNewActiveStates(ArrayList<AbstractState> newActiveStates,
		Transition selectedTransition, ArrayList<Transition> currentActiveTransitions,
		Hashtable<String, Object> context){
		
		if((selectedTransition.target instanceof Pseudostate)
			&& (selectedTransition.target instanceof Fork)){
			for(Transition _transition : (selectedTransition.target as Pseudostate).outgoing){
				newActiveStates.add(_transition.target)
			}
		}
		else{
			// Adding the super states to the current state.
			val ArrayList<AbstractState> targetParents = new ArrayList<AbstractState>()
			_self.getAllParents(selectedTransition.target, targetParents)
			targetParents.forEach[_parent | 
				if(!newActiveStates.contains(_parent))
						newActiveStates.add(_parent);
			]
			
			for(Transition _currentTransition : currentActiveTransitions){
				if(!newActiveStates.contains(selectedTransition.target) && 
				!selectedTransition.alreadyFired(context))
					newActiveStates.add(selectedTransition.target)
			}
					
			// Removing the states coming from conflicting transitions
			var ArrayList<AbstractState> toDelete = new ArrayList<AbstractState>()
			val ArrayList<AbstractState> targetChildren = new ArrayList<AbstractState>()
			_self.getAllChildren(selectedTransition.source, targetChildren)
			
			for(AbstractState _newState : newActiveStates){
				//Do we can delete all this?
				var boolean delete = true
				var List<Transition> transitions = new ArrayList<Transition>();
				transitions.addAll(_newState.incoming)
				for(AbstractState _children : targetChildren){
					transitions.addAll(_children.incoming)
				}
				for(Transition _incoming : transitions){
					
					if(_newState instanceof State){
						var ArrayList<AbstractState> children = newArrayList
						_self.getAllChildren(_newState, children)
						if(children.findFirst[child | newActiveStates.contains(child)] != null)
							delete = false
					}
	
					if(currentActiveTransitions.contains(_incoming))
						delete = false
				}
				
				if(delete)
					toDelete.add(_newState)
			}
			
			val ArrayList<AbstractState> moreToDelete = new ArrayList<AbstractState>()
			newActiveStates.forEach[ _state |
				val ArrayList<AbstractState> child = new ArrayList<AbstractState>()
				_self.getAllChildren(_state, child)
				if(!_state.incoming.exists[ t | currentActiveTransitions.contains(t)] &&
					!child.exists[ s | s.incoming.exists[ t | currentActiveTransitions.contains(t)]])
						moreToDelete.add(_state)
			]
			
			toDelete.addAll(moreToDelete)
			newActiveStates.removeAll(toDelete)
		}
		
		for(Transition _candidate : selectedTransition.target.outgoing){
			if((_candidate.target instanceof Pseudostate)
				&& ((_candidate.target instanceof Join))){
				if(!newActiveStates.contains(_candidate.target))
					newActiveStates.add(_candidate.target)
			}
		}
	}
	
	/**
	 * Finds the transitions that will be fired during the step. 
	 */
	def public void findNewActiveTransitions(ArrayList<Transition> newActiveTransitions, 
		Transition selectedTransition, Hashtable<String, Object> context){
			
		if((selectedTransition.target instanceof Pseudostate)
				&& (selectedTransition.target instanceof Fork)){
			newActiveTransitions.addAll((selectedTransition.target as Pseudostate).outgoing)
		}
		else{
			newActiveTransitions.add(selectedTransition)
			var ArrayList<Transition> activeTransitions = new ArrayList<Transition>()
			activeTransitions.addAll(newActiveTransitions)
			activeTransitions.add(selectedTransition)
			var boolean conflictingTransition = _self.highestConflictingTransition(activeTransitions)
	
			if(!conflictingTransition)
				newActiveTransitions.add(selectedTransition)
			else{
				newActiveTransitions.clear()
				newActiveTransitions.addAll(activeTransitions)	
			}	
		}
		
		for(Transition _candidate : selectedTransition.target.outgoing){
			if((_candidate.target instanceof Pseudostate)
				&& ((_candidate.target instanceof Join))){
				newActiveTransitions.add(_candidate)
			}
		}
		
	}
	
	def public void getAllParents(AbstractState vertex, ArrayList<AbstractState> parents){
		if(vertex instanceof State){
			var State superState = (vertex as State).ownerRegion.ownerState
			while(superState != null){
				if(!parents.contains(superState))
					parents.add(superState);
				superState = superState.ownerRegion.ownerState
			}
		}
	}
	
	def public void getAllChildren(AbstractState vertex, ArrayList<AbstractState> children){
		if(vertex instanceof State){
			if((vertex as State).ownedRegions != null){
				(vertex as State).ownedRegions.forEach[_region|
					children.addAll(_region.subvertex)
					_region.subvertex.forEach[ _vertex |
						_self.getAllChildren(_vertex, children)
					]
				]
			}
		}
	}
	
	def public boolean highestConflictingTransition(ArrayList<Transition> activeTransitions){
		val res = activeTransitions.size()
		val ArrayList<Transition> toDelete = new ArrayList<Transition>()
		activeTransitions.forEach[ x |
			activeTransitions.forEach[ y |
				if(x.source instanceof State){
					var ArrayList<AbstractState> children = new ArrayList<AbstractState>()
					_self.getAllChildren((x.source as State), children)
					if(children.contains(y.source)){
						toDelete.add(y)
					}
				}
			]
		]
		activeTransitions.removeAll(toDelete)
		return res != activeTransitions.size()
	}
	
	def public void saveDeepHistoryState(Hashtable<String, Object> context){
		println('saving the history state')
		if(_self.subvertex.exists[ _vertex | _vertex instanceof Pseudostate &&
			(_vertex instanceof DeepHistory)]){
				_self.deepHistory = new ArrayList<AbstractState>()
				val ArrayList<AbstractState> substates = new ArrayList<AbstractState>()
				_self.getAllSubstates(_self.ownerState, substates)
				_self.deepHistory.addAll(substates.filter[ _substate | 
					(context.get("currentState-" + _self.name) as ArrayList<AbstractState>).contains(_substate)])
		}
	}
	
	def public void getAllSubstates(AbstractState vertex, ArrayList<AbstractState> children){
		if(vertex instanceof State){
			if((vertex as State).ownedRegions != null){
				(vertex as State).ownedRegions.forEach[_region|
					children.addAll(_region.subvertex)
					_region.subvertex.forEach[ _vertex |
						_self.getAllSubstates(_vertex, children)
					]
				]
			}
		}
	}
	
	def public void saveHistoryState(Hashtable<String, Object> context){
		println('saving the history state')
		if(_self.subvertex.exists[ _vertex | _vertex instanceof Pseudostate &&
			(_vertex instanceof ShallowHistory)]){
				_self.history = (context.get("currentState-" + _self.name) as ArrayList<AbstractState>).findFirst[ _state |
					_state instanceof State && (_state as State).ownerRegion == _self] as State
		}
		
	}
}