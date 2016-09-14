package com.botlogic.client.audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.botlogic.client.rest.ClientWS;
import com.botlogic.client.rest.DtoOut;

public class Main {
	
	private final static Logger log = LogManager.getLogger();

	public static void main(String[] args) throws LineUnavailableException, Exception {
		File audio = File.createTempFile("audio", ".wav");
		try(AudioRecorder recorder = AudioRecorder.create(audio, new ParseText(), new DualMicrophone(1000, 100))){
			recorder.run();
		}
		log.info("Closing application");
		System.exit(0);
	}
	
	public static boolean contains(Map<String,Set<String>> instruction, String key, String ... values){
		if(instruction.containsKey(key)){
			Set<String> programs = instruction.get(key);
			for(String value : values){
				if(programs.contains(value)){
					return true;
				}
			}
		}
		return false;
	}
	
	private static class ParseText implements AudioFileListener {

		private final ClientWS client = new ClientWS();
		
		@Override
		public Boolean apply(File file) {
			log.debug("Receiving audio file "+file.getName());
			if(IMicropone.FAILED_AUDIO != file){
				try {
					List<DtoOut<Map<String,Set<String>>>> dtos = client.getFromAudio(file);
					log.debug(dtos.toString());
					if(dtos.size() > 0){
						DtoOut<Map<String,Set<String>>> dto = dtos.get(0);
						if("order.execute".equals(dto.getCategory())){
							return !contains(dto.getInstruction(), "program", "exit", "finalize", "finish");
						}
						return true;
					}
				} catch (Exception e) {
					log.error("Unexpected error, can not analyze", e);
				}
				return true;
			}else{
				return false;
			}
		}
		
	}

}
