package uk.co.stikman.regexwordsolve.regexprinv;

public class GroupOp extends Op {
	public GroupOp(Regex owner) {
		super(owner);
	}

	private Op	child;
	private int	id;

	public void setChild(Op child) {
		this.child = child;
	}

	public Op getChild() {
		return child;
	}

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), Integer.toString(id));
		output.incDepth();
		printChain(child, output);
		output.decDepth();
	}

	public void walk(WalkTarget tgt) {
		tgt.process(this);
		getChild().walk(tgt);
		super.walk(tgt);
	}

	public void setGroupNumber(int i) {
		this.id = i;
	}

	public int getGroupNumber() {
		return this.id;
	}
}
