package com.botlogic.analyzer.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class InstructionStrategyFactory {

	private final static Map<String, InstructionStrategy<?>> STRATEGIES = new HashMap<>();
	public final static String QUESTION_TIME = "question.time";
	public final static String QUESTION_LOCATION = "question.location";
	public final static String ORDER_MOVEMENT = "order.movement";
	public final static String ORDER_EXECUTE = "order.execute";
	
	static{
		STRATEGIES.put(QUESTION_TIME, new QuestionTimeStrategy());
		STRATEGIES.put(QUESTION_LOCATION, new QuestionLocationStrategy());
		STRATEGIES.put(ORDER_MOVEMENT, new OrderMovementStrategy());
		STRATEGIES.put(ORDER_EXECUTE, new OrderExecuteStrategy());
	}
	
	public static InstructionStrategy<?> create(Entry<Double,String> entry){
		if(STRATEGIES.containsKey(entry.getValue())){
			return STRATEGIES.get(entry.getValue());
		}else{
			return new VoidStrategy();
		}
	}
	
}
