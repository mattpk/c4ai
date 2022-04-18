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
			int i;
			if (game.isXTurn()) {
				System.out.print("Enter a column (1-" + Game.WIDTH + "): ");
				i = Math.min(in.nextInt()-1, Game.WIDTH-1);
			} else {
				i = ai.suggestMove(game);
			}

			System.out.println("Placing at column " + (i+1));
			game.move(i);
			game.print();
		}

		System.out.println(game.getWinnerName() + " WINS");
	}
}