package com.botlogic.analyzer.strategy;

import java.io.IOException;

import com.botlogic.analyzer.TextAnalyzer;

public class VoidStrategy implements InstructionStrategy<Void>{

	@Override
	public Void createInstruction(TextAnalyzer analyzer, String sentence) throws IOException{
		return null;
	}

}
