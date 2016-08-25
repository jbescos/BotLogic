package com.botlogic.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.botlogic.analyzer.strategy.InstructionStrategy;
import com.botlogic.analyzer.strategy.InstructionStrategyFactory;

public class ProcessResponse {

	private final static Logger log = LogManager.getLogger();
	private final TextAnalyzer analyzer = new TextAnalyzer();
	private final File categorizeModel;
	
	public ProcessResponse(File categorizeModel){
		this.categorizeModel = categorizeModel;
	}
	
	public List<DtoOut<?>> process(String text) throws IOException{
		long time = System.currentTimeMillis();
		List<DtoOut<?>> response = new ArrayList<>();
		String[] sentences = analyzer.splitSentences(text);
		for(String sentence : sentences){
			String category = analyzer.categorize(text, categorizeModel);
			InstructionStrategy<?> strategy = InstructionStrategyFactory.create(category);
			DtoOut<Object> dto = new DtoOut<>();
			dto.setCategory(category);
			dto.setSentence(sentence);
			dto.setInstruction(strategy.createInstruction(analyzer, sentence));
			response.add(dto);
		}
		log.info(System.currentTimeMillis() - time + " millis");
		return response;
	}
	
}
