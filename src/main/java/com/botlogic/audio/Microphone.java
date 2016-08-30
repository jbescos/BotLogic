package com.botlogic.audio;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Microphone implements Supplier<File>, AutoCloseable{

	private final static Logger log = LogManager.getLogger();
	public final static float SAMPLE_RATE = 16000;
	private final static int SAMPLE_SIZE_BITS = 16;
	private final static int CHANNELS = 1;
	private final static boolean SIGNED = true;
	private final static boolean BIG_ENDIAN = true;
	private final static AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
	private final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
	private final AudioInputStream ais;
	private final TargetDataLine microphone;
	private final long millisRecord;
	public final static File FAILED_AUDIO = new File("");
	
	public Microphone(long millisRecord) throws LineUnavailableException{
		this.microphone = AudioSystem.getTargetDataLine(FORMAT);
		this.ais = new AudioInputStream(microphone);
		this.millisRecord = millisRecord;
		this.microphone.open(FORMAT);
	}
	
	@Override
	public File get() {
		try {
			microphone.start();
			File tmp = File.createTempFile("ztmp_chunk", ".wav");
			Executor executor = Executors.newSingleThreadExecutor();
			executor.execute(()-> {
				try {
					Thread.sleep(millisRecord);
				} catch (Exception e) {
					log.error("Timeout", e);
				} finally {
					microphone.stop();
				}
			});
			AudioSystem.write(ais, FILE_TYPE, tmp);
			microphone.flush();
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

	
	
}
