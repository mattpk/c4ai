package c4ai;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class AI {
	//public int DEPTH = 4;
	private int bestMove = -1;

	private int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public int randomMove(Game game) {
		List<Integer> moves = game.legalMoves();
		int num = randInt(0, moves.size());
		return moves.get(num);
	}

	public int suggestMove(Game game) {
		boolean debug = false;
		if (debug) {
			List<Integer> moves = game.legalMoves();
			boolean isMaximizing = game.isXTurn();
			double best = isMaximizing ? -1000 : 1000;
			for (Integer move : moves) {
				game.move(move);
				double val = minimax(game, Double.MIN_VALUE, Double.MAX_VALUE);
				best = isMaximizing ? Math.max(best, val) : Math.min(best, val);
				System.out.println("move " + (move+1) + ": " + val);
				game.undo();
			}
		}

		minimax(game, Double.MIN_VALUE, Double.MAX_VALUE, true);
		return bestMove;
	}

	// return number between 0 and 1
	private double minimax(Game game, double minGuaranteedForMaximizer, double maxGuaranteedForMinimizer) {
		return minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, false);
	}

	private double minimax(Game game, double minGuaranteedForMaximizer, double maxGuaranteedForMinimizer, boolean first) {
		boolean isMaximizing = game.isXTurn();
		// base cases
		double lengthIncentive = ((double)game.turn) / 1000;
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

		if (isMaximizing) {
			double max = Integer.MIN_VALUE;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer);
				if (ans > max) {
					max = ans;
					if (first) bestMove = move;
				}
				game.undo();

				if (max >= maxGuaranteedForMinimizer) {
					// The minimizer would never let the game get into this state
					// since they already had a better guaranteed score\
					break;
				}
				// update minimum guaranteed for further searching
				minGuaranteedForMaximizer = Math.max(max, minGuaranteedForMaximizer);
			}
			return max;
		} else {
			double min = Integer.MAX_VALUE;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer);
				if (ans < min) {
					min = ans;
					if (first) bestMove = move;
				}
				game.undo();

				if (min <= minGuaranteedForMaximizer) {
					// The maximizer would never let the game get into this state
					// since they already had a better guaranteed score
					break;
				}
				// update max guaranteed for further searching
				maxGuaranteedForMinimizer = Math.min(min, maxGuaranteedForMinimizer);
			}
			return min;
		}
	}
}