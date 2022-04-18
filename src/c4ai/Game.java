package c4ai;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
	public static int WIDTH = 7;
	public static int HEIGHT = 6;
	public static int WIN_LENGTH = 4;

	public int[][] grid;
	public int turn = 0;

	// 1 - X, 2 - O
	public int winner = 0;

	public Game() {
		 grid = new int[HEIGHT][WIDTH];
		 for (int i = 0; i < grid.length; i++) {
		 	for (int j = 0; j < grid[0].length; j++) {
		 		grid[i][j] = 0;
		 	}
		 }
	}

	public int randCol() {
		return ThreadLocalRandom.current().nextInt(0, HEIGHT);
	}

	public String getWinnerName() {
		if (winner == 0) return null;
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

	public void move(int col) {
		move(col, isXTurn());
	}

	public void move(int col, boolean isX) {
		int piece = isX ? 1 : 2;
		int row;
		for (row = HEIGHT -1; row >= 0; row--) {
			if (grid[row][col] == 0) {
				break;
			}
		}
		if (row < 0) {
			return;
		}

		// set and calculate if we won
		grid[row][col] = piece;
		// calculate
		int horiz = countRay(row, col, 0, 1);
		int vert = countRay(row, col, 1, 0);
		int cross = countRay(row, col, 1, 1);
		int cross2 = countRay(row, col, 1, -1);
		if (horiz >= WIN_LENGTH || vert >= WIN_LENGTH || cross >= WIN_LENGTH || cross2 >= WIN_LENGTH) {
			win();
		}

		// pass turn
		turn++;
	}

	private void win() {
		winner = isXTurn() ? 1 : 2;
	}

	private int countRay(int oi, int oj, int c, int d) {
		int i = oi;
		int j = oj;
		int orig = grid[oi][oj];
		int cnt = -1; // middle will be counted twice
		while (i >= 0 && j >= 0 && i < HEIGHT && j < WIDTH) {
			if (grid[i][j] == orig) {
				cnt++;
			} else {
				break;
			}
			i += c;
			j += d;
		}
		i = oi;
		j = oj;
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

	public int suggestMove() {
		return randCol();
	}
}