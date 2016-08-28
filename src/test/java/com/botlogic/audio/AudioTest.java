package com.botlogic.audio;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class AudioTest {

	private final Logger log = LogManager.getLogger();
	
	@Test
	@Ignore
	public void run() throws Exception{
		try(AudioRecorder audio = AudioRecorder.create(File.createTempFile("test", ".wav"), 5000, file -> {})){
			AudioRecorder.printInfo();
			audio.record();
		}
	}
	
	@Test
	@Ignore
	public void runForever() throws Exception{
		try(AudioRecorder audio = AudioRecorder.create(File.createTempFile("sequence", ".wav"), 1000, file -> log.debug("File: "+file.getAbsolutePath()))) {
			audio.run();
		} catch (LineUnavailableException | IOException e) {
			log.error("Unexpected error",  e);
			fail(e.getMessage());
		}
	}
	
}
