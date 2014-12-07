package uk.co.stikman.regexwordsolve.regexprinv;

class OpSequence {

	private Op	first;
	private Op	last;

	public Op getFirst() {
		return first;
	}

	public Op getLast() {
		return last;
	}

	public void append(Op o) {
		if (o == null)
			throw new NullPointerException("Op cannot be null");
		if (contains(o))
			throw new IllegalArgumentException("Op " + o + " already in sequence");
		if (first == null) {
			first = o;
			last = o;
		} else {
			last.setNext(o);
			o.setPrev(last);
			last = o;
		}
	}

	public boolean contains(Op o) {
		if (first == null || o == null)
			return false;
		Op x = first;
		while (x != null) {
			if (x.equals(o))
				return true;
			x = x.getNext();
		}
		return false;
	}

	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Replace the last Op in the sequence with this one, then return the replaced one
	 * @param x
	 * @return
	 */
	public Op replaceLast(Op x) {
		if (last == null)
			throw new NullPointerException();
		Op tmp = last;
		if (last.getPrev() == null) { 
			// 
			// We're replacing the first one
			//
			first = x;
			last = x;
		} else {
			//
			// Wasn't the last one, so update pointers
			//
			last.getPrev().setNext(x);
			x.setPrev(last.getPrev());
			last = x;
		}
		return tmp;
	}



}
