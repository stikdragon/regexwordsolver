package uk.co.stikman.regexwordsolve.regexprinv;

public class AnyCharOp extends Op {

	public AnyCharOp(Regex owner) {
		super(owner);
	}

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), null);
	}

	public void walk(WalkTarget tgt) {
		tgt.process(this);
		super.walk(tgt);
	}


	@Override
	public boolean render(State state, RenderTarget target) {
		int a = state.indexes[getId()]++;
		target.write(getOwner().getCharSet().get(a));
		// TODO Auto-generated method stub
		return false;
	}
}
