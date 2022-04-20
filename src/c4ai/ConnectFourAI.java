package c4ai;

import java.util.*;

public class ConnectFourAI {

	public static Game game;
	public static AI ai;

	public static void main(String[] args) throws Exception {
		game = new Game();
		ai = new AI();
		game.print();

		Scanner in = new Scanner(System.in);

		//game.turn = 1;

		while (game.winner == 0) {
			int move;
			if (game.isXTurn()) {
				//move = ai.suggestMove(game);
				System.out.print("Enter a column (1-" + Game.WIDTH + "): ");
				move = Math.min(in.nextInt()-1, Game.WIDTH-1);
			} else {
				move = ai.suggestMove(game);
				// System.out.print("Enter a column (1-" + Game.WIDTH + "): ");
				// move = Math.min(in.nextInt()-1, Game.WIDTH-1);
			}

			System.out.println("Placing at column " + (move+1));
			game.move(move);
			game.print();
		}

		System.out.println(game.getWinnerName() + " WINS");
	}
}