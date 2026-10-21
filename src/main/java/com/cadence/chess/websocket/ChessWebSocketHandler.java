package com.cadence.chess.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChessWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Player connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession sender, TextMessage message)
            throws IOException {

        String text = message.getPayload();

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                synchronized (session) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(text));
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) {

        sessions.remove(session);
        System.out.println("Player disconnected: " + session.getId());
    }
}