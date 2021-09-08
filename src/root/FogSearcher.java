package root;

import java.util.ArrayList;
import java.util.Random;

public class FogSearcher {
	public ChessBoard board;
	public int resultX, resultY, resultDir;
	static private Random r = new Random();
	
	private class ProbEntry{
		int x, y, dir;
		double prob;
	}
	
	public void findMove(char player) {
		if(board == null) {return;}
		double sum = 0;
		ArrayList<ProbEntry> list = new ArrayList<ProbEntry>();
		int[][] delta = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
		
		for(int row = 0;row < board.sizeNum;++row) {
			for(int column = 0;column < board.sizeNum;++column) {
				char t = board.board[row][column];
				if(t <= 1 || (player == 0 && t >= 5 && t <= 7) || (player == 1 && t >= 2 && t <= 4)) {continue;}
				for(int dir = 0;dir < 8;++dir) {
					int new_row = row + delta[dir][0];
					int new_col = column + delta[dir][1];
					if(new_row < 0 || new_row >= board.sizeNum || new_col < 0 || new_col >= board.sizeNum) {continue;}
					char new_t = board.board[new_row][new_col];
					if(player == 0 && new_t >= 2 && new_t <= 4 || player == 1 && new_t >= 5 && new_t <= 7) {continue;}
					double p1,p2,p3,p4,p0;
					p1 = board.getProbPiece(new_row, new_col, 0, 1);
					p2 = board.getProbPiece(new_row, new_col, 0, 2);
					p3 = board.getProbPiece(new_row, new_col, 0, 3);
					p4 = board.getProbPiece(new_row, new_col, 0, 4);
					p0 = 1 - p1 - p2 - p3 - p4;
					ProbEntry ent = new ProbEntry();
					ent.x = row;ent.y = column;ent.dir = dir;
					double prob = 0;
					if(t == 2 || t == 5) {prob = p0 - 2 * p1 - p2 - 2 * p3 + 2 * p4;}
					else if(t == 3 || t == 6) {prob = p0 - 2 * p1 + 2 * p2 - p3 - 2 * p4;}
					else {prob = p0 - 2 * p1 - 2 * p2 + 2 * p3 - p4;}
					prob = Math.exp(prob);
					ent.prob = prob;
					list.add(ent);
					sum += prob;
				}
			}
		}
		
		double choice = r.nextDouble() * sum;
		double currSum = 0;
		int c = 0;
		while(c < list.size() - 1) {
			if(currSum + list.get(c).prob > choice) {break;}
			++c;
			currSum += list.get(c).prob;
		}
		resultX = list.get(c).x;
		resultY = list.get(c).y;
		resultDir = list.get(c).dir;
	}
}
