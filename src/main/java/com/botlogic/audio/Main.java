package com.botlogic.audio;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.LineUnavailableException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;

import com.botlogic.analyzer.DtoOut;
import com.botlogic.analyzer.ProcessResponse;
import com.botlogic.analyzer.strategy.InstructionStrategyFactory;
import com.botlogic.analyzer.strategy.OrderExecuteStrategy;
import com.botlogic.speech.Languages;
import com.botlogic.speech.SpeechSync;
import com.botlogic.utils.FileUtils;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class Main {
	
	private final static Logger log = LogManager.getLogger();

	public static void main(String[] args) throws LineUnavailableException, Exception {
		File audio = File.createTempFile("audio", ".wav");
		try(AudioRecorder recorder = AudioRecorder.create(audio, 1000, new ParseText(), new DualMicrophone(1000, 10))){
			recorder.run();
		}
		log.info("Closing application");
		System.exit(0);
	}
	
	private static class ParseText implements AudioFileListener {

		private final ProcessResponse process;
		private final Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).property(ClientProperties.READ_TIMEOUT, 20000).property(ClientProperties.CONNECT_TIMEOUT, 20000).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINEST").build();
		private final SpeechSync speech = new SpeechSync(client);
		
		public ParseText() throws IOException{
			process = new ProcessResponse(FileUtils.loadFileFromClasspath("/trainingModel.bin"));
		}
		
		@Override
		public Boolean apply(File file) {
			if(IMicropone.FAILED_AUDIO != file){
				try {
					String text = speech.obtainTextV1beta(file, Languages.EN_US);
					if(text != null){
						List<DtoOut<?>> response = process.process(text);
						log.debug("RESPONSE: "+response);
						File copy = File.createTempFile("copy_"+text, ".wav");
						org.apache.commons.io.FileUtils.copyFile(file, copy);
						DtoOut<?> dto = response.get(0);
						if(InstructionStrategyFactory.ORDER_EXECUTE.equals(dto.getCategory())){
							@SuppressWarnings("unchecked")
							Map<String, Object> content = (Map<String, Object>)dto.getInstruction();
							if(content.containsKey(OrderExecuteStrategy.ACTION)){
								String actionValue = ((String) content.get(OrderExecuteStrategy.ACTION)).toLowerCase();
								return !"exit".equals(actionValue);
							}
						}
					}
				} catch (IllegalAccessException | IOException | ProcessingException e) {
					log.error("Unexpected error, can not analyze", e);
				}
				return true;
			}else{
				return false;
			}
		}
		
	}

}
