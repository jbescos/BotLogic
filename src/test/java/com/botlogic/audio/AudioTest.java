package com.botlogic.audio;

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
	public void run() throws LineUnavailableException, InterruptedException, IOException{
		AudioRecorder audio = AudioRecorder.create(File.createTempFile("test", ".wav"), 5);
		AudioRecorder.printInfo();
		audio.record();
	}
	
}
