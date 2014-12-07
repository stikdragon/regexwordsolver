package uk.co.stikman.regexwordsolve.regexprinv;

public interface StructurePrinterOutput {

	void print(String name, String detail);
	void incDepth();
	void decDepth();

}
