package c4ai;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class AI {
	private int maxDepth = 6;
	private int recursions = 0;
	private int bestMove = -1;

	public int actualTurnNum = -1;
	
	private List<double[]> scoredMoves;
	private Game copy;

	public AI(){}
	public AI(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	private void debug(String s) {
		if (false) {
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
		minimax(game, 0, 1, false, 0);
		return bestMove;
	}

	public List<double[]> suggestMoves(Game game) {
		copy = new Game(game);
		scoredMoves = new ArrayList<>(7);
		minimax(game, 0, 1, true, 0);
		Collections.sort(scoredMoves,
			Comparator.comparing(
			(double[] m) -> 0-m[1])
		);
		return scoredMoves;
	}

	private double minimax(Game game, double minGuaranteedForMaximizer, double maxGuaranteedForMinimizer,
						   boolean recordAllScores, int depth) {
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
		if (maxDepth > 4) {
			moves = preSort(game, moves);
		}

		if (depth >= maxDepth) {
			if (maxDepth <= 2) {
				return (minGuaranteedForMaximizer + maxGuaranteedForMinimizer)/2;
			}
			if (((copy.turn > 12 && depth < maxDepth + 1)
				|| (copy.turn > 17 && depth < maxDepth + 2)
				|| (copy.turn > 24 && depth < maxDepth + 3)
				|| (copy.turn > 30 && depth < maxDepth + 4)
				|| (copy.turn > 42 && depth < maxDepth + 5))) {
			} else {
				return monteCarloValue(game);
			}
		}

		if (isMaximizing) {
			double max = 0.0;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, false, depth+1);
				if (recordAllScores) {
					scoredMoves.add(new double[]{move, ans});
				}
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

				 	// update minimum guaranteed for further searching
					minGuaranteedForMaximizer = Math.max(max, minGuaranteedForMaximizer);
				if (depth == 0) {
					// is inaccurate when pruned
					debug("move " + (move+1) + ": " + String.format("%.12f", ans));
				}
			}
			return max;
		} else {
			double min = 1.00;
			for (Integer move : moves) {
				game.move(move);
				double ans = minimax(game, minGuaranteedForMaximizer, maxGuaranteedForMinimizer, false, depth+1);
				if (recordAllScores) {
					scoredMoves.add(new double[]{move, ans});
				}
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

				// update max guaranteed for further searching
				maxGuaranteedForMinimizer = Math.min(min, maxGuaranteedForMinimizer);
				if (depth == 0) {
					// is inaccurate when pruned
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

	private List<Integer> preSort(Game game, List<Integer> moves) {
		// do a 2 step minimax
		AI ai = new AI(2);
		List<double[]> scoredMoves = ai.suggestMoves(game);
		List<Integer> list = new ArrayList<>(scoredMoves.size());
		for (int i = 0; i < scoredMoves.size(); i++) {
			list.add((int) scoredMoves.get(i)[0]);
		}
		return list;
	}
}