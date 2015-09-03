var G = new jsnx.DiGraph();
G.addNodesFrom(["StateMachine: [fsm]","AbstractState: [fsm]","State: [fsm]","Transition: [fsm]","Trigger: [fsm]","Pseudostate: [fsm]","PseudostateKind: [fsm]","FinalState: [fsm]","Statement: [fsm]","Program: [fsm]","Conditional: [fsm]","Loop: [fsm]","VarDecl: [fsm]","ConsoleOutput: [fsm]","Println: [fsm]","Print: [fsm]","Assignation: [fsm]","Wait: [fsm]","NamedElement: [fsm]","Constraint: [fsm, flowchart]","RelationalConstraint: [fsm, flowchart]","VarReference: [fsm, flowchart]","Expression: [fsm, kmLogo, flowchart]","Literal: [fsm, kmLogo, flowchart]","IntegerLit: [fsm, kmLogo, flowchart]","StringLit: [fsm, kmLogo, flowchart]","BoolLit: [fsm, kmLogo, flowchart]","ArithmeticExpression: [fsm, kmLogo, flowchart]","ArithmeticOperator: [fsm, kmLogo, flowchart]","RelationalExpression: [fsm, kmLogo, flowchart]","RelationalOperator: [fsm, kmLogo, flowchart]","LogoProgram: [kmLogo]","Instruction: [kmLogo]","Primitive: [kmLogo]","Back: [kmLogo]","Forward: [kmLogo]","Left: [kmLogo]","Right: [kmLogo]","PenDown: [kmLogo]","PenUp: [kmLogo]","Clear: [kmLogo]","ProcCall: [kmLogo]","ProcDeclaration: [kmLogo]","Block: [kmLogo]","If: [kmLogo]","ControlStructure: [kmLogo]","Repeat: [kmLogo]","While: [kmLogo]","Parameter: [kmLogo]","ParameterCall: [kmLogo]","Flowchart: [flowchart]","Node: [flowchart]","Arc: [flowchart]","Subflow: [flowchart]","Action: [flowchart]","Decision: [flowchart]"], {group:0});

G.addEdgesFrom([["StateMachine: [fsm]","AbstractState: [fsm]"],["StateMachine: [fsm]","Transition: [fsm]"],["AbstractState: [fsm]","Transition: [fsm]"],["AbstractState: [fsm]","Transition: [fsm]"],["State: [fsm]","Program: [fsm]"],["State: [fsm]","Program: [fsm]"],["State: [fsm]","Program: [fsm]"],["Transition: [fsm]","Trigger: [fsm]"],["Transition: [fsm]","AbstractState: [fsm]"],["Transition: [fsm]","AbstractState: [fsm]"],["Transition: [fsm]","Statement: [fsm]"],["Transition: [fsm]","Constraint: [fsm, flowchart]"],["Pseudostate: [fsm]","PseudostateKind: [fsm]"],["Program: [fsm]","Statement: [fsm]"],["Conditional: [fsm]","Expression: [fsm, kmLogo, flowchart]"],["Conditional: [fsm]","Program: [fsm]"],["Loop: [fsm]","Expression: [fsm, kmLogo, flowchart]"],["Loop: [fsm]","Program: [fsm]"],["VarDecl: [fsm]","Expression: [fsm, kmLogo, flowchart]"],["Assignation: [fsm]","VarDecl: [fsm]"],["Assignation: [fsm]","Expression: [fsm, kmLogo, flowchart]"],["RelationalConstraint: [fsm, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["ArithmeticExpression: [fsm, kmLogo, flowchart]","ArithmeticOperator: [fsm, kmLogo, flowchart]"],["ArithmeticExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["ArithmeticExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["RelationalExpression: [fsm, kmLogo, flowchart]","RelationalOperator: [fsm, kmLogo, flowchart]"],["RelationalExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["RelationalExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["LogoProgram: [kmLogo]","Instruction: [kmLogo]"],["Back: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["Forward: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["Left: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["Right: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["ProcCall: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["ProcCall: [kmLogo]","ProcDeclaration: [kmLogo]"],["ProcDeclaration: [kmLogo]","Parameter: [kmLogo]"],["ProcDeclaration: [kmLogo]","ProcCall: [kmLogo]"],["ProcDeclaration: [kmLogo]","Instruction: [kmLogo]"],["Block: [kmLogo]","Instruction: [kmLogo]"],["If: [kmLogo]","Block: [kmLogo]"],["If: [kmLogo]","Block: [kmLogo]"],["ControlStructure: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["Repeat: [kmLogo]","Block: [kmLogo]"],["While: [kmLogo]","Block: [kmLogo]"],["ParameterCall: [kmLogo]","Parameter: [kmLogo]"],["Flowchart: [flowchart]","Node: [flowchart]"],["Flowchart: [flowchart]","Arc: [flowchart]"],["Node: [flowchart]","Arc: [flowchart]"],["Node: [flowchart]","Arc: [flowchart]"],["Arc: [flowchart]","Node: [flowchart]"],["Arc: [flowchart]","Node: [flowchart]"],["Decision: [flowchart]","Constraint: [fsm, flowchart]"],["StateMachine: [fsm]","NamedElement: [fsm]"],["AbstractState: [fsm]","NamedElement: [fsm]"],["State: [fsm]","AbstractState: [fsm]"],["Transition: [fsm]","NamedElement: [fsm]"],["Pseudostate: [fsm]","AbstractState: [fsm]"],["FinalState: [fsm]","State: [fsm]"],["Program: [fsm]","Statement: [fsm]"],["Conditional: [fsm]","Statement: [fsm]"],["Loop: [fsm]","Statement: [fsm]"],["VarDecl: [fsm]","Statement: [fsm]"],["ConsoleOutput: [fsm]","Statement: [fsm]"],["Println: [fsm]","ConsoleOutput: [fsm]"],["Print: [fsm]","ConsoleOutput: [fsm]"],["Assignation: [fsm]","Statement: [fsm]"],["Wait: [fsm]","Statement: [fsm]"],["RelationalConstraint: [fsm, flowchart]","Constraint: [fsm, flowchart]"],["VarReference: [fsm, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["Literal: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["IntegerLit: [fsm, kmLogo, flowchart]","Literal: [fsm, kmLogo, flowchart]"],["StringLit: [fsm, kmLogo, flowchart]","Literal: [fsm, kmLogo, flowchart]"],["BoolLit: [fsm, kmLogo, flowchart]","Literal: [fsm, kmLogo, flowchart]"],["ArithmeticExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["RelationalExpression: [fsm, kmLogo, flowchart]","Expression: [fsm, kmLogo, flowchart]"],["Primitive: [kmLogo]","Instruction: [kmLogo]"],["Back: [kmLogo]","Primitive: [kmLogo]"],["Forward: [kmLogo]","Primitive: [kmLogo]"],["Left: [kmLogo]","Primitive: [kmLogo]"],["Right: [kmLogo]","Primitive: [kmLogo]"],["PenDown: [kmLogo]","Primitive: [kmLogo]"],["PenUp: [kmLogo]","Primitive: [kmLogo]"],["Clear: [kmLogo]","Primitive: [kmLogo]"],["ProcCall: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["ProcDeclaration: [kmLogo]","Instruction: [kmLogo]"],["Block: [kmLogo]","Instruction: [kmLogo]"],["If: [kmLogo]","ControlStructure: [kmLogo]"],["ControlStructure: [kmLogo]","Instruction: [kmLogo]"],["Repeat: [kmLogo]","ControlStructure: [kmLogo]"],["While: [kmLogo]","ControlStructure: [kmLogo]"],["Parameter: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["ParameterCall: [kmLogo]","Expression: [fsm, kmLogo, flowchart]"],["Subflow: [flowchart]","Flowchart: [flowchart]"],["Subflow: [flowchart]","Node: [flowchart]"],["Action: [flowchart]","Node: [flowchart]"],["Decision: [flowchart]","Node: [flowchart]"]]);