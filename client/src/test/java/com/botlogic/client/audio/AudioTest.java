package com.botlogic.client.audio;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.botlogic.client.audio.AudioRecorder;
import com.botlogic.client.audio.DualMicrophone;
import com.botlogic.client.audio.IMicropone;
import com.botlogic.client.audio.Microphone;

public class AudioTest {

	private final Logger log = LogManager.getLogger();
	
	@Test
	@Ignore
	public void run() throws Exception{
		try(IMicropone audio = new Microphone(5000)){
			File file = audio.get();
//			file.delete();
		}
	}
	
	@Test
	@Ignore
	public void runForever() throws Exception{
		try(AudioRecorder audio = AudioRecorder.create(File.createTempFile("sequence", ".wav"), file -> Boolean.TRUE, new DualMicrophone(1000, 10))) {
			audio.run();
		} catch (LineUnavailableException | IOException e) {
			log.error("Unexpected error",  e);
			fail(e.getMessage());
		}
	}
	
}
