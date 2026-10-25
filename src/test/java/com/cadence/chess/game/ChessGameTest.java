
package com.cadence.chess.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {

    @Test
    void startsWithTwentyLegalMoves() throws Exception {
        ChessGame game = new ChessGame();

        assertEquals(20, game.getLegalMoves().size());
    }

    @Test
    void acceptsLegalMove() throws Exception {
        ChessGame game = new ChessGame();

        assertTrue(game.makeMove("e2e4"));
        assertEquals(20, game.getLegalMoves().size());
    }

    @Test
    void rejectsIllegalMove() throws Exception {
        ChessGame game = new ChessGame();
        String originalPosition = game.getPosition();

        assertFalse(game.makeMove("e2e5"));
        assertEquals(originalPosition, game.getPosition());
    }

    @Test
    void rejectsMovingOpponentsPieceOutOfTurn() throws Exception {
        ChessGame game = new ChessGame();

        assertFalse(game.makeMove("e7e5"));
        assertEquals(20, game.getLegalMoves().size());
    }

}

