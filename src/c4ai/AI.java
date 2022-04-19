package c4ai;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class AI {
	public int DEPTH = 9;
	private int recursions = 0;
	private int bestMove = -1;

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
		bestMove = -1;
		minimax(game, -0.01, 1.01, 0);
		return bestMove;
	}

	private double minimax(Game game, double minGuaranteedForMaximizer, double maxGuaranteedForMinimizer, int depth) {
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
		if (depth >= DEPTH) {
			return monteCarloValue(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer);
		}
		List<Integer> moves = game.legalMoves();
		//preSortMoves(game, moves);

		if (isMaximizing) {
			if (depth == 0);
			double max = 0.0;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, depth+1);
				if (ans > max) {
					max = ans;
					if (depth == 0) bestMove = move;
				}
				game.undo();

				if (ans >= maxGuaranteedForMinimizer) {
					// Any remaining subtrees aren't worth searching
					// because the minimizer has a better option and
					// would never choose this subtree
					break;
				}
				 if (depth > 0)
				 	// update minimum guaranteed for further searching
					minGuaranteedForMaximizer = Math.max(max, minGuaranteedForMaximizer);
				 if (depth == 0)
					debug("move " + (move+1) + ": " + String.format("%.12f", ans));
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

				if (ans <= minGuaranteedForMaximizer && depth > 0) {
					// The maximizer would never let the game get into this state
					// since they already had a better guaranteed score
					break;
				}
				 if (depth > 0)
					// update max guaranteed for further searching
					maxGuaranteedForMinimizer = Math.min(min, maxGuaranteedForMinimizer);
				 if (depth == 0)
					debug("move " + (move+1) + ": " + String.format("%.12f", ans));
			}
			return min;
		}
	}

	private void preSortMoves(Game game, List<Integer> moves) {
		Collections.sort(moves, Comparator.comparing(m -> {
			return -game.simulateCountRays(m);
		}));
	}

	private double monteCarloValue(Game game, double alpha, double beta) {
		return (alpha + beta) / 2.0;
	}
}