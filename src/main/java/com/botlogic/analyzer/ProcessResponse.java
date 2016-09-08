package com.botlogic.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.botlogic.analyzer.strategy.InstructionStrategy;
import com.botlogic.analyzer.strategy.TextFileStrategy;

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
			Entry<Double,String> entry = analyzer.categorize(text, categorizeModel);
			DtoOut<Object> dto = new DtoOut<>();
			dto.setProbability(entry.getKey());
			dto.setSentence(sentence);
			InstructionStrategy<?> strategy = new TextFileStrategy(entry.getValue(), analyzer);
			dto.setCategory(entry.getValue());
			dto.setInstruction(strategy.createInstruction(sentence));
			response.add(dto);
		}
		log.info(System.currentTimeMillis() - time + " millis");
		return response;
	}
	
}
