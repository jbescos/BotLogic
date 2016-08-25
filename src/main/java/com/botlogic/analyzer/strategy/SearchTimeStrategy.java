package com.botlogic.analyzer.strategy;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.botlogic.analyzer.TextAnalyzer;
import com.botlogic.analyzer.WordContent;

public class SearchTimeStrategy implements InstructionStrategy<Date>{

	@Override
	public Date createInstruction(TextAnalyzer analyzer, String sentence) throws IOException{
		List<WordContent> words = analyzer.posTagger(sentence);
		return new Date();
	}

}
