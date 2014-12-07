package uk.co.stikman.regexwordsolve.regexprinv;

import java.util.ArrayList;
import java.util.List;

public class OrOp extends Op {

	public OrOp(Regex owner) {
		super(owner);
	}

	private List<Op>	choices	= new ArrayList<>();

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), null);
		output.incDepth();
		for (int i = 0; i < choices.size(); ++i) {
			output.print("(choice " + (i + 1) + ")", null);
			output.incDepth();
			printChain(choices.get(i), output);
			output.decDepth();
		}
		output.decDepth();
	}

	public void addChild(Op op) {
		choices.add(op);
	}
	
	public void walk(WalkTarget tgt) {
		tgt.process(this);
		for (Op x : choices)
			x.walk(tgt);
		super.walk(tgt);
	}


}
