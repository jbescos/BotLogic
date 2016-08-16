package com.botlogic.speech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Ignore;
import org.junit.Test;

import com.botlogic.audio.AudioRecorder;

public class SpeechTest {
	
	private final Logger log = LogManager.getLogger();
	private final Client client;
	
	public SpeechTest(){
		client = ClientBuilder.newBuilder().property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINEST").build();
	}
	
	@Test
	@Ignore
	public void audioToText(){
		try {
			String text = new SpeechSync(client).obtainTextV1beta(loadFile("test.wav"));
			assertEquals("this is a test let's see", text);
		} catch (ProcessingException | IllegalAccessException | IOException | SpeechException e) {
			log.error("Unexpected error", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void microToText() throws IOException, LineUnavailableException, IllegalAccessException, SpeechException{
		File file = File.createTempFile("test", ".wav");
		AudioRecorder audio = AudioRecorder.create(file, 5);
		audio.record();
		String text = new SpeechSync(client).obtainTextV1beta(file);
		log.info(text);
	}
	
	private File loadFile(String fileName){
		File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		return file;
	}
	
}
