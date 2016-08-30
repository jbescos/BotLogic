package com.botlogic.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.botlogic.analyzer.strategy.InstructionStrategyFactory;
import com.botlogic.utils.FileUtils;

public class ProcessResponseTest {

	private final static Logger log = LogManager.getLogger();
	private final ProcessResponse process;
	
	public ProcessResponseTest() throws IOException{
		File trainingFile = FileUtils.loadFileFromClasspath("/training.txt");
		File fileModel = File.createTempFile("training", ".bin");
		new TextAnalyzer().trainCategorizer(trainingFile, fileModel);
		this.process = new ProcessResponse(fileModel);
	}
	
	@Test
	public void outputs() throws IOException{
		verifyOutputs(InstructionStrategyFactory.ORDER_MOVEMENT, "Move forward 1 meter");
		verifyOutputs(InstructionStrategyFactory.ORDER_MOVEMENT, "Turn 50 degrees to the left");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Exit the program");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Finish application");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Switch on the pin 5");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Open the browser");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Run this: apt-get update");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Update the time to 11:00");
		verifyOutputs(InstructionStrategyFactory.QUESTION_TIME, "What time is it?");
		verifyOutputs(InstructionStrategyFactory.QUESTION_TIME, "Is it noon?.");
		
	}
	
	private List<DtoOut<?>> verifyOutputs(String category, String sentence) throws IOException{
		List<DtoOut<?>> response = process.process(sentence);
		log.debug("Response: "+response);
		assertEquals(1, response.size());
		assertEquals("Category: "+category+" Sentence: "+sentence, category, response.get(0).getCategory());
		return response;
	}
	
}
