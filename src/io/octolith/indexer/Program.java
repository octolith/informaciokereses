package io.octolith.indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;


public class Program {

	public static void main(String[] args) {
		
		DirectoryIndexer indexer = new DirectoryIndexer();
		
		try {
			Files.walkFileTree(Paths.get("C:\\temp\\indexer\\corpus"), indexer);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		TermsIndex termsIndex = indexer.getTermsIndex();
			
		ArrayList<String> terms = new ArrayList<String>();
		terms.add("Sony");
		terms.add("készülék");
		
		FileOccurrence exactOccurrences = termsIndex.searchExact(terms);
		
		FileOccurrence expandedOccurrences = termsIndex.searchWithSemanticExpansion(terms);
	}
}
