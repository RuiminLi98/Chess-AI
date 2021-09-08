package root;

public class Searcher {
	public static class MoveSpec {
		public int x, y, dir;
	}
	
	public MoveSpec suggestedMove;
	public Heuristic heur;
	
	private int metric(ChessBoard board) {
		return board.computerPieceNum - board.playerPieceNum;
	}
	
	/* Maximize black, minimize white */
	public int RunSearch(ChessBoard board, boolean black, int maxDepth, boolean record, int alpha, int beta) {
		BinaryHeap heap = new BinaryHeap();
		
		int currVal = -1;
		if(record) {
			suggestedMove = new MoveSpec();
			suggestedMove.x = suggestedMove.y = suggestedMove.dir = -1;
		}
		BinaryHeap.Entry currMove;
		if(maxDepth == 0) {return metric(board);}
		if(record) {suggestedMove = new MoveSpec();}
		ChessBoard newBoard = new ChessBoard();
		newBoard.board = new char[board.sizeNum][board.sizeNum];
		newBoard.sizeNum = board.sizeNum;
		if(black) {
			currVal = -10000;
			for(int i = 0;i < board.sizeNum;++i) {
				for(int j = 0;j < board.sizeNum;++j) {
					char t = board.board[i][j];
					if(t < 5 || t > 7) {continue;}
					for(int k = 0;k < 8;++k) {
						newBoard.copyBoard(board);
						if(!newBoard.jump(i,j,k)) {continue;}
						heap.insert(i, j, k, -heur.compute(newBoard, maxDepth - 1));
					}
				}
			}
			while((currMove = heap.pop()) != null) {
				newBoard.copyBoard(board);
				newBoard.jump(currMove.x, currMove.y, currMove.dir);
				int tmp = RunSearch(newBoard,!black,maxDepth - 1,false, alpha, beta);
				if(tmp > currVal) {
					currVal = tmp;
					if(record) {suggestedMove.x = currMove.x;suggestedMove.y = currMove.y;suggestedMove.dir = currMove.dir;}
				}
				if(alpha < currVal) {alpha = currVal;}
				if(alpha >= beta) {return currVal;}
			}
			return currVal;
		} else {
			currVal = 10000;
			for(int i = 0;i < board.sizeNum;++i) {
				for(int j = 0;j < board.sizeNum;++j) {
					char t = board.board[i][j];
					if(t < 2 || t > 4) {continue;}
					for(int k = 0;k < 8;++k) {
						newBoard.copyBoard(board);
						if(!newBoard.jump(i,j,k)) {continue;}
						heap.insert(i, j, k, heur.compute(newBoard, maxDepth - 1));
					}
				}
			}
			while((currMove = heap.pop()) != null) {
				newBoard.copyBoard(board);
				newBoard.jump(currMove.x, currMove.y, currMove.dir);
				int tmp = RunSearch(newBoard,!black,maxDepth - 1,false, alpha, beta);
				if(tmp < currVal) {
					currVal = tmp;
					if(record) {suggestedMove.x = currMove.x;suggestedMove.y = currMove.y;suggestedMove.dir = currMove.dir;}
				}
				if(beta > currVal) {beta = currVal;}
				if(beta <= alpha) {return currVal;}
			}
			return currVal;
		}
	}
}
