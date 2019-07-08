package edu.kit.informatik.pcc.service.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("webservice/publicKey")
public class KeyProviderServerProxy {
	@GET
	public String getPublicKey() {
		return WebService.getGlobal().getPublicKey().toString();
	}
}
