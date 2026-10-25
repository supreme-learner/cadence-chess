
package com.cadence.chess.websocket;

import com.cadence.chess.game.GameRoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class ChessWebSocketHandler extends TextWebSocketHandler {

    private final GameRoomService gameRoomService;

    public ChessWebSocketHandler(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession sender, TextMessage message)
            throws IOException {

        String text = message.getPayload();

        if (text.startsWith("JOIN ")) {
            String roomId = text.substring(5).trim();

            if (gameRoomService.joinRoom(roomId, sender)) {
                sender.sendMessage(new TextMessage(
                        "JOINED " + roomId));
            } else {
                sender.sendMessage(new TextMessage(
                        "ERROR Could not join room"));
            }

            return;
        }

        gameRoomService.broadcast(sender, text);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) {

        gameRoomService.leaveRoom(session);
        System.out.println("Disconnected: " + session.getId());
    }
}
