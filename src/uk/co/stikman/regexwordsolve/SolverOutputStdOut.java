package uk.co.stikman.regexwordsolve;

public class SolverOutputStdOut implements SolverOutput {

	private int	id;

	public SolverOutputStdOut(int i) {
		this.id = i;
	}

	@Override
	public void log(String msg) {
		System.out.println("(" + id + ") Solver: " + msg);
	}

}
