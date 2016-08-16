package com.botlogic.audio;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AudioRecorder {

	private final static Logger log = LogManager.getLogger();
	private final static AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
	public final static float SAMPLE_RATE = 16000;
	private final static int SAMPLE_SIZE_BITS = 16;
	private final static int CHANNELS = 1;
	private final static boolean SIGNED = true;
	private final static boolean BIG_ENDIAN = true;
	private final AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
	private final TargetDataLine microphone;
	private final File dest;
	private final int seconds;
	
	private AudioRecorder(File dest, int seconds) throws LineUnavailableException{
		AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
		microphone = AudioSystem.getTargetDataLine(format);
		this.dest = dest;
		this.seconds = seconds;
	}
	
	public void record() throws LineUnavailableException, IOException{
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(()-> {
			try {
				for(int i=seconds-1;i>=0;i--){
					Thread.sleep(1000);
					log.debug("Second "+i);
				}
				stop();
			} catch (Exception e) {
				log.error("Timeout", e);
			}
		});
		try(AudioInputStream ais = new AudioInputStream(microphone)){
			microphone.open(format);
			microphone.start();
			log.info("Microphone openned");
			AudioSystem.write(ais, FILE_TYPE, dest);
		}
	}
	
	private void stop(){
		microphone.stop();
		microphone.close();
		log.info("Microphone closed");
	}

	public static void printInfo() throws LineUnavailableException {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getSourceLineInfo();
			for (Line.Info lineInfo : lineInfos) {
				log.info(info.getName() + "---" + lineInfo);
				Line line = m.getLine(lineInfo);
				log.info("\t-----" + line);
			}
			lineInfos = m.getTargetLineInfo();
			for (Line.Info lineInfo : lineInfos) {
				log.info(m + "---" + lineInfo);
				Line line = m.getLine(lineInfo);
				log.info("\t-----" + line);
			}

		}
	}
	
	public static AudioRecorder create(File dest, int seconds) throws LineUnavailableException{
		AudioRecorder recorder = new AudioRecorder(dest, seconds);
		return recorder;
	}

}
