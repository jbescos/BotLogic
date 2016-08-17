package com.botlogic.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TextAnalizerTest {

	private final TextAnalyzer analyzer = new TextAnalyzer();
	
	@Test
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
	
}
