package uk.co.stikman.regexwordsolve;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.regexwordsolve.regexprinv.Regex;

public class Main {

	private static final int	THREAD_COUNT	= 8;

	public static void main(String[] args) {

		
		Regex re = new Regex("1([ABC]|2)+(A?).*");
//		Regex re = new Regex("A|(S(B))|C");
		re.getRoot().printStructure(new StructurePrinterImpl());
		
		
//		Main m = new Main();
//		m.run("Volapük");
//		m.run("Cities");
//		m.run(null);

	}

	/**
	 * Run with <code>null</code> as a parameter and it'll do all of them,
	 * otherwise it'll just do those in the group
	 * 
	 * @param group
	 */
	private void run(String group) {

		PuzzleDownloader downloader = new PuzzleDownloader();
		List<Puzzle> puzzles = downloader.go();

		List<Puzzle> working = new ArrayList<Puzzle>();
		for (Puzzle p : puzzles)
			if (group == null || p.getGroup().equals(group))
				working.add(p);
		final List<Puzzle> pending = new ArrayList<>(working);

		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < THREAD_COUNT; ++i) {
			final int id = i;
			threads.add(new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						Puzzle p;
						synchronized (pending) {
							if (pending.isEmpty())
								break;
							p = pending.remove(0);
						}
						SolverOutput out = new SolverOutputStdOut(id);
						try {
							solvePuzzle(p, out);
						} catch (TookTooLongException ttle) {
							p.setStatus(Status.TIMEOUT);
						} catch (Throwable th) {
							p.setStatus(Status.ERROR, th.toString());
							th.printStackTrace();
						}
					}
				}
			}));
		}

		for (Thread t : threads)
			t.start();

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Finished!");

		for (Puzzle puzz : working)
			System.out.println(puzz.toString());

	}

	private void solvePuzzle(Puzzle puzz, SolverOutput output) throws TookTooLongException {
		Solver solver = new Solver(puzz.getWidth(), puzz.getHeight());
		for (PuzzleRegex pr : puzz.getClues())
			solver.addClue(pr.getIndex(), pr.getRegex(), pr.getSide());
		solver.setAvailableCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?-/*.\\ ',:\"$£%^&()[]{}<>");
		solver.setOutput(output);
		solver.setTimeout(1000);
		long start = System.currentTimeMillis();
		for (String s : solver.solve())
			puzz.addSolution(s);
		long dt = System.currentTimeMillis() - start;
		if (puzz.getSolutions().isEmpty())
			puzz.setStatus(Status.NOSOLUTIONS, "Took " + dt + "ms");
		else
			puzz.setStatus(Status.SOLVED, "Took " + dt + "ms");
	}
}
