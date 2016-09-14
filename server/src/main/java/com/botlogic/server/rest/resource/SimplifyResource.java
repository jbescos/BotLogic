package com.botlogic.server.rest.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.botlogic.client.audio.Languages;
import com.botlogic.client.rest.DtoOut;
import com.botlogic.server.analyzer.ProcessResponse;
import com.botlogic.server.speech.SpeechSync;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Path(SimplifyResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SimplifyResource {

	private final static Logger log = LogManager.getLogger();
	static final String PATH = "/simplify";
	private final ProcessResponse process;
	private final Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).property(ClientProperties.READ_TIMEOUT, 20000).property(ClientProperties.CONNECT_TIMEOUT, 20000).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINEST").build();
	private final SpeechSync speech = new SpeechSync(client);
	
	@Inject
	public SimplifyResource(ProcessResponse process) {
		this.process = process;
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadAudioFile(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileMetaData) throws IOException, IllegalAccessException {
		File audio = File.createTempFile(fileMetaData.getName(), fileMetaData.getType());
		try(OutputStream out = new FileOutputStream(audio)){
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = fileInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		}
		String text = speech.obtainTextV1beta(audio, Languages.EN_US);
		audio.delete();
		if(text != null){
			List<DtoOut<?>> response = process.process(text);
			return Response.ok(response).build();
		}else{
			return Response.ok(Collections.emptyList()).build();
		}
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@QueryParam("text") String text) throws IOException{
		List<DtoOut<?>> response = process.process(text);
		return Response.ok(response).build();
	}
	

}
