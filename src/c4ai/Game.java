package c4ai;

import java.util.*;

@SuppressWarnings (value="unchecked")
public class Game {
	public static int WIDTH = 7;
	public static int HEIGHT = 6;
	public static int WIN_LENGTH = 4;

	public int[][] grid;
	public int[] heights;
	public int turn = 0;
	public Stack<Integer> history;

	// 0 - undecided 1 - X, 2 - O, 3 - DRAW
	public int winner = 0;

	public Game() {
		heights = new int[WIDTH];
		grid = new int[HEIGHT][WIDTH];
	 	for (int i = 0; i < grid.length; i++) {
	 		for (int j = 0; j < grid[0].length; j++) {
	 			grid[i][j] = 0;
	 		}
	 	}
	 	turn = 0;
	 	history = new Stack<Integer>();
	}

	// copy constructor
	public Game(Game game) {
		heights = new int[WIDTH];
		grid = new int[HEIGHT][WIDTH];
		toGame(game);
	}

	public void toGame(Game game) {
		for (int i = 0; i < heights.length; i++) {
			heights[i] = game.heights[i];
		}
		for (int i = 0; i < grid.length; i++) {
	 		for (int j = 0; j < grid[0].length; j++) {
	 			grid[i][j] = game.grid[i][j];
	 		}
	 	}
	 	turn = game.turn;
	 	winner = game.winner;
	 	history = (Stack<Integer>) game.history.clone();
	}

	public String getWinnerName() {
		if (winner == 0) return null;
		if (winner == 3) return "NOBODY";
		return winner == 1 ? "Blue" : "Red";
	}

	public void print() {
		printGrid(grid);
	}

	public void printGrid(int[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			StringBuilder b = new StringBuilder();
			b.append('║');
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == 0) {
					b.append('　');
				} else if (grid[i][j] == 1) {
					b.append("🔵");
				} else {
					b.append("🔴");
				}
			}
			b.append('║');
			System.out.println(b.toString());
		}
	}

	public boolean isXTurn() {
		return turn % 2 == 0;
	}

	public List<Integer> legalMoves() {
		List<Integer> moves = new ArrayList<>();
		for (int i = 0; i < WIDTH; i++) {
			if (heights[i] < HEIGHT)
				moves.add(i);
		}
		return moves;
	}

	public void undo() {
		if (history.isEmpty()) {
			return;
		}
		int col = history.pop();
		int row = HEIGHT-heights[col];
		grid[row][col] = 0;
		heights[col]--;
		turn--;
		winner = 0;
	}

	public void move(int col) {
		move(col, isXTurn());
	}

	public int rowFromCol(int col) {
		return HEIGHT-1-heights[col];
	}

	public void move(int col, boolean isX) {
		int piece = isX ? 1 : 2;
		int row = rowFromCol(col);
		if (row < 0) {
			System.out.println("Illegal move");
			return;
		}

		// set and calculate if we won
		grid[row][col] = piece;
		heights[col]++;
		history.push(col);

		// calculate
		
		if (countMaxRays(row, col) >= WIN_LENGTH) {
			win();
		} else if (turn + 1 == WIDTH * HEIGHT) {
			draw();
		}

		// pass turn
		turn++;
	}

	private void win() {
		winner = isXTurn() ? 1 : 2;
	}
	private void draw() {
		winner = 3;
	}

	public int simulateCountRays(int col) {
		int row = rowFromCol(col);
		if (grid[row][col] != 0) {
			return countMaxRays(row, col);
		}
		grid[row][col] = isXTurn() ? 1 : 2;
		int ans = countMaxRays(row, col);
		grid[row][col] = 0;
		return ans;
	}

	public int countMaxRays(int row, int col) {
		return Math.max(
			Math.max(
				countRay(row, col, 0, 1),
				countRay(row, col, 1, 0)
			),
			Math.max(
				countRay(row, col, 1, 1),
				countRay(row, col, 1, -1)
			)
		);
	}

	private int countRay(int oi, int oj, int c, int d) {
		int i = oi;
		int j = oj;
		int orig = grid[oi][oj];
		int cnt = 0;
		while (i >= 0 && j >= 0 && i < HEIGHT && j < WIDTH) {
			if (grid[i][j] == orig) {
				cnt++;
			} else {
				break;
			}
			i += c;
			j += d;
		}
		i = oi - c;
		j = oj - d;
		while (i >= 0 && j >= 0 && i < HEIGHT && j < WIDTH) {
			if (grid[i][j] == orig) {
				cnt++;
			} else {
				break;
			}
			i -= c;
			j -= d;
		}
		return cnt;
	}
}