package com.botlogic.speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.botlogic.speech.SpeechDtoIn.RecognitionAudio;
import com.botlogic.speech.SpeechDtoIn.RecognitionConfig;
import com.botlogic.speech.SpeechDtoOut.Alternative;

public class SpeechSync {

	private final Logger log = LogManager.getLogger(); 
	private final Client client;
	private final ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	public SpeechSync(Client client){
		this.client = client;
	}
	
	public String obtainTextV1beta(File file, String languageCode) throws IllegalAccessException, FileNotFoundException, IOException{
		SpeechDtoIn in = new SpeechDtoIn();
		RecognitionConfig config = new RecognitionConfig();
		config.setLanguageCode(languageCode);
		in.setConfig(config);
		in.setAudio(RecognitionAudio.create(file));
//		log.debug("Converting to text: "+file.getAbsolutePath());
		Response response = client.target(bundle.getString("speech.url.v1beta")).queryParam("key", bundle.getString("speech.key")).request().post(Entity.entity(in, MediaType.APPLICATION_JSON_TYPE));
		return readResponse(response);
	}
	
	private String readResponse(Response response) throws IllegalAccessException{
		if(response.getStatus() == 200){
			SpeechDtoOut out = response.readEntity(SpeechDtoOut.class);
			if(out.getResults().size() > 0){
				Alternative alternative = out.getResults().get(0);
				if(alternative.getAlternatives().size() > 0){
					String text = alternative.getAlternatives().get(0).getTranscript();
					log.info("Text: "+text);
					return text;
				}
			}else{
				log.warn("No text found in the audio");
			}
		}else{
			throw new IllegalAccessException("Errors in the request: "+debugResponse(response));
		}
		return null;
	}
	
	private String debugResponse(Response response){
		return "Response: "+response+". Content: "+response.readEntity(Object.class);
		
	}
	
}
