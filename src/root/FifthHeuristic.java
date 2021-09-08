package root;

public class FifthHeuristic implements Heuristic {

	@Override
	public int compute(ChessBoard board, int maxDepth) {
		int tmp = board.computerPieceNum - board.playerPieceNum;
		for(int i = 0;i < board.sizeNum * board.sizeNum;++i) {
			char p = board.board[i / board.sizeNum][i % board.sizeNum];
			if(p < 5 || p > 7) {continue;}
			for(int j = 0;j < board.sizeNum * board.sizeNum;++j) {
				if(i == j) {continue;}
				char q = board.board[j / board.sizeNum][j % board.sizeNum];
				if(!board.canKill(p, q)) {continue;}
				maxDepth -= 1;
				tmp -= 1;
				if(maxDepth == 0) {return tmp;}
			}
		}
		return tmp;
	}

}
