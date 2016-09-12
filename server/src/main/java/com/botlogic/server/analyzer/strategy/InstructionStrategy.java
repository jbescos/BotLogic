package com.botlogic.server.analyzer.strategy;

import java.io.IOException;

@FunctionalInterface
public interface InstructionStrategy<T> {

	T createInstruction(String sentence) throws IOException;
	
}
