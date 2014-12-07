package uk.co.stikman.regexwordsolve.regexprinv;

public abstract class Op {
	private Op		next;
	private Op		prev;
	private int		id;
	private Regex	owner;

	public Op(Regex owner) {
		this.owner = owner;
	}

	public Op getNext() {
		return next;
	}

	public void setNext(Op next) {
		this.next = next;
	}

	public Op getPrev() {
		return prev;
	}

	public void setPrev(Op prev) {
		this.prev = prev;
	}

	protected void printChain(Op op, StructurePrinterOutput output) {
		while (op != null) {
			op.printStructure(output);
			op = op.getNext();
		}

	}

	public abstract void printStructure(StructurePrinterOutput output);

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public void walk(WalkTarget tgt) {
		if (getNext() != null)
			getNext().walk(tgt);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public abstract int getStateCount();

	public Regex getOwner() {
		return owner;
	}

	/**
	 * If it's valid to render to the next position in target then return true.
	 * If not, return false
	 * 
	 * @param state
	 * @param target
	 * @return
	 */
	public abstract boolean render(State state, RenderTarget target);

}
