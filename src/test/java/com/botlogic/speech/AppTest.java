package com.botlogic.speech;

import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	private final ClientConfig clientConfig;
	
	public AppTest(){
		clientConfig = new ClientConfig();
		clientConfig.register(LoggingFeature.class);
		clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
	}
	
	@Test
	public void test() throws IllegalAccessException{
		new SpeechSync(ClientBuilder.newClient(clientConfig)).getFromFile(null);
	}
	
}
