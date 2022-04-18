package c4ai;

import java.util.*;

public class ConnectFourAI {

	public static Game game;

	public static void main(String[] args) throws Exception {
		game = new Game();
		game.print();

		Scanner in = new Scanner(System.in);

		while (game.winner == 0) {
			if (game.isXTurn()) {
				int i = in.nextInt();
				game.move(Math.min(i-1, 6));
			} else {
				int i = game.suggestMove();
				game.move(i);
			}
			game.print();
		}

		System.out.println(game.getWinnerName() + " WINS");
	}
}