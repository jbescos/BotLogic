package com.botlogic.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import opennlp.tools.util.InvalidFormatException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.botlogic.utils.FileUtils;

public class TextAnalizerTest {

	private final TextAnalyzer analyzer = new TextAnalyzer();
	private final static Logger log = LogManager.getLogger();
	
	@Test
	@Ignore
	public void sentences() throws FileNotFoundException, IOException{
		final String SENTENCE_1 = "Hello this is a test. ";
		final String SENTENCE_2 = "Hello this is other test. ";
		final String SENTENCE_3 = "And this is the last one. ";
		String[] sentences = analyzer.splitSentences(SENTENCE_1+SENTENCE_2+SENTENCE_3);
		assertEquals(3, sentences.length);
		assertEquals(SENTENCE_1.trim(), sentences[0]);
		assertEquals(SENTENCE_2.trim(), sentences[1]);
		assertEquals(SENTENCE_3.trim(), sentences[2]);
	}
	
	@Test
	@Ignore
	public void chunk() throws InvalidFormatException, IOException{
		List<WordContent> words = analyzer.parseSentence("What time is it?");
		assertEquals(words.toString(), 4, words.size());
		debugWords(words);
		words = analyzer.parseSentence("Move to the right");
		debugWords(words);
		words = analyzer.parseSentence("Switch on the pin number 8");
		debugWords(words);
		words = analyzer.parseSentence("How was the score of the last futbol match?");
		debugWords(words);
	}
	
	private void debugWords(List<WordContent> words){
		List<String> print = words.stream().map(w->w.getWord()+"("+w.getTag()+")").collect(Collectors.toList());
		log.debug(print);
	}
	
	@Test
	public void categorize() throws IOException{
		try{
			File fileModel = File.createTempFile("training", ".bin");
			analyzer.trainCategorizer(FileUtils.loadFileFromClasspath("training.txt"), fileModel);
			String category = analyzer.categorize("I'm looking for Zaragoza", fileModel);
			assertEquals("search_location", category);
		}catch(Exception e){
			log.error("Error doing categorize", e);
			fail(e.getMessage());
		}
	}
	
}
