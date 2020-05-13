package io.octolith.indexer;

import java.util.HashMap;

public class FileOccurrence {
	private HashMap<String, HashMap<String, Integer>> index;
	
	public HashMap<String, HashMap<String, Integer>> getIndex() {
		return index;
	}
	
	public FileOccurrence() {
		index = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public int size() {
		return index.size();
	}
	
	public HashMap<String, Integer> put(String filename, HashMap<String, Integer> terms) {
		return index.put(filename, terms);
	}
	
	public void addOrUpdate(String term, HashMap<String, Integer> files) {
		for(String filename: files.keySet()) {
			if(index.containsKey(filename)) {
				index.get(filename).put(term, files.get(filename));
			}
			else {
				HashMap<String, Integer> termOccurrences = new HashMap<String, Integer>();
				termOccurrences.put(term, files.get(filename));
				index.put(filename, termOccurrences);
			}
		}
	}
	
	// returns the total number of occurrences for a file
	public Integer getTotalNumberOfOccurrences(String filename) {
		Integer total = 0;
		for(String term: index.get(filename).keySet()) {
			total += index.get(filename).get(term);
		}
		return total;
	}
	
	// returns those files that have the exact number of matched terms
	public FileOccurrence getExactlyMatched(Integer numberOfMatches) {
		FileOccurrence fullyMatched = new FileOccurrence();
		for(String filename: index.keySet()) {
			if(index.get(filename).size() == numberOfMatches.intValue()) {
				fullyMatched.put(filename, index.get(filename));
			}
		}
		return fullyMatched;
	}
	
	// returns the biggest number of occurrences between all files
	public Integer getMaximumNumberOfOccurrences() {
		Integer maximum = 0;
		for(String filename: index.keySet()) {
			Integer numberOfOccurrences = getTotalNumberOfOccurrences(filename);
			
			if(numberOfOccurrences > maximum) {
				maximum = numberOfOccurrences;
			}
		}
		
		return maximum;
	}
	
	// returns those files that have the highest number of matched terms
	public FileOccurrence getBestMatched() {
		
		Integer numberOfMatches = getMaximumNumberOfOccurrences();
		return getExactlyMatched(numberOfMatches);
	}
	
	public void listItems() {
		for(String filename: index.keySet()) {
			System.out.println(filename + ": " + getTotalNumberOfOccurrences(filename));
		}
	}
}
