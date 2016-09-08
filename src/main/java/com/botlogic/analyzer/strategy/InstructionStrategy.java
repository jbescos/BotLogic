package com.botlogic.analyzer.strategy;

import java.io.IOException;

@FunctionalInterface
public interface InstructionStrategy<T> {

	T createInstruction(String sentence) throws IOException;
	
}
