package chess;

import java.util.Random;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.*;
import root.*;

public class chessGen implements ActionListener {
	public static Random randEngine;
	
	private JFrame f;
	private ImageIcon[] boardIcons;
	private JLabel[] boardLabels;
	private ChessBoard currentBoard;
	private JLabel labelCoordX, labelCoordY, labelDirection;
	private JTextField textCoordX, textCoordY, textDirection;
	private JLabel labelTurn;
	private JButton buttonMove;
	private JButton buttonSearch;
	private JLabel labelSearchDepth;
	private JTextField textSearchDepth;
	private JLabel labelHeur;
	private JTextField textHeur;
	private JButton buttonFog;
	private JLabel labelPieceInfo;
	private boolean fogMode;
	private int clickedLabel;
	
	public class LabelArrayHandler extends MouseAdapter {
		int index;
		public LabelArrayHandler(int index_) {
			index = index_;
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			clickedLabel = index;
			labelClicked();
		}
	}

	
	private char turn;//0 -> White (human)'s turn, 1 -> Black (machine)'s turn
	
	public static ChessBoard Initialize() {
		ChessBoard board = new ChessBoard();
		randEngine=new Random();
		board.sizeNum = (randEngine.nextInt(4) + 1) * 3;
		board.board=new char[board.sizeNum][board.sizeNum];
		for(int i = 0;i < board.sizeNum;i += 3)
			board.board[0][i] = 7;
		for(int i = 1;i < board.sizeNum;i += 3)
			board.board[0][i] = 5;
		for(int i = 2;i < board.sizeNum;i += 3)
			board.board[0][i] = 6;
		for(int i = 0;i < board.sizeNum;i += 3)
			board.board[board.sizeNum-1][i] = 4;
		for(int i = 1;i < board.sizeNum;i += 3)
			board.board[board.sizeNum-1][i] = 2;
		for(int i = 2;i < board.sizeNum;i += 3)
			board.board[board.sizeNum-1][i] = 3;
		for(int i = 1;i < board.sizeNum-1;i++)	
			for(int j = 0;j < board.sizeNum;j++)
				board.board[i][j] = 0;
		for(int i = 1;i < board.sizeNum-1;i++)
		{
				int num=0,num2;
				while(num < board.sizeNum / 3 - 1)
				{
					num2 = randEngine.nextInt(board.sizeNum - 1);
					if(board.board[i][num2] != 1) {
						board.board[i][num2] = 1;
						num += 1;
					}
				}
		}
		board.probData = new double[2][board.sizeNum + 1][board.sizeNum * board.sizeNum + 1];
		board.probPieceType = new int[2][board.sizeNum + 1];
		for(int i = 0;i < board.sizeNum;++i) {
			if(i % 3 == 0) {board.probPieceType[0][i] = 4;board.probPieceType[1][i] = 4;}
			else if(i % 3 == 1) {board.probPieceType[0][i] = 2;board.probPieceType[1][i] = 2;}
			else {board.probPieceType[0][i] = 3;board.probPieceType[1][i] = 3;}
			board.probData[1][i][(board.sizeNum - 1) * board.sizeNum + i] = 1;
			board.probData[0][i][i] = 1;
		}
		board.probPieceType[0][board.sizeNum] = 1;
		board.probPieceType[1][board.sizeNum] = 1;
		for(int i = board.sizeNum;i < board.sizeNum * (board.sizeNum - 1);++i) {
			board.probData[0][board.sizeNum][i] = 0.1d;
			board.probData[1][board.sizeNum][i] = 0.1d;
		}
		board.pieceCount = new int[2];
		board.pieceCount[0] = board.sizeNum;
		board.pieceCount[1] = board.sizeNum;
		board.playerPieceNum = board.sizeNum;
		board.computerPieceNum = board.sizeNum;
		return board;
	}
	
	private void CreateComponents(int boardSize) {
		f = new JFrame();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		f.setLayout(null);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		int boardLeft = 15, boardTop = 15,
			boardWidth = 680, boardHeight = 680,
			pieceWidth = boardWidth / boardSize, pieceHeight = boardHeight / boardSize;
		
		boardIcons = new ImageIcon[9];
		boardIcons[0] = new ImageIcon("empty.png");
		boardIcons[1] = new ImageIcon("pit.png");
		boardIcons[2] = new ImageIcon("wh.png");
		boardIcons[3] = new ImageIcon("wm.png");
		boardIcons[4] = new ImageIcon("ww.png");
		boardIcons[5] = new ImageIcon("bh.png");
		boardIcons[6] = new ImageIcon("bm.png");
		boardIcons[7] = new ImageIcon("bw.png");
		boardIcons[8] = new ImageIcon("fog.png");
		
		for(int i = 0;i < 9;++i) {
			Image image = boardIcons[i].getImage();
			image = image.getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH);
			boardIcons[i] = new ImageIcon(image);
		}
		
		boardLabels = new JLabel[boardSize * boardSize];
		
		for(int i = 0;i < boardSize * boardSize;++i) {
			boardLabels[i] = new JLabel(Integer.toString(i));
			boardLabels[i].setBounds(boardLeft + pieceWidth * (i % boardSize),boardTop + pieceHeight * (i / boardSize),pieceWidth,pieceHeight);
			boardLabels[i].addMouseListener(new LabelArrayHandler(i));
			f.add(boardLabels[i]);
		}
		
		labelCoordX = new JLabel("X Coordinate");
		labelCoordX.setBounds(700,25,100,50);
		labelCoordY = new JLabel("Y Coordinate");
		labelCoordY.setBounds(825,25,100,50);
		labelDirection = new JLabel("Direction");
		labelDirection.setBounds(950,25,100,50);
		textCoordX = new JTextField();
		textCoordX.setBounds(700,60,100,25);
		textCoordY = new JTextField();
		textCoordY.setBounds(825,60,100,25);
		textDirection = new JTextField();
		textDirection.setBounds(950,60,100,25);
		buttonMove = new JButton("Move");
		buttonMove.setBounds(825,100,100,25);
		buttonMove.addActionListener(this);
		labelTurn = new JLabel();
		labelTurn.setBounds(825,150,150,25);
		buttonSearch = new JButton("Run Search");
		buttonSearch.setBounds(825,200,100,25);
		buttonSearch.addActionListener(this);
		labelSearchDepth = new JLabel("Minimax search depth");
		labelSearchDepth.setBounds(825,250,150,25);
		textSearchDepth = new JTextField("3");
		textSearchDepth.setBounds(825,300,100,25);
		labelHeur = new JLabel("Heuristic (a-e)");
		labelHeur.setBounds(825,350,100,25);
		textHeur = new JTextField("a");
		textHeur.setBounds(825,400,100,25);
		buttonFog = new JButton("Enter Fog Mode");
		buttonFog.setBounds(825,450,150,25);
		buttonFog.addActionListener(this);
		labelPieceInfo = new JLabel("");
		labelPieceInfo.setBounds(750,250,400,175);
		labelPieceInfo.setVisible(false);
		labelPieceInfo.setVerticalAlignment(SwingConstants.TOP); 
		f.add(labelCoordX);
		f.add(labelCoordY);
		f.add(labelDirection);
		f.add(textCoordX);
		f.add(textCoordY);
		f.add(textDirection);
		f.add(buttonMove);
		f.add(labelTurn);
		f.add(buttonSearch);
		f.add(labelSearchDepth);
		f.add(textSearchDepth);
		f.add(labelHeur);
		f.add(textHeur);
		f.add(buttonFog);
		f.add(labelPieceInfo);
	}
	
	private void redrawBoard() {
		if(!fogMode) {
			for(int i = 0;i < currentBoard.sizeNum * currentBoard.sizeNum;++i) {
				boardLabels[i].setIcon(boardIcons[currentBoard.board[i / currentBoard.sizeNum][i % currentBoard.sizeNum]]);
			}
		} else {
			for(int i = 0;i < currentBoard.sizeNum * currentBoard.sizeNum;++i) {
				char piece = currentBoard.board[i / currentBoard.sizeNum][i % currentBoard.sizeNum];
				if(turn == 0 && piece >= 2 && piece <= 4 || turn == 1 && piece >= 5 && piece <= 7) {
					boardLabels[i].setIcon(boardIcons[piece]);
				} else {
					boardLabels[i].setIcon(boardIcons[8]);
				}
			}
		}
		if(turn == 0) {
			labelTurn.setText("White (player)'s turn");
		} else {
			labelTurn.setText("Black (machine)'s turn");
		}
	}
	
	private void buttonMovePressed() {
		try {
			String coordX = textCoordX.getText();
			int x = Integer.parseInt(coordX);
			String coordY = textCoordY.getText();
			int y = Integer.parseInt(coordY);
			String dir = textDirection.getText();
			int d = Integer.parseInt(dir);
			if(x < 0 || x >= currentBoard.sizeNum || y < 0 || y >= currentBoard.sizeNum) {throw new NumberFormatException();}
			if(d < 0 || d >= 8) {throw new NumberFormatException();}
			int[][] delta = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
			int goalX = x + delta[d][0], goalY = y + delta[d][1];
			if(goalX < 0 || goalX >= currentBoard.sizeNum || goalY < 0 || goalY >= currentBoard.sizeNum) {throw new NumberFormatException();}
			char t = currentBoard.board[x][y], v = currentBoard.board[goalX][goalY];
			if((turn == 0 && !(t >= 2 && t <= 4)) || (turn == 1 && !(t >= 5 && t <= 7))) {throw new NumberFormatException();}
			if(currentBoard.jump(x, y, d)) {
				if(fogMode) {currentBoard.updateProbability(turn, x, y, goalX, goalY, t, currentBoard.board[goalX][goalY] == t, v != 0 && currentBoard.board[goalX][goalY] != v);}
				if(turn == 0) {turn = 1;} else {turn = 0;}
				redrawBoard();
				
			} else {
				JOptionPane.showMessageDialog(f, "Move failed");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void buttonSearchPressed() {
		if(!fogMode) {
			try {
				Searcher search = new Searcher();
				int depth = Integer.parseInt(textSearchDepth.getText());
				if(depth <= 0) {JOptionPane.showMessageDialog(f, "Please enter a valid search depth");return;}
				String heur = textHeur.getText();
				if(heur.equals("a")) {
					search.heur = new FirstHeuristic();
				} else if(heur.equals("b")) {
					search.heur = new SecondHeuristic();
				} else if(heur.equals("c")) {
					search.heur = new ThirdHeuristic();
				} else if(heur.equals("d")) {
					search.heur = new FourthHeuristic();
				} else if(heur.equals("e")) {
					search.heur = new FifthHeuristic();
				} else {
					JOptionPane.showMessageDialog(f, "Please enter a valid heuristic (a-e)");return;
				}
				search.heur = new FirstHeuristic();
				search.RunSearch(currentBoard, turn == 1, depth, true, -10000, 10000);
				textCoordX.setText(Integer.toString(search.suggestedMove.x));
				textCoordY.setText(Integer.toString(search.suggestedMove.y));
				textDirection.setText(Integer.toString(search.suggestedMove.dir));
			} catch(Exception e) {
				JOptionPane.showMessageDialog(f, "Error during search");
			}
		} else {
			FogSearcher searcher = new FogSearcher();
			searcher.board = currentBoard;
			searcher.findMove(turn);
			textCoordX.setText(Integer.toString(searcher.resultX));
			textCoordY.setText(Integer.toString(searcher.resultY));
			textDirection.setText(Integer.toString(searcher.resultDir));
		}
	}
	
	private void buttonFogPressed() {
		try {
			if(fogMode) {
				buttonFog.setText("Enter Fog Mode");
				fogMode = false;
				labelSearchDepth.setVisible(true);
				textSearchDepth.setVisible(true);
				labelHeur.setVisible(true);
				textHeur.setVisible(true);
				labelPieceInfo.setVisible(false);
			} else {
				buttonFog.setText("Exit Fog Mode");
				fogMode = true;
				labelSearchDepth.setVisible(false);
				textSearchDepth.setVisible(false);
				labelHeur.setVisible(false);
				textHeur.setVisible(false);
				labelPieceInfo.setVisible(true);
				labelPieceInfo.setText("Click on a piece to show probability and observation data");
			}
			redrawBoard();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(f, "Unknown error occurred");
		}
	}
	
	private void labelClicked() {
		int row = clickedLabel / currentBoard.sizeNum, column = clickedLabel % currentBoard.sizeNum;
		char piece = currentBoard.board[row][column];
		String info = "<html>Row: " + row + ", Column: " + column + "<br>";
		boolean pit = false, wumpus = false, hero = false, mage = false;
		int neighbor[][] = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
		if(turn == 0) {
			if(piece >= 2 && piece <= 4) {
				for(int i = 0;i < 4;++i) {
					int new_x = row + neighbor[i][0], new_y = column + neighbor[i][1];
					if(new_x < 0 || new_x >= currentBoard.sizeNum) {continue;}
					if(new_y < 0 || new_y >= currentBoard.sizeNum) {continue;}
					if(currentBoard.board[new_x][new_y] == 1) {pit = true;}
					if(currentBoard.board[new_x][new_y] == 5) {hero = true;}
					if(currentBoard.board[new_x][new_y] == 6) {mage = true;}
					if(currentBoard.board[new_x][new_y] == 7) {wumpus = true;}
				}
				info += "Pit: " + pit + ", Hero: " + hero + ", Mage: " + mage + ", Wumpus: " + wumpus + "<br>";
			} else {
				double p1,p2,p3,p4,p0;
				p1 = currentBoard.getProbPiece(row, column, 0, 1);
				p2 = currentBoard.getProbPiece(row, column, 0, 2);
				p3 = currentBoard.getProbPiece(row, column, 0, 3);
				p4 = currentBoard.getProbPiece(row, column, 0, 4);
				p0 = 1 - p1 - p2 - p3 - p4;
				info += "Probability of empty: " + p0 + "<br>";
				info += "Probability of pit: " + p1 + "<br>";
				info += "Probability of hero: " + p2 + "<br>";
				info += "Probability of mage: " + p3 + "<br>";
				info += "Probability of wumpus: " + p4;
			}
		} else {
			if(piece >= 5 && piece <= 7) {
				for(int i = 0;i < 4;++i) {
					int new_x = row + neighbor[i][0], new_y = column + neighbor[i][1];
					if(new_x < 0 || new_x >= currentBoard.sizeNum) {continue;}
					if(new_y < 0 || new_y >= currentBoard.sizeNum) {continue;}
					if(currentBoard.board[new_x][new_y] == 1) {pit = true;}
					if(currentBoard.board[new_x][new_y] == 2) {hero = true;}
					if(currentBoard.board[new_x][new_y] == 3) {mage = true;}
					if(currentBoard.board[new_x][new_y] == 4) {wumpus = true;}
				}
				info += "Pit: " + pit + ", Hero: " + hero + ", Mage: " + mage + ", Wumpus: " + wumpus + "<br>";
			} else {
				double p1,p2,p3,p4,p0;
				p1 = currentBoard.getProbPiece(row, column, 1, 1);
				p2 = currentBoard.getProbPiece(row, column, 1, 2);
				p3 = currentBoard.getProbPiece(row, column, 1, 3);
				p4 = currentBoard.getProbPiece(row, column, 1, 4);
				p0 = 1 - p1 - p2 - p3 - p4;
				info += "Probability of empty: " + p0 + "<br>";
				info += "Probability of pit: " + p1 + "<br>";
				info += "Probability of hero: " + p2 + "<br>";
				info += "Probability of mage: " + p3 + "<br>";
				info += "Probability of wumpus: " + p4;
			}
		}
		labelPieceInfo.setText(info);
	}
	
	public void actionPerformed(ActionEvent ev) {
		try {
			if(ev.getSource() == buttonMove) {
				buttonMovePressed();
			} else if(ev.getSource() == buttonSearch) {
				buttonSearchPressed();
			} else if(ev.getSource() == buttonFog) {
				buttonFogPressed();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			ChessBoard board = Initialize();
			chessGen inst = new chessGen();
			inst.CreateComponents(board.sizeNum);
			inst.currentBoard = board;
			inst.turn = 0;
			inst.fogMode = false;
			inst.redrawBoard();
			inst.f.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
