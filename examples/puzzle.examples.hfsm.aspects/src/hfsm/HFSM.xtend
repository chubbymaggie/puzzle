package hfsm

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod

import java.util.ArrayList
import java.util.Hashtable
import java.util.Scanner

import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList

import static extension hfsm.StateAspect.*
import static extension hfsm.RegionAspect.*
import static extension hfsm.TransitionAspect.*
import static extension hfsm.TriggerAspect.*
import static extension hfsm.ExpressionAspect.*
import static extension hfsm.ConstraintAspect.*
import static extension hfsm.ProgramAspect.*

// *.*
// ASPECT
@Aspect(className=StateMachine)
class StateMachineAspect { 
	
	public String chain
	public ArrayList<ArrayList<String>> events
	 
	/**
	 * Evaluates the input and sequentially executes the steps in the state machine. 
	 */
	def public void evalStateMachine() {
		println("\nExecuting the state machine. Please enter the events to process...\n")
		
		val Hashtable<String, Object> context = new Hashtable<String, Object>
		
		_self.regions.forEach[ _region | 
			_region.initRegion(context)
		]
		
		print("    step: ---> current active state (s): ")
		var _it = context.keySet.iterator
		while(_it.hasNext){
			var String _key = _it.next
			var Object _value = context.get(_key)
			if(_key.startsWith("currentState"))
				(_value as ArrayList<AbstractState>).forEach[ _vertex |
					print( _vertex.name + " ")]
		}
		
		_it = context.keySet.iterator
		var String variablesString = ""
		while(_it.hasNext){
			var String _key = _it.next
			var Object _value = context.get(_key)
			if(!_key.startsWith("currentState"))
				variablesString += " - " + _key + ": " + _value + "\n"
		}
		if(!variablesString.equals("")){
			println("\n ---> current variables' values: ")
			println(variablesString)
		}
		
		while(true){
			var Scanner in = new Scanner(System.in);
			print(" \n\n *INPUT ---> Next event: ")
			var String[] eventsChain = in.nextLine.split(",")
			for(String _event : eventsChain){
				val EList<String> events = new BasicEList<String>()
				events.add(_event)
				_self.regions.forEach[ _region | _region.step(context, events)]

				print("    step: ---> current active state (s): ")
				_it = context.keySet.iterator
				while(_it.hasNext){
					var String _key = _it.next
					var Object _value = context.get(_key)
					if(_key.startsWith("currentState"))
						(_value as ArrayList<AbstractState>).forEach[ _vertex |
							print( _vertex.name + " ")]
				}
						
				_it = context.keySet.iterator
				variablesString = ""
				while(_it.hasNext){
					var String _key = _it.next
					var Object _value = context.get(_key)
					if(!_key.startsWith("currentState"))
						variablesString += "              - " + _key + ": " + _value + "\n"
				}
				if(!variablesString.equals("")){
					println("\n          ---> current variables' values: ")
					println(variablesString)
				}
				println
			}
		}
	}
}

// *.*
// ASPECT
@Aspect(className=Region)
class RegionAspect {
	
	def public void initRegion(Hashtable<String, Object> context){
		var ArrayList<AbstractState> regionCurrentState = new ArrayList<AbstractState>();
		context.put(_self.getContextPathByRegion, regionCurrentState)
		
		// Looking for the initial pseudo-state
		var Pseudostate initialPseudostate = _self.subvertex.
						findFirst[ _vertex | (_vertex instanceof Pseudostate) && 
							(_vertex as Pseudostate).kind == PseudostateKind.INITIAL] as Pseudostate
		
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
	
	def public String getContextPathByRegion(){
		return "currentState-" + _self.name
	}
	
	/**
	 * Performs a step in the state machine i.e., reads an entry of the input stack and executes it.
	 * If there are several events in the same step they are executed sequentially.  
	 */
	def public void step(Hashtable<String, Object> context, EList<String> events){
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
	}
	
	def public void removeStatesFromContext(Hashtable<String, Object> context, ArrayList<AbstractState> toRemove){
		(context.get(_self.contextPathByRegion) as ArrayList<AbstractState>).removeAll(toRemove)
	}
	
	def public void addStatesToContext(Hashtable<String, Object> context, ArrayList<AbstractState> newStates){
		for(AbstractState _newState : newStates){
			if(!(context.get(_self.contextPathByRegion) as ArrayList<AbstractState>).contains(_newState))
				(context.get(_self.contextPathByRegion) as ArrayList<AbstractState>).add(_newState)
		}
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
			if( (transition.trigger == null) || (transition.trigger != null && transition.trigger.evalTrigger(events))){
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
		if(!oldActiveStates.contains(selectedTransition.source))
			oldActiveStates.add(selectedTransition.source)
	}
	
	/**
	 * Finds the set of states that will be active after the step.
	 */
	def public void findNewActiveStates(ArrayList<AbstractState> newActiveStates,
		Transition selectedTransition, ArrayList<Transition> currentActiveTransitions,
		Hashtable<String, Object> context){
			if(!newActiveStates.contains(selectedTransition.target) && 
				!selectedTransition.alreadyFired(context))
					newActiveStates.add(selectedTransition.target)
	}
	
	/**
	 * Finds the transitions that will be fired during the step. 
	 */
	def public void findNewActiveTransitions(ArrayList<Transition> newActiveTransitions, 
		Transition selectedTransition, Hashtable<String, Object> context){
		newActiveTransitions.add(selectedTransition)
	}
}

// *.*
// ASPECT
@Aspect(className=State)
class StateAspect {
	
	def public void entryState(Hashtable<String, Object> context){
		if(_self.entry != null)
			_self.entry.eval(context)
	}
	
	def public void evalState(Hashtable<String, Object> context) {
		if(_self.doActivity != null){
			var Runnable _runnable = new Runnable{
				override run(){
					 synchronized (_self) {
					 	_self.doActivity.eval(context)
					 }
				}
			}
		 	var Thread _thread = new Thread(_runnable)
			_thread.start
		}
	}
	
	def public void exitState(Hashtable<String, Object> context){
		if(_self.exit != null)
			_self.exit.eval(context)
	}
}

// *.*
// ASPECT
@Aspect(className=Transition)
class TransitionAspect {
	
	private boolean fired = false
	
	def public void evalTransition(Hashtable<String, Object> context){
		if(_self.validGuard(context)){
			_self.fired = true
			if(_self.target instanceof State){
				(_self.target as State).entryState(context)
				(_self.target as State).evalState(context)				
			}
		} 
	}
	
	def public boolean validGuard(Hashtable<String, Object> context){
		return (_self.guard == null) || (_self.guard != null && _self.guard.evalConstraint(context) == true)
	}
	
	def public boolean alreadyFired(Hashtable<String, Object> context){
		return _self.fired
		
	}
	
	def public void resetFired(){
		_self.fired = false
	}
}

// *.*
// ASPECT
@Aspect(className=Trigger)
class TriggerAspect {
	
	def public boolean evalTrigger(EList<String> events){
		return events.contains(_self.expression)
	}
}

// *.*
// ASPECT
@Aspect(className=FinalState)
class FinalStateAspect extends StateAspect {
	
	@OverrideAspectMethod
	def public void exitState(Hashtable<String, Object> context){
		_self.super_exitState(context)
		System.exit(0)
	}
}

@Aspect(className=Expression)
public class ExpressionAspect {

	def Object eval (Hashtable<String, Object> context) {
		return 0 
	}
}

@Aspect(className=Literal)
public class LiteralAspect extends ExpressionAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		return 0
	}
}

@Aspect(className=IntegerLit)
public class IntegerLitAspect extends LiteralAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		return _self.value
	}
}

@Aspect(className=StringLit)
public class StringLitAspect extends LiteralAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		return _self.value
	}
}

@Aspect(className=BoolLit)
public class BoolLitAspect extends LiteralAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		return _self.value
	}
}

@Aspect(className=ArithmeticExpression)
public class ArithmeticExpressionAspect extends ExpressionAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		var int result = 0
		if(_self.operator == ArithmeticOperator.PLUS){
			result = (_self.left.eval(context) as Integer) + (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == ArithmeticOperator.MINUS){
			result = (_self.left.eval(context) as Integer) - (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == ArithmeticOperator.MULT){
			result = (_self.left.eval(context) as Integer) * (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == ArithmeticOperator.DIV){
			result = (_self.left.eval(context) as Integer) / (_self.right.eval(context) as Integer)
		}
		return result
	}
}

@Aspect(className=RelationalExpression)
public class RelationalExpressionAspect extends ExpressionAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		var boolean result = false
		if(_self.operator == RelationalOperator.EQUALS){
			result = (_self.left.eval(context) as Integer) == (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == RelationalOperator.NOT_EQUAL){
			result = (_self.left.eval(context) as Integer) != (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == RelationalOperator.LESS_THAN){
			result = (_self.left.eval(context) as Integer) < (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == RelationalOperator.GREATER_THAN){
			result = (_self.left.eval(context) as Integer) > (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == RelationalOperator.LESS_THAN_OR_EQUAL_TO){
			result = (_self.left.eval(context) as Integer) <= (_self.right.eval(context) as Integer)
		}
		else if(_self.operator == RelationalOperator.GREATER_THAN_OR_EQUAL_TO){
			result = (_self.left.eval(context) as Integer) >= (_self.right.eval(context) as Integer)
		}
		return result
	}
}

@Aspect(className=Constraint)
public class ConstraintAspect {
	
	def boolean evalConstraint(Hashtable<String, Object> context){
		return false
	}
}

@Aspect(className=RelationalConstraint)
public class RelationalConstraintAspect extends ConstraintAspect {

	@OverrideAspectMethod	
	def boolean evalConstraint(Hashtable<String, Object> context){
		return _self.expression.eval(context) as Boolean
	}
}

@Aspect(className=Statement)
abstract class StatementAspect {
	
	def abstract void eval(Hashtable<java.lang.String, Object> context)
}

@Aspect(className=Program)
class ProgramAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def void eval(Hashtable<java.lang.String, Object> context){
		for(Statement _statement : _self.statements){
			_statement.eval(context)
			
		}
	}
}

@Aspect(className=VarDecl)
class VarDeclAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def void eval(Hashtable<java.lang.String, Object> context){
		context.put(_self.name, _self.expr.eval(context))
	}
}

@Aspect(className=Conditional)
class ConditionalAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def void eval(Hashtable<java.lang.String, Object> context){
		if((_self.condition as RelationalExpression).eval(context) as Boolean){
			_self.body.eval(context)
		}
	}
}

@Aspect(className=Loop)
class LoopAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def void eval(Hashtable<java.lang.String, Object> context){
		while((_self.guard as RelationalExpression).eval(context) as Boolean){
			_self.body.eval(context)
		}
	}
}

@Aspect(className=Println)
class PrintlnAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def public void eval(Hashtable<java.lang.String, Object> context){
		println(_self.input)
	}
}

@Aspect(className=Print)
class PrintAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def public void eval(Hashtable<java.lang.String, Object> context){
		print(_self.input)
	}
}

@Aspect(className=Assignation)
class AssignationAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def public void eval(Hashtable<java.lang.String, Object> context){
		context.put(_self.varRef.name, _self.expression.eval(context))
	}
}

@Aspect(className=Wait)
class WaitAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def public void eval(Hashtable<java.lang.String, Object> context){
		synchronized(_self){
			_self.wait(_self.miliseconds)
		}
	}
}