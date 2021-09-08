package root;

public class ChessBoard {
	public char[][] board;
	/*
	 * 0 -> Empty
	 * 1 -> Pit
	 * 2 -> wH
	 * 3 -> wM
	 * 4 -> wW
	 * 5 -> bH
	 * 6 -> bM
	 * 7 -> bW
	 */
	public int sizeNum;
	public int playerPieceNum;
	public int computerPieceNum;
	
	public double[][][] probData;
	public int[][] probPieceType;
	public int[] pieceCount;
	/*
	 * 0 -> number of black pieces
	 * 1 -> number of white pieces
	 */
	
	public double getProbPiece(int row, int column, int player, int type) {
		double sum = 0, target = 0;
		for(int i = -1;i < sizeNum + 1;++i) {
			double prod = 1;
			for(int j = 0;j < sizeNum + 1;++j) {
				if(i == j) {
					prod *= probData[player][j][row * sizeNum + column];
				} else {
					prod *= (1 - probData[player][j][row * sizeNum + column]);
				}
			}
			sum += prod;
			if(i >= 0 && type == probPieceType[player][i]) {target += prod;}
		}
		return target / sum;
	}
	
	private void markovUpdate(char player, int pieceId) {
		double newProb[] = new double[sizeNum * sizeNum + 1];
		newProb[sizeNum * sizeNum] = probData[player][pieceId][sizeNum * sizeNum];
		for(int i = 0;i < sizeNum * sizeNum;++i) {
			newProb[i] = probData[player][pieceId][i] * ((double)(pieceCount[player] - 1)) / pieceCount[player];
		}
		for(int i = 0;i < sizeNum * sizeNum;++i) {
			int row = i / sizeNum, col = i % sizeNum;
			int validNeighbor = 0;
			int[][] delta = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
			for(int j = 0;j < 8;++j) {
				int new_row = row + delta[j][0];
				int new_col = col + delta[j][1];
				if(new_row < 0 || new_row >= sizeNum || new_col < 0 || new_col >= sizeNum) {continue;}
				validNeighbor += 1;
			}
			for(int j = 0;j < 8;++j) {
				int new_row = row + delta[j][0];
				int new_col = col + delta[j][1];
				if(new_row < 0 || new_row >= sizeNum || new_col < 0 || new_col >= sizeNum) {continue;}
				int t = new_row * sizeNum + new_col;
				newProb[t] += probData[player][pieceId][i] / (pieceCount[player] * validNeighbor);
			}
		}
		for(int i = 0;i < sizeNum * sizeNum + 1;++i) {
			probData[player][pieceId][i] = newProb[i];
		}
	}
	
	private void dieUnspecific(char player) {
		double factor = ((double)pieceCount[player] - 1) / pieceCount[player];
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] == 1) {continue;}
			for(int j = 0;j < sizeNum * sizeNum;++j) {
				probData[player][i][j] *= factor;
			}
			probData[player][i][sizeNum * sizeNum] += (1 - probData[player][i][sizeNum * sizeNum]) / pieceCount[player];
		}
		pieceCount[player] -= 1;
	}
	
	private void dieSpecific(char player, char piece) {
		double sum = 0;
		if(piece >= 5) {piece -= 3;}
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] != piece) {continue;}
			sum += (1 - probData[player][i][sizeNum * sizeNum]);
		}
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] != piece) {continue;}
			double prob_of_death = (1 - probData[player][i][sizeNum * sizeNum]) / sum;
			for(int j = 0;j < sizeNum * sizeNum;++j) {
				probData[player][i][j] *= (1 - prob_of_death);
			}
			probData[player][i][sizeNum * sizeNum] = (1 - prob_of_death) * probData[player][i][sizeNum * sizeNum] + prob_of_death;
		}
		pieceCount[player] -= 1;
	}
	
	private void probClearCell(char player, int row, int col) {
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] == 1) {
				probData[player][i][row * sizeNum + col] = 0;
			} else {
				double factor = 1.0f / (1 - probData[player][i][row * sizeNum + col]);
				for(int j = 0;j < sizeNum * sizeNum + 1;++j) {
					if(j != row * sizeNum + col) {
						probData[player][i][j] *= factor;
					} else {
						probData[player][i][j] = 0;
					}
				}
			}
		}
	}
	
	private void probPieceOrPit(char player, int row, int col, char piece) {
		if(piece >= 5) {piece -= 3;}
		double sum = 0;
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] != piece && probPieceType[player][i] != 1) {
				double factor = 1.0f / (1 - probData[player][i][row * sizeNum + col]);
				for(int j = 0;j < sizeNum * sizeNum + 1;++j) {
					if(j != row * sizeNum + col) {
						probData[player][i][j] *= factor;
					} else {
						probData[player][i][j] = 0;
					}
				}
			} else {
				double prod = 1;
				for(int j = 0;j < sizeNum + 1;++j) {
					if(probPieceType[player][i] != piece && probPieceType[player][i] != 1) {continue;}
					if(i == j) {prod *= probData[player][j][row * sizeNum + col];} else {prod *= (1 - probData[player][j][row * sizeNum + col]);}
				}
				sum += prod;
			}
		}
		for(int i = 0;i < sizeNum + 1;++i) {
			if(probPieceType[player][i] != piece && probPieceType[player][i] != 1) {continue;}
			double prod = 1;
			for(int j = 0;j < sizeNum + 1;++j) {
				if(probPieceType[player][i] != piece && probPieceType[player][i] != 1) {continue;}
				if(i == j) {prod *= probData[player][j][row * sizeNum + col];} else {prod *= (1 - probData[player][j][row * sizeNum + col]);}
			}
			double prob_chosen = prod / sum;
			for(int j = 0;j < sizeNum * sizeNum + 1;++j) {
				if(j == row * sizeNum + col) {
					probData[player][i][j] = (1 - prob_chosen) * probData[player][i][j] + prob_chosen;
				} else {
					probData[player][i][j] = (1 - prob_chosen) * probData[player][i][j];
				}
			}
		}
	}
	
	public void updateProbability(char player, int src_row, int src_col, int dst_row, int dst_col, char piece, boolean survived, boolean killed) {
		//Step 1: Help opponent update distribution
		if(!killed) {
			for(int i = 0;i < sizeNum;++i) {
				markovUpdate(player == 0 ? (char)1 : (char)0,i);
			}
			if(!survived) {
				dieUnspecific(player == 0 ? (char)1 : (char)0);
			}
		} else {
			dieSpecific(player == 0 ? (char)1 : (char)0, piece);
		}
		
		//Step 2: Update our probability
		if(killed) {
			char target = 0;
			if(survived) {
				if(piece == 2 || piece == 5) {target = 4;}
				else if(piece == 3 || piece == 6) {target = 2;}
				else {target = 3;}
			} else {
				target = piece;
				if(target >= 5) {target -= 3;}
			}
			dieSpecific(player,target);
			probClearCell(player,dst_row,dst_col);
		} else {
			if(survived) {
				probClearCell(player,dst_row,dst_col);
			} else {
				char target = 0;
				if(piece == 2 || piece == 5) {target = 3;}
				else if(piece == 3 || piece == 6) {target = 4;}
				else {target = 2;}
				probPieceOrPit(player,dst_row,dst_col,target);
			}
		}
	}
	
	private boolean isWhite(char t) {
		return t >= 2 && t <= 4;
	}
	
	/* We assume that a and b are a pair of black and white pieces */
	private boolean sameKind(char a, char b) {
		if(!isWhite(a)) {char t = a;a = b;b = t;}
		return b == a + 3;
	}
	
	public boolean canKill(char a, char b) {
		char[][] killPairs = {{2,7},{3,5},{4,6},{5,4},{6,2},{7,3}};
		for(int i = 0;i < 6;++i) {if(a == killPairs[i][0] && b == killPairs[i][1]) {return true;}}
		return false;
	}
	
	private void losePiece(int x,int y) {
		char t = board[x][y];
		if(isWhite(t)) {playerPieceNum--;} else {computerPieceNum--;}		
		board[x][y] = 0;
	}
	
	/* Return value indicates jump successful or not */
	public boolean jump(int startX, int startY, int direction) {
		if(board[startX][startY] == 0 || board[startX][startY] == 1) {return false;}
		int[][] delta = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
		int goalX = startX + delta[direction][0], goalY = startY + delta[direction][1];
		if(goalX < 0 || goalX >= sizeNum) {return false;}
		if(goalY < 0 || goalY >= sizeNum) {return false;}
		
		if(board[goalX][goalY] == 1) {
			losePiece(startX,startY);
			return true;
		} else if(board[goalX][goalY] == 0) {
			board[goalX][goalY] = board[startX][startY];
			board[startX][startY] = 0;
			return true;
		}
		
		if(!(isWhite(board[startX][startY]) ^ isWhite(board[goalX][goalY]))) {return false;}
		if(sameKind(board[startX][startY],board[goalX][goalY])) {
			losePiece(startX,startY);
			losePiece(goalX,goalY);
		} else if(canKill(board[startX][startY],board[goalX][goalY])) {
			losePiece(goalX,goalY);
			board[goalX][goalY] = board[startX][startY];
			board[startX][startY] = 0;
		} else {
			losePiece(startX,startY);
		}
		return true;
	}
	
	public void copyBoard(ChessBoard src) {
		for(int i = 0;i < sizeNum;++i) {
			for(int j = 0;j < sizeNum;++j) {
				board[i][j] = src.board[i][j];
			}
		}
		computerPieceNum = src.computerPieceNum;
		playerPieceNum = src.playerPieceNum;
	}
	
	
}
