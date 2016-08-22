package com.botlogic.clasifier;

import static org.junit.Assert.fail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class NaiveBayesTest {

	private final static Logger log = LogManager.getLogger();
	
	@Test
	public void categorize(){
		try {
			NaiveBayes naive = new NaiveBayes();
			naive.trainIt();
			naive.classifyNewTweet("You sucks");
		} catch (Exception e) {
			log.error("Error running naive bayes", e);
			fail(e.getMessage());
		}
		
	}
	
}
