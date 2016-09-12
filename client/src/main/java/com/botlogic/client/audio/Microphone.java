package com.botlogic.client.audio;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Microphone implements IMicropone{

	private final static Logger log = LogManager.getLogger();
	private final AudioInputStream ais;
	private final TargetDataLine microphone;
	private final long millisRecord;
	private final Executor executor = Executors.newSingleThreadExecutor();
	private final MicrophoneEventListener listener;
	
	public Microphone(long millisRecord, MicrophoneEventListener listener) throws LineUnavailableException{
		this.microphone = AudioSystem.getTargetDataLine(FORMAT);
		this.ais = new AudioInputStream(microphone);
		this.millisRecord = millisRecord;
		this.microphone.open(FORMAT);
		this.listener = listener;
	}
	
	public Microphone(long millisRecord) throws LineUnavailableException{
		this(millisRecord, null);
	}
	
	@Override
	public File get() {
		try {
			microphone.start();
//			log.debug("Start recording ----------------------"+System.currentTimeMillis());
			File tmp = File.createTempFile("ztmp_chunk", ".wav");
			executor.execute(()-> {
				try {
					sleep();
				} catch (Exception e) {
					log.error("Unexpected exception", e);
				} finally {
					microphone.stop();
				}
			});
			AudioSystem.write(ais, FILE_TYPE, tmp);
//			log.debug("Stop recording ----------------------"+System.currentTimeMillis());
			microphone.flush();
			tmp.deleteOnExit();
			return tmp;
		} catch (Exception e) {
			log.error("Can not record the audio", e);
			return FAILED_AUDIO;
		}
		
	}

	@Override
	public void close() throws Exception {
		microphone.close();
		ais.close();
		log.info("Closing resources");
	}

	protected void sleep() throws InterruptedException {
		if(listener != null){
			long sleepTime = millisRecord - listener.getAlmostEndMillis();
			Thread.sleep(sleepTime);
			listener.almostEnd();
			Thread.sleep(listener.getAlmostEndMillis());
		}else{
			Thread.sleep(millisRecord);
		}
	}
	
}
