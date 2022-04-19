package c4ai;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class AI {
	public static int DEPTH = 5;
	private int recursions = 0;
	private int bestMove = -1;
	private Game copy;

	private void debug(String s) {
		if (true) {
			System.out.println(s);
		}
	}

	private int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public int randomMove(Game game) {
		List<Integer> moves = game.legalMoves();
		int num = randInt(0, moves.size());
		return moves.get(num);
	}

	public int suggestMove(Game game) {
		copy = new Game(game);
		bestMove = -1;
		minimax(game, 0, 1, 0);
		return bestMove;
	}

	private double minimax(Game game, double minGuaranteedForMaximizer, double maxGuaranteedForMinimizer, int depth) {
		boolean isMaximizing = game.isXTurn();
		// base cases
		double salt = randInt(0, 1000) / 1000000.0;
		double lengthIncentive = ((double)game.turn) / 1000 + salt;
		
		if (game.winner == 1) {
			return 1.0 - lengthIncentive;
		}
		if (game.winner == 2) {
			return 0.0 + lengthIncentive;
		}
		if (game.winner == 3) {
			return 0.5;
		}
		List<Integer> moves = game.legalMoves();

		if (depth >= DEPTH) {
			return monteCarloValue(game);
		}

		if (isMaximizing) {
			double max = 0.0;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, depth+1);
				if (ans > max) {
					max = ans;
					if (depth == 0) bestMove = move;
				}
				game.undo();

 				if (ans > maxGuaranteedForMinimizer) {
					// Any remaining subtrees aren't worth searching
					// because the minimizer has a better option and
					// would never choose this subtree
					break;
				}

				if (depth > 0) {
				 	// update minimum guaranteed for further searching
					minGuaranteedForMaximizer = Math.max(max, minGuaranteedForMaximizer);
				} else {
					debug("move " + (move+1) + ": " + String.format("%.12f", ans));
				}
			}
			return max;
		} else {
			double min = 1.0;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, depth+1);
				if (ans < min) {
					min = ans;
					if (depth == 0) bestMove = move;
				}
				game.undo();

				if (ans < minGuaranteedForMaximizer) {
					// The maximizer would never let the game get into this state
					// since they already had a better guaranteed score
					break;
				}

				if (depth > 0) {
					// update max guaranteed for further searching
					maxGuaranteedForMinimizer = Math.min(min, maxGuaranteedForMinimizer);
				} else {
					debug("move " + (move+1) + ": " + String.format("%.12f", ans));
				}
			}
			return min;
		}
	}

	private double monteCarloValue(Game game) {
		// naive implementation, just run a bunch of simulations
		int NUM_SIMS = 100;
		int winsForX = 0;
		for (int s = 0; s < NUM_SIMS; s++) {
			winsForX += simulate(game);

		}
		return ((double)winsForX) / NUM_SIMS;
	}

	private double simulate(Game game) {
		// really naive, not re-using tree
		copy.toGame(game);

		while (game.winner == 0) {
			int move = randomMove(game);
			game.move(move);
		}
		double ans = 1;
		if (game.winner == 2) ans = 0;
		else if (game.winner == 3) ans = 0.5;

		game.toGame(copy);
		return ans;
	}
}