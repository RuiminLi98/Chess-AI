package root;

public class FourthHeuristic implements Heuristic {

	@Override
	public int compute(ChessBoard board, int maxDepth) {
		int tmp = board.computerPieceNum - board.playerPieceNum;
		int limit = board.sizeNum / 2;
		if(board.sizeNum % 2 == 1) {++limit;}
		for(int i = 0;i < limit;++i) {
			for(int j = 0;j < board.sizeNum;++j) {
				if(board.board[i][j] >= 2 && board.board[i][j] <= 4) {
					--tmp;
					--maxDepth;
					if(maxDepth == 0) {return tmp;}
				}
			}
		}
		if(board.sizeNum % 2 == 1) {--limit;}
		for(int i = limit;i < board.sizeNum;++i) {
			for(int j = 0;j < board.sizeNum;++j) {
				if(board.board[i][j] >= 2 && board.board[i][j] <= 4) {
					--tmp;
					--maxDepth;
					if(maxDepth == 0) {return tmp;}
				}
			}
		}
		return tmp;
	}

}
