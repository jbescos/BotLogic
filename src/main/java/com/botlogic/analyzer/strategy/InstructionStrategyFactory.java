package com.botlogic.analyzer.strategy;

import java.util.HashMap;
import java.util.Map;

public class InstructionStrategyFactory {

	private final static Map<String, InstructionStrategy<?>> STRATEGIES = new HashMap<>();
	public final static String SEARCH_TIME = "search.time";
	public final static String SEARCH_LOCATION = "search.location";
	public final static String ORDER_MOVEMENT = "order.movement";
	public final static String ORDER_EXECUTE = "order.execute";
	
	static{
		STRATEGIES.put(SEARCH_TIME, new SearchTimeStrategy());
		STRATEGIES.put(SEARCH_LOCATION, new SearchLocationStrategy());
		STRATEGIES.put(ORDER_MOVEMENT, new OrderMovementStrategy());
		STRATEGIES.put(ORDER_EXECUTE, new OrderExecuteStrategy());
	}
	
	public static InstructionStrategy<?> create(String category){
		if(STRATEGIES.containsKey(category)){
			return STRATEGIES.get(category);
		}
		return new VoidStrategy();
	}
	
}
