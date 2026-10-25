
package com.cadence.chess.game;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameRoomService {

    private final ConcurrentHashMap<String, Set<WebSocketSession>> rooms =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, String> sessionRooms =
            new ConcurrentHashMap<>();

    public boolean joinRoom(String roomId, WebSocketSession session) {
        if (roomId == null || !roomId.matches("[a-zA-Z0-9_-]{1,40}")) {
            return false;
        }

        String currentRoom = sessionRooms.get(session.getId());

        if (currentRoom != null) {
            return currentRoom.equals(roomId);
        }

        Set<WebSocketSession> players =
                rooms.computeIfAbsent(
                        roomId, id -> ConcurrentHashMap.newKeySet());

        synchronized (players) {
            // For this first milestone, a room holds at most two players.
            if (players.size() >= 2) {
                return false;
            }

            players.add(session);
            sessionRooms.put(session.getId(), roomId);
        }

        return true;
    }

    public void broadcast(WebSocketSession sender, String message)
            throws IOException {

        String roomId = sessionRooms.get(sender.getId());

        if (roomId == null) {
            return;
        }

        Set<WebSocketSession> players = rooms.get(roomId);

        if (players == null) {
            return;
        }

        for (WebSocketSession player : players) {
            if (player.isOpen()) {
                synchronized (player) {
                    if (player.isOpen()) {
                        player.sendMessage(new TextMessage(message));
                    }
                }
            }
        }
    }

    public void leaveRoom(WebSocketSession session) {
        String roomId = sessionRooms.remove(session.getId());

        if (roomId == null) {
            return;
        }

        Set<WebSocketSession> players = rooms.get(roomId);

        if (players == null) {
            return;
        }

        synchronized (players) {
            players.remove(session);

            if (players.isEmpty()) {
                rooms.remove(roomId, players);
            }
        }
    }
}

