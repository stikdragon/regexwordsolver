package uk.co.stikman.regexwordsolve.regexprinv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CharSetOp extends Op {

	private List<Character>	chars	= new ArrayList<>();
	private boolean			invert;

	public CharSetOp(Regex owner, char ch) {
		super(owner);
		chars.add(ch);
	}

	public CharSetOp(Regex owner, Set<Character> set, boolean invert) {
		super(owner);
		chars.addAll(set);
		this.invert = invert;
	}

	public List<Character> getChars() {
		return chars;
	}

	public boolean isInvert() {
		return invert;
	}

	@Override
	public void printStructure(StructurePrinterOutput output) {
		output.print(getClass().getSimpleName(), chars.toString());
	}
	
	public void walk(WalkTarget tgt) {
		tgt.process(this);
		super.walk(tgt);
	}

	@Override
	public int getStateCount() {
		return chars.size();
	}


}
