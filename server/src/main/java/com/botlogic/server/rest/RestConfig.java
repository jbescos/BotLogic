package com.botlogic.server.rest;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.botlogic.server.analyzer.ProcessResponse;
import com.botlogic.server.rest.resource.SimplifyResource;
import com.botlogic.server.utils.FileUtils;

public class RestConfig extends ResourceConfig {

	private final static Logger log = LogManager.getLogger();

	// For tests
	public RestConfig(Object... injections) {
		for (Object injection : injections) {
			register(injection);
		}
		packages(SimplifyResource.class.getPackage().getName());
		property(ServerProperties.TRACING, "ALL");
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
//		register(MvcFeature.class);
//		property(MvcFeature.TEMPLATE_BASE_PATH, "/");
		log.info("Jersey has been loaded");
	}

	public RestConfig() {
		this(new Binder());
	}

	public static class Binder extends AbstractBinder {

		@Override
		protected void configure() {
			try {
				// FIXME thread safe?
				ProcessResponse process = new ProcessResponse(FileUtils.loadFileFromClasspath("/trainingModel.bin"));
				bind(process).to(ProcessResponse.class);
			} catch (IOException e) {
				log.error("Can not load the model", e);
			}
		}

	}

}
