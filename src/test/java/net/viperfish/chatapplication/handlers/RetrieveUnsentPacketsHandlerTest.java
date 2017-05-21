package net.viperfish.chatapplication.handlers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;

public class RetrieveUnsentPacketsHandlerTest {
	private static UserDatabase db;

	static {
		db = new RAMUserDatabase();
		User u = new User();
		u.setUsername("test");
		u.getUnsentMessages().add(new LSPayload());
		u.getUnsentMessages().add(new LSPayload());
		db.save(u);
	}

	@Test
	public void testSendUnsent() {
		RetrieveUnsentPacketsHandler handler = new RetrieveUnsentPacketsHandler(db);
		LSRequest req = new LSRequest();
		req.setSource("test");
		req.setType(LSRequest.LS_RETRIEVE_UNSENT);

		List<LSPayload> payloads = new LinkedList<>();

		LSResponse resp = handler.handleRequest(req, payloads);
		Assert.assertEquals(LSResponse.SUCCESS, resp.getStatus());
		Assert.assertEquals(2, payloads.size());
	}
}
