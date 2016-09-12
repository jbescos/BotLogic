package com.botlogic.server.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.botlogic.client.rest.ClientWS;
import com.botlogic.client.rest.DtoOut;

public class SimplyResourceTest extends JerseyTest{

	private final ClientWS ws = new ClientWS();
	
	@Test
	public void get(){
		List<DtoOut<Map<String, Set<String>>>> result = ws.getFromText("Open the browser");
		assertEquals(1, result.size());
		assertEquals("order.execute", result.get(0).getCategory());
	}

	@Override
	protected Application configure() {
		return new RestConfig();
	}
	
}
