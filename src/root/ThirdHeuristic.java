package root;

public class ThirdHeuristic implements Heuristic {

	@Override
	public int compute(ChessBoard board, int maxDepth) {
		int tmp = board.computerPieceNum - board.playerPieceNum;
		for(int i = 0;i < board.sizeNum;++i) {
			for(int j = 0;j < board.sizeNum;++j) {
				if(board.board[i][j] < 2 || board.board[i][j] > 4) {continue;}
				for(int k = -maxDepth;k <= maxDepth;++k) {
					if(i + k < 0 || i + k >= board.sizeNum) {continue;}
					for(int l = -maxDepth;l <= maxDepth;++l) {
						if(j + l < 0 || j + l >= board.sizeNum) {continue;}
						if(board.board[k][l] >= 5 && board.board[k][l] <= 7) {
							tmp -= 1;
							maxDepth -= 1;
							if(maxDepth == 0) {return tmp;}
						}
					}
				}
			}
		}
		return tmp;
	}

}
