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
		File trainingFile = FileUtils.loadFileFromClasspath("training.txt");
		File fileModel = File.createTempFile("training", ".bin");
		new TextAnalyzer().trainCategorizer(trainingFile, fileModel);
		this.process = new ProcessResponse(fileModel);
	}
	
	@Test
	public void outputs() throws IOException{
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Move forward 1 meter");
		verifyOutputs(InstructionStrategyFactory.ORDER_EXECUTE, "Open the browser");
		verifyOutputs(InstructionStrategyFactory.QUESTION_TIME, "What time is it?");
		verifyOutputs(InstructionStrategyFactory.QUESTION_TIME, "Is it noon?.");
		
	}
	
	private void verifyOutputs(String category, String sentence) throws IOException{
		List<DtoOut<?>> response = process.process(sentence);
		log.debug("Response: "+response);
		assertEquals(1, response.size());
		assertEquals("Category: "+category+" Sentence: "+sentence, category, response.get(0).getCategory());
	}
	
}
