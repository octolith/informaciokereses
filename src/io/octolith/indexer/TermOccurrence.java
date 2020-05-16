package io.octolith.indexer;

public class TermOccurrence implements Comparable<TermOccurrence> {
	public String term;
	public Integer numberOfOccurrences;
	
	public TermOccurrence(String term, Integer numberOfOccurrences) {
		this.term = term;
		this.numberOfOccurrences = numberOfOccurrences;
	}

	@Override
	public int compareTo(TermOccurrence other) {
		return this.numberOfOccurrences - other.numberOfOccurrences;
	}

	
}
