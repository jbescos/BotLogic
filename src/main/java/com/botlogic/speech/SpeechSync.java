package com.botlogic.speech;

import java.io.File;
import java.util.ResourceBundle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeechSync {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Client client;
	private final ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	public SpeechSync(Client client){
		this.client = client;
	}
	
	public String getFromFile(File file) throws IllegalAccessException{
		SpeechDtoIn in = new SpeechDtoIn();
		Response response = client.target(bundle.getString("speech.url")).path("/v1beta1/speech:syncrecognize").queryParam("key", bundle.getString("speech.key")).request().post(Entity.entity(in, MediaType.APPLICATION_JSON_TYPE));
		if(response.getStatus() == 200){
			SpeechDtoOut out = response.readEntity(SpeechDtoOut.class);
			// TODO
			return out.toString();
		}else{
			throw new IllegalAccessException("Errors in the request "+response);
		}
	}
	
}
