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

public class SpeechSync {

	private final Logger log = LogManager.getLogger(); 
	private final Client client;
	private final ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	public SpeechSync(Client client){
		this.client = client;
	}
	
	public String getFromFile(File file) throws IllegalAccessException, FileNotFoundException, IOException, SpeechException{
		SpeechDtoIn in = new SpeechDtoIn();
		in.setConfig(new RecognitionConfig());
		in.setAudio(RecognitionAudio.create(file));
		log.debug("Sending: "+in);
		Response response = client.target(bundle.getString("speech.url")).path("/v1beta1/speech:syncrecognize").queryParam("key", bundle.getString("speech.key")).request().post(Entity.entity(in, MediaType.APPLICATION_JSON_TYPE));
		if(response.getStatus() == 200){
			SpeechDtoOut out = response.readEntity(SpeechDtoOut.class);
			if(out.getAlternatives().size() > 0){
				String text = out.getAlternatives().get(0).getTranscript();
				log.info("Text: "+text);
				return text;
			}else{
				throw new SpeechException("It didn't understand the audio. Response: "+response+" dto: "+out);
			}
		}else{
			throw new IllegalAccessException("Errors in the request: "+debugResponse(response));
		}
	}
	
	private String debugResponse(Response response){
		return "Response: "+response+". Content: "+response.readEntity(Object.class);
		
	}
	
}
