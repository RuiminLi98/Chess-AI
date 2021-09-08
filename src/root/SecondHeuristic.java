package root;

public class SecondHeuristic implements Heuristic {

	@Override
	public int compute(ChessBoard board, int maxDepth) {
		int tmp = board.computerPieceNum - board.playerPieceNum;
		for(int i = 0;i < board.sizeNum;++i) {
			for(int j = 0;j < board.sizeNum;++j) {
				if(board.board[i][j] >= 2 && board.board[i][j] <= 4 && maxDepth > 0) {
					maxDepth -= 1;
					tmp -= 1;
				}
			}
		}
		return tmp;
	}

}
