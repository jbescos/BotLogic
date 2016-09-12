package com.botlogic.server.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.botlogic.client.audio.Main;
import com.botlogic.client.rest.DtoOut;
import com.botlogic.server.utils.FileUtils;

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
	public void orderMovement() {
		Map<String, Set<String>> instruction = null;
		instruction = verifyOutputs("order.movement", "Move forward 1 meter");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "move"));
		assertTrue(instruction.toString(), Main.contains(instruction, "units", "meter"));
		assertTrue(instruction.toString(), Main.contains(instruction, "direction", "forward"));
		assertTrue(instruction.toString(), Main.contains(instruction, "amount", "1"));
		
		instruction = verifyOutputs("order.movement", "Turn 50 degrees to the left");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "turn"));
		assertTrue(instruction.toString(), Main.contains(instruction, "units", "degrees"));
		assertTrue(instruction.toString(), Main.contains(instruction, "direction", "left"));
		assertTrue(instruction.toString(), Main.contains(instruction, "amount", "50"));
	}
	
	@Test
	public void orderExecute() {
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("order.execute", "Exit the program");
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "exit"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "program"));
		
		instruction = verifyOutputs("order.execute", "Finish application");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "finish"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "application"));
		
		instruction = verifyOutputs("order.execute", "Switch on the pin 5");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "on"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "pin"));
		assertTrue(instruction.toString(), Main.contains(instruction, "arguments", "5"));
		
		instruction = verifyOutputs("order.execute", "Open the browser");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "open"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "browser"));
		
		instruction = verifyOutputs("order.execute", "Run this: apt-get update");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "run"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "apt-get", "update"));
		
		instruction = verifyOutputs("order.execute", "Update the time to 11:00");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "update"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "time"));
		assertTrue(instruction.toString(), Main.contains(instruction, "arguments", "11:00"));
		
		instruction = verifyOutputs("order.execute", "execute program Marriott");
		assertTrue(instruction.toString(), Main.contains(instruction, "action", "execute"));
		assertTrue(instruction.toString(), Main.contains(instruction, "program", "marriott"));
	}
	
	@Test
	public void questionTime() {
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.time", "What time is it now?");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "time"));
		assertTrue(instruction.toString(), Main.contains(instruction, "descriptor", "now"));
		
		instruction = verifyOutputs("question.time", "What time is it in China?.");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "time"));
		assertTrue(instruction.toString(), Main.contains(instruction, "descriptor", "china"));
	}
	
	@Test
	public void questionLocation() {
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.location", "Where is the city of Madrid?");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "madrid"));
		assertTrue(instruction.toString(), Main.contains(instruction, "location", "city"));
		
		instruction = verifyOutputs("question.location", "Where is the street Slavojova?.");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "slavojova"));
		assertTrue(instruction.toString(), Main.contains(instruction, "location", "street"));
		
		instruction = verifyOutputs("question.location", "Where is Spain");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "spain"));
		
		instruction = verifyOutputs("question.location", "What is the location of Spain");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "spain"));
	}
	
	@Test
	public void questionMeaning() {
		Pattern pattern = Pattern.compile("(?i)meaning*");
		assertTrue(pattern.matcher("meaning").find());
		assertTrue(pattern.matcher("Meaning").find());
		assertTrue(pattern.matcher("meanings").find());
		assertTrue(pattern.matcher("Meanings").find());
		assertFalse(pattern.matcher("eaning").find());
		Map<String, Set<String>> instruction = null;
		
		instruction = verifyOutputs("question.meaning", "What is the meaning of sugar?");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "sugar"));
		assertTrue(instruction.toString(), Main.contains(instruction, "descriptor", "meaning"));
		
		instruction = verifyOutputs("question.meaning", "Who is Newton?");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "newton"));
		
		instruction = verifyOutputs("question.meaning", "What is the meaning of Mars?");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "mars"));
		assertTrue(instruction.toString(), Main.contains(instruction, "descriptor", "meaning"));
		
		instruction = verifyOutputs("question.meaning", "what's the meaning of life");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "life"));
		assertTrue(instruction.toString(), Main.contains(instruction, "descriptor", "meaning"));
		
		instruction = verifyOutputs("question.meaning", "what does it mean chair");
		assertTrue(instruction.toString(), Main.contains(instruction, "search", "chair"));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Set<String>> verifyOutputs(String category, String sentence){
		Map<String, Set<String>> result = null;
		try{
			List<DtoOut<?>> response = process.process(sentence);
			log.debug("Response: "+response);
			assertEquals(1, response.size());
			assertEquals("Category: "+category+" Sentence: "+sentence, category, response.get(0).getCategory());
			result = (Map<String, Set<String>>) response.get(0).getInstruction();
		}catch(Exception e){
			log.error("Unexpected error", e);
			fail(e.getMessage());
		}
		return result;
	}
	
}
