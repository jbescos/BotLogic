package com.botlogic.analyzer.strategy;

import java.io.IOException;

import com.botlogic.analyzer.TextAnalyzer;

public interface InstructionStrategy<T> {

	T createInstruction(TextAnalyzer analyzer, String sentence) throws IOException;
	
}
