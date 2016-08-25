package com.botlogic.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
//	@Ignore
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
//	@Ignore
	public void chunk() throws InvalidFormatException, IOException{
		List<WordContent> words = analyzer.posTagger("What time is it?");
		assertEquals(words.toString(), 5, words.size());
		tagAndDebug("Move to the right");
		tagAndDebug("Switch on the pin number 8");
		tagAndDebug("How was the score of the last futbol match?");
	}
	
	private void tagAndDebug(String sentence) throws InvalidFormatException, IOException{
		long time1 = System.currentTimeMillis();
		List<WordContent> words = analyzer.posTagger(sentence);
		debugWords(words, time1, "Tagger ");
	}
	
	private void debugWords(List<WordContent> words, long time, String analyzer){
		List<String> print = words.stream().map(w->w.getWord()+"("+w.getTag()+")").collect(Collectors.toList());
		log.debug(analyzer+(System.currentTimeMillis() - time)+" millis: "+print);
	}
	
	@Test
//	@Ignore
	public void categorize() throws IOException{
		try{
			File fileModel = FileUtils.loadFileFromClasspath("newspapers.bin");
//			File fileModel = File.createTempFile("training", ".bin");
//			analyzer.trainCategorizer(FileUtils.loadFileFromClasspath("newspapers6517598969977471588.train"), fileModel);
			String category = analyzer.categorize("I would like to buy a motorcycle, where can I buy one?", fileModel);
			assertEquals("rec.motorcycles", category);
		}catch(Exception e){
			log.error("Error doing categorize", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	@Ignore
	public void createTrainingText() throws FileNotFoundException, IOException{
		File currentFile = new File("/home/jorge/Downloads/20news-bydate/20news-bydate-train/");
		File trainingText = File.createTempFile("newspapers", ".train");
		try(OutputStream fos = new FileOutputStream(trainingText); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));){
			analyzer.makeTrainingFile(currentFile, bw);
		}
	}
	
}
