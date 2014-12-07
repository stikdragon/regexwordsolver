package uk.co.stikman.regexwordsolve.regexprinv;

public class CountOp extends Op {

	private Op	child;
	private int	min	= 0;
	private int	max	= -1;	// means infinite

	
	
	/**
	 * @param min
	 * @param max
	 */
	public CountOp(Regex owner, int min, int max) {
		super(owner);
		this.min = min;
		this.max = max;
	}

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), min + " to " + (max == -1 ? "inf" : max));
		output.incDepth();
		printChain(child, output);
		output.decDepth();
	}

	public void setChild(Op child) {
		this.child = child;
	}

	public Op getChild() {
		return child;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	
	public void walk(WalkTarget tgt) {
		tgt.process(this);
		getChild().walk(tgt);
		super.walk(tgt);
	}

	@Override
	public int getStateCount() {
		if (getMax() == -1) 
			return getOwner().getMaxLength() - getMin();
		else
			return Math.min(getOwner().getMaxLength(), getMax()) - getMin();
	}

	
}
