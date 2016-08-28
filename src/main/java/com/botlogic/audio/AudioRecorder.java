package com.botlogic.audio;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class AudioRecorder implements Runnable, AutoCloseable {

	private final static Logger log = LogManager.getLogger();
	private final static AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
	public final static float SAMPLE_RATE = 16000;
	private final static int SAMPLE_SIZE_BITS = 16;
	private final static int CHANNELS = 1;
	private final static boolean SIGNED = true;
	private final static boolean BIG_ENDIAN = true;
//	private final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SAMPLE_SIZE_BITS/8, 8, BIG_ENDIAN);
	private final AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
	private final TargetDataLine microphone;
	private final File dest;
	private final BlockingQueue<File> queue;
	private final long millisChunkRecording;
	private volatile boolean running = true;
	private final AudioFileListener listener;
	private final int HEAD_WAV_BYTES = 52;
	private final AudioInputStream ais;
	
	private AudioRecorder(File dest, long millisChunkRecording, AudioFileListener listener, BlockingQueue<File> queue) throws LineUnavailableException{
		this.microphone = AudioSystem.getTargetDataLine(format);
		this.dest = dest;
		this.millisChunkRecording = millisChunkRecording;
		this.listener = listener;
		this.queue = queue;
		this.ais = new AudioInputStream(microphone);
		this.microphone.open(format);
	}
	
	private int getAvgVolume(byte[] audioData){
		long lSum = 0;
	    for(int i=0; i<audioData.length; i++)
	        lSum = lSum + audioData[i];

	    double dAvg = lSum / audioData.length;

	    double sumMeanSquare = 0d;
	    for(int j=0; j<audioData.length; j++)
	        sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

	    double averageMeanSquare = sumMeanSquare / audioData.length;
	    return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5) - HEAD_WAV_BYTES;
	}
	
	public void record() throws LineUnavailableException, IOException{
		record(dest);
	}
	
	private void record(File newAudio) throws LineUnavailableException, IOException{
		microphone.start();
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(()-> {
			try {
				Thread.sleep(millisChunkRecording);
			} catch (Exception e) {
				log.error("Timeout", e);
			} finally {
				microphone.stop();
			}
		});
		AudioSystem.write(ais, FILE_TYPE, newAudio);
		microphone.flush();
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
	
	public static AudioRecorder create(File dest, long millisChunkRecording, AudioFileListener listener) throws LineUnavailableException{
		AudioRecorder recorder = new AudioRecorder(dest, millisChunkRecording, listener, new ArrayBlockingQueue<>(20));
		return recorder;
	}
	
	private AudioInputStream createCombinedInputStream(File newAudio, File oldAudio) throws UnsupportedAudioFileException, IOException{
		AudioInputStream newStream = AudioSystem.getAudioInputStream(newAudio);
		if(oldAudio.length() > 0){
			AudioInputStream oldStream = AudioSystem.getAudioInputStream(oldAudio);
			return new AudioInputStream(new SequenceInputStream(oldStream, newStream), newStream.getFormat(), newStream.getFrameLength() + oldStream.getFrameLength());
		}else{
			return newStream;
		}
	}

	@Override
	public void run() {
		try {
			log.info("Start listening");
			dest.delete();
			ConsumeAudio consumer = new ConsumeAudio();
			Executor executor = Executors.newSingleThreadExecutor();
			SupplyAudio supplier = new SupplyAudio();
			executor.execute(() -> {
				while(running)
					queue.add(supplier.get());
			});
			while(running){
				int size = queue.size();
				if(size > 10)
					log.warn("Queue size: "+queue.size());
				File newAudio = queue.take();
				consumer.accept(newAudio);
			}
		} catch (IOException | RuntimeException | UnsupportedAudioFileException | InterruptedException e) {
			log.error("The recording has finnished abruptely", e);
			stopRun();
		}
	}
	
	private boolean isWantedAudio(double volume){
//		log.debug("Volume: "+volume);
		return volume > 1;
	}
	
	public void stopRun(){
		running = false;
	}
	
	@FunctionalInterface
	private interface ThrowingConsumer<T> {

	    void acceptThrows(T elem) throws IOException;
	}
	
	private class SupplyAudio implements Supplier<File>{

		@SuppressWarnings("serial")
		@Override
		public File get() {
			try {
				File tmp = File.createTempFile("audio_recorder_chunk", ".wav");
				record(tmp);
				return tmp;
			} catch (IOException | LineUnavailableException e) {
				log.error("Unexpected error. Exiting SupplyAudio.", e);
				stopRun();
				queue.clear();
				return new File(""){};
			}
		}

	}
	
	private class ConsumeAudio {
		
		public void accept(File newAudio) throws IOException, UnsupportedAudioFileException {
			byte[] chunk = FileUtils.readFileToByteArray(newAudio);
			int volume = getAvgVolume(chunk);
			if(isWantedAudio(volume)){
				try(AudioInputStream audioMerged = createCombinedInputStream(newAudio, dest)){
					dest.delete();
					AudioSystem.write(audioMerged, AudioFileFormat.Type.WAVE, dest);
				}
			}else if(dest.length() > 0){
				listener.notify(dest);
				dest.delete();
			}
			newAudio.delete();
		}
	}

	@Override
	public void close() throws Exception {
		microphone.close();
		ais.close();
		log.info("Closing resources");
	}

}
