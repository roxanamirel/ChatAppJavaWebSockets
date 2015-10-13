package com.chat.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatServerEndpoint")
public class ChatServerEndpoint {

	private static Set<Session> chatUsers = Collections
			.synchronizedSet(new HashSet<Session>());
	private static final String AUTO_GEN_USERNAME = "user ";
	private static final String CONNECT_MESSAGE = "Connected as: ";
	private static final String NEW_USERNAME = "newusername";
	private static final String CHANGED_USERNAME = "New username: ";

	@OnOpen
	public void handleOpen(Session userSession) throws IOException {
		chatUsers.add(userSession);

		// generate a username that contains the last 5 characters in the user
		// session id
		int beginIndex = userSession.getId().length() - 5;
		int endIndex = userSession.getId().length();
		String generatedUserName = AUTO_GEN_USERNAME
				+ userSession.getId().substring(beginIndex, endIndex);
		userSession.getUserProperties().put("username", generatedUserName);
		userSession.getBasicRemote().sendText(
				buildMessage(CONNECT_MESSAGE, generatedUserName));
	}

	@OnMessage
	public void handleMessage(String message, Session userSession) {
		String username = (String) userSession.getUserProperties().get(
				"username");
		try {
			String[] parts = message.split("<>");
			if (parts.length == 2 && parts[0].equalsIgnoreCase(NEW_USERNAME)) {

				userSession.getUserProperties().put("username", parts[1]);
				userSession.getBasicRemote().sendText(
						buildMessage(CHANGED_USERNAME, parts[1]));
			} else {
				Iterator<Session> iterator = chatUsers.iterator();
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote()
							.sendText(buildMessage(username, message));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	@OnClose
	public void handleClose(Session userSession) {
		chatUsers.remove(userSession);
	}

	private String buildMessage(String username, String message) {
		JsonObject json = Json.createObjectBuilder()
				.add("message", username + ": " + message).build();
		StringWriter writer = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(writer)) {
			jsonWriter.write(json);
		}

		return writer.toString();
	}

}
