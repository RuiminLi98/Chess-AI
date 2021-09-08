package root;

public class FirstHeuristic implements Heuristic {

	@Override
	public int compute(ChessBoard board, int maxDepth) {
		return board.computerPieceNum - board.playerPieceNum - maxDepth;
	}

}
