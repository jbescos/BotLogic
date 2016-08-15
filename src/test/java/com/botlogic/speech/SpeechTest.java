package com.botlogic.speech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;

public class SpeechTest {
	
	private final Logger log = LogManager.getLogger();
	private final Client client;
	
	public SpeechTest(){
		client = ClientBuilder.newBuilder().property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINEST").build();
	}
	
	@Test
	public void test(){
		try {
			String text = new SpeechSync(client).obtainTextV1beta(loadFile("hello.wav"));
			assertEquals("hello Google", text);
		} catch (ProcessingException | IllegalAccessException | IOException | SpeechException e) {
			log.error("Unexpected error", e);
			fail(e.getMessage());
		}
	}
	
	private File loadFile(String fileName){
		File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		return file;
	}
	
}
