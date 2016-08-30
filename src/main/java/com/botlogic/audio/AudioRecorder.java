package com.botlogic.audio;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class AudioRecorder implements Runnable, AutoCloseable {

	private final static Logger log = LogManager.getLogger();
	private final File dest;
	private final BlockingQueue<File> queue = new ArrayBlockingQueue<>(20);
	private volatile boolean running = true;
	private final AudioFileListener listener;
	private final IMicropone microphone;
	private final long millisChunkRecording;
	
	private AudioRecorder(File dest, long millisChunkRecording, AudioFileListener listener) throws LineUnavailableException{
		this.dest = dest;
		this.listener = listener;
		this.microphone = new Microphone(millisChunkRecording);
		this.millisChunkRecording = millisChunkRecording;
	}
	
	public static AudioRecorder create(File dest, long millisChunkRecording, AudioFileListener listener) throws LineUnavailableException{
		AudioRecorder recorder = new AudioRecorder(dest, millisChunkRecording, listener);
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
			executor.execute(() -> {
				while(running){
					long millis = System.currentTimeMillis();
					File audio = microphone.get();
					boolean inserted = queue.offer(audio);
					if(!inserted){
						log.warn("Can not save audio "+audio.getAbsolutePath());
						audio.delete();
					}
					long total = System.currentTimeMillis() - millis;
					log.debug("Lost audio millis: "+(total - millisChunkRecording));
				}
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
	
	private class ConsumeAudio {
		
		private int numberOfAudios = 0;
		
		public void accept(File newAudio) throws IOException, UnsupportedAudioFileException {
			if(IMicropone.FAILED_AUDIO != newAudio){
				byte[] chunk = FileUtils.readFileToByteArray(newAudio);
				int volume = AudioUtils.getMaxAvg(chunk, 1);
				if(isWantedAudio(volume)){
					try(AudioInputStream audioMerged = createCombinedInputStream(newAudio, dest)){
						dest.delete();
						AudioSystem.write(audioMerged, AudioFileFormat.Type.WAVE, dest);
						numberOfAudios++;
					}
				}else if(dest.length() > 0){
					log.debug("Audios in file: "+numberOfAudios);
					notifyAudio(dest);
					dest.delete();
					numberOfAudios = 0;
				}
				newAudio.delete();
			}else{
				notifyAudio(newAudio);
			}
		}
	}
	
	private void notifyAudio(File file){
		Boolean continueRecording = listener.apply(file);
		if(!continueRecording)
			stopRun();
	}

	@Override
	public void close() throws Exception {
		microphone.close();
	}

}
