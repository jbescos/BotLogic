package com.botlogic.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.botlogic.analyzer.strategy.TextFileStrategy;
import com.botlogic.utils.FileUtils;

public class ProcessResponseTest {

	private final static Logger log = LogManager.getLogger();
	private final static ProcessResponse process;
	
	static{
		try {
			File trainingFile = FileUtils.loadFileFromClasspath("/training.txt");
			File fileModel = File.createTempFile("training", ".bin");
			new TextAnalyzer().trainCategorizer(trainingFile, fileModel);
			process = new ProcessResponse(fileModel);
		} catch (IOException e) {
			log.error("Unexpected error", e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	@Test
	public void orderMovement() throws IOException{
		Map<String, Set<String>> instruction = null;
		instruction = verifyOutputs("order.movement", "Move forward 1 meter");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "move"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "units", "meter"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "direction", "forward"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "amount", "1"));
		
		instruction = verifyOutputs("order.movement", "Turn 50 degrees to the left");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "turn"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "units", "degrees"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "direction", "left"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "amount", "50"));
	}
	
	@Test
	public void orderExecute() throws IOException{
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("order.execute", "Exit the program");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "exit"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "program"));
		
		instruction = verifyOutputs("order.execute", "Finish application");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "finish"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "application"));
		
		instruction = verifyOutputs("order.execute", "Switch on the pin 5");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "on"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "pin"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "arguments", "5"));
		
		instruction = verifyOutputs("order.execute", "Open the browser");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "open"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "browser"));
		
		instruction = verifyOutputs("order.execute", "Run this: apt-get update");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "run"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "apt-get", "update"));
		
		instruction = verifyOutputs("order.execute", "Update the time to 11:00");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "action", "update"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "program", "time"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "arguments", "11:00"));
	}
	
	@Test
	public void questionTime() throws IOException{
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.time", "What time is it now?");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "time"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "descriptor", "now"));
		
		instruction = verifyOutputs("question.time", "What time is it in China?.");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "time"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "descriptor", "china"));
	}
	
	@Test
	public void questionLocation() throws IOException{
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.location", "Where is the city of Madrid?");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "madrid"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "location", "city"));
		
		instruction = verifyOutputs("question.location", "Where is the street Slavojova?.");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "slavojova"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "location", "street"));
	}
	
	@Test
	public void questionMeaning() throws IOException{
		Pattern pattern = Pattern.compile("(?i)meaning*");
		assertTrue(pattern.matcher("meaning").find());
		assertTrue(pattern.matcher("Meaning").find());
		assertTrue(pattern.matcher("meanings").find());
		assertTrue(pattern.matcher("Meanings").find());
		assertFalse(pattern.matcher("eaning").find());
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.meaning", "What is the meaning of sugar?");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "sugar"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "descriptor", "meaning"));
		
		instruction = verifyOutputs("question.meaning", "Who is Newton?");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "newton"));
		
		instruction = verifyOutputs("question.meaning", "What is the meaning of Mars?");
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "search", "mars"));
		assertTrue(instruction.toString(), TextFileStrategy.contains(instruction, "descriptor", "meaning"));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Set<String>> verifyOutputs(String category, String sentence) throws IOException{
		List<DtoOut<?>> response = process.process(sentence);
		log.debug("Response: "+response);
		assertEquals(1, response.size());
		assertEquals("Category: "+category+" Sentence: "+sentence, category, response.get(0).getCategory());
		return (Map<String, Set<String>>) response.get(0).getInstruction();
	}
	
}
