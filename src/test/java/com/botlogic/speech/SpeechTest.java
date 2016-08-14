package com.botlogic.speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;

public class SpeechTest {
	
	private final Logger log = LogManager.getLogger();
	private final Client client;
	
	public SpeechTest(){
		client = ClientBuilder.newBuilder().build().register(new LoggingFeature(new java.util.logging.Logger("filter", "config"){
			@Override
			public void log(Level level, String msg){
				log.debug(msg);
			}
		}, LoggingFeature.Verbosity.PAYLOAD_ANY));
	}
	
	@Test
	public void test() throws IllegalAccessException, FileNotFoundException, IOException{
		new SpeechSync(client).getFromFile(loadFile("test.m4a"));
	}
	
	private File loadFile(String fileName){
		File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		return file;
	}
	
}
