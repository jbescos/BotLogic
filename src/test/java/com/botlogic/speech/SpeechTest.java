package com.botlogic.speech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.sound.sampled.LineUnavailableException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Ignore;
import org.junit.Test;

import com.botlogic.analyzer.TextAnalyzer;
import com.botlogic.audio.AudioRecorder;
import com.botlogic.utils.FileUtils;

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
			String text = new SpeechSync(client).obtainTextV1beta(FileUtils.loadFileFromClasspath("test.wav"), Languages.EN_US);
			assertEquals("this is a test let's see", text);
		} catch (ProcessingException | IllegalAccessException | IOException | SpeechException e) {
			log.error("Unexpected error", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	@Ignore
	public void microToText() throws IOException, LineUnavailableException, IllegalAccessException, SpeechException{
		File file = File.createTempFile("test", ".wav");
		AudioRecorder audio = AudioRecorder.create(file, 10);
		audio.record();
		String text = new SpeechSync(client).obtainTextV1beta(file, Languages.EN_US);
		log.info(text);
		TextAnalyzer analyzer = new TextAnalyzer();
		Entry<Double,String> pair = analyzer.categorize(text, FileUtils.loadFileFromClasspath("newspapers.bin"));
		log.info(text+"\n Belongs to category: "+pair.getValue());
	}
	
}
