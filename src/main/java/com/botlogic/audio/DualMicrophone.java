package com.botlogic.audio;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DualMicrophone implements IMicropone {

	private final static Logger log = LogManager.getLogger();
	private final int N_MICROPHONES = 2;
	private final IMicropone[] microphones;
	private int index = -1;
	private final BlockingQueue<File> queue = new ArrayBlockingQueue<>(10);
	private final MicrophoneEventListener listener;
	// No matter the number of microphones, 2 threads are always needed
	private final Executor executor = Executors.newFixedThreadPool(2);
	
	public DualMicrophone(long millisRecord, long almostEndMillis) throws LineUnavailableException{
		this.microphones = new IMicropone[N_MICROPHONES];
		this.listener = new EventListener(almostEndMillis);
		init(millisRecord);
	}
	
	private void init(long millisRecord) throws LineUnavailableException{
		for(int i=0; i<N_MICROPHONES;i++){
			microphones[i] = new Microphone(millisRecord, listener);
		}
	}
	
	@Override
	public File get() {
		try {
			if(index == -1)
				listener.almostEnd();
			return queue.take();
		} catch (Exception e) {
			log.error("Unexpected error. It can not record a new audio.", e);
			return FAILED_AUDIO;
		}
	}

	@Override
	public void close() throws Exception {
		index = 0;
		for(int i=0; i<N_MICROPHONES;i++){
			microphones[i].close();
		}
	}
	
	private class EventListener implements MicrophoneEventListener{

		private final long almostEndMillis;
		
		public EventListener(long almostEndMillis) {
			this.almostEndMillis = almostEndMillis;
		}
		
		@Override
		public void almostEnd() {
			executor.execute(() -> {
				synchronized (EventListener.this) {
					index++;
					if(index == N_MICROPHONES){
						index = 0;
					}
				}
				try {
					queue.put(microphones[index].get());
				} catch (Exception e) {
					log.error("Unexpected error. It can not record a new audio.", e);
					queue.offer(FAILED_AUDIO);
				}
			});
		}

		@Override
		public long getAlmostEndMillis() {
			return almostEndMillis;
		}
		
	}

}
