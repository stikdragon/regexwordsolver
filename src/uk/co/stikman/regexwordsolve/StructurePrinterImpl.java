package uk.co.stikman.regexwordsolve;

import uk.co.stikman.regexwordsolve.regexprinv.StructurePrinterOutput;

public class StructurePrinterImpl implements StructurePrinterOutput {

	private int depth;
	
	@Override
	public void print(String name, String detail) {
		for (int i = 0; i < depth * 3; ++i)
			System.out.print(" ");
		if (detail != null)
			System.out.println(name + " - " + detail);
		else
			System.out.println(name);
	}

	@Override
	public void incDepth() {
		++depth;
	}

	@Override
	public void decDepth() {
		--depth;
	}

}
