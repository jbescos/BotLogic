package com.botlogic.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class ClientWS {

	private final Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).property(ClientProperties.READ_TIMEOUT, 20000).property(ClientProperties.CONNECT_TIMEOUT, 20000).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINEST").register(MultiPartFeature.class).build();
	private final ResourceBundle bundle = ResourceBundle.getBundle("botlogic");
	private final String URL = bundle.getString("server.url");
	private final String LOGIN = bundle.getString("login.user");
	private final String PASSWORD = bundle.getString("login.password");
	private final String LOGIN_URL = bundle.getString("login.url");
	private final String COOKIE_AUTH_NAME = "sharedDomainsCookie";
	private final int STATUS_OK = 200;
	private final int STATUS_UNAUTHORIZED = 401;
	private final int STATUS_FORBBIDEN = 403;
	private String authCookie;

	public List<DtoOut<Map<String, Set<String>>>> getFromAudio(File audio) throws IOException, IllegalAccessException {
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		FileDataBodyPart filePart = new FileDataBodyPart("file", audio);
		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);
		try {
			Builder builder = client.target(URL).request();
			if(authCookie != null)
				builder.cookie(COOKIE_AUTH_NAME, authCookie);
			Response response = builder.post(Entity.entity(multipart, multipart.getMediaType()));
			int code = response.getStatus();
			if(code == STATUS_OK){
				return response.readEntity(new GenericType<List<DtoOut<Map<String, Set<String>>>>>(){});
			}else if(code == STATUS_UNAUTHORIZED){
				login();
				return getFromAudio(audio);
			}else if(code == STATUS_FORBBIDEN){
				throw new IllegalAccessException("Not autorized");
			}
			throw new IllegalAccessException("Unexpected response code "+code+". "+response);
		} finally {
			formDataMultiPart.close();
			multipart.close();
		}
	}
	
	public List<DtoOut<Map<String, Set<String>>>> getFromText(String text){
		return client.target(URL).queryParam("text", text).request().get(new GenericType<List<DtoOut<Map<String, Set<String>>>>>(){});
	}
	
	public void login() throws IllegalAccessException {
		Response response = client.target(LOGIN_URL).queryParam("username", LOGIN).queryParam("password", PASSWORD).request().get();
		int code = response.getStatus();
		if(code != STATUS_OK){
			throw new IllegalAccessException(LOGIN+" doesn't exist. Create an user account."); 
		}
		authCookie = response.getCookies().get(COOKIE_AUTH_NAME).getValue();
	}

}
