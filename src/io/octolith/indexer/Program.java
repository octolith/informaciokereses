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
		//termsIndex.listAllItems();
			
		ArrayList<String> terms = new ArrayList<String>();
		terms.add("monitor");
		terms.add("konfiguráció");
		
		termsIndex.listItems(terms);
		termsIndex.listAllItemsByOccurrence();
		
		FileOccurrence exactOccurrences = termsIndex.searchExact(terms);
		exactOccurrences.listItems();
		
		FileOccurrence expandedOccurrences = termsIndex.searchWithSemanticExpansion(terms);
		expandedOccurrences.listItems();
	}
}
