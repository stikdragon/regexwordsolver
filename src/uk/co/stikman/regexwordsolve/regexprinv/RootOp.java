package uk.co.stikman.regexwordsolve.regexprinv;

public class RootOp extends Op {
	public RootOp(Regex owner) {
		super(owner);
	}

	private Op	child;

	public void setChild(Op child) {
		this.child = child;
	}

	public Op getChild() {
		return child;
	}

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), null);
		output.incDepth();
		printChain(child, output);
		output.decDepth();
	}

	public void walk(WalkTarget tgt) {
		tgt.process(this);
		getChild().walk(tgt);
		super.walk(tgt);
	}



}
