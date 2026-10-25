
package com.cadence.chess.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import java.util.List;

public class ChessGame {

    private final Board board = new Board();

    public String getPosition() {
        return board.getFen();
    }

    public List<Move> getLegalMoves() throws MoveGeneratorException {
        return MoveGenerator.generateLegalMoves(board);
    }

    public boolean makeMove(String uciMove) throws MoveGeneratorException {
        for (Move move : getLegalMoves()) {
            if (move.toString().equalsIgnoreCase(uciMove)) {
                return board.doMove(move);
            }
        }

        return false;
    }

    public boolean isGameOver() {
        return board.isMated() || board.isDraw();
    }
}

