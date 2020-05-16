package io.octolith.indexer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import org.bme.mit.iir.TermRecognizer;
import org.bme.mit.iir.Util;

public class DirectoryIndexer extends SimpleFileVisitor<Path> {
	
	public TermsIndex termsIndex = new TermsIndex();
	
	public String stopWordsFileName = "C:\\temp\\indexer\\stopwords.txt";

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (attrs.isRegularFile()) {
			
			String filename = file.toString();
            //System.out.println("file: " + filename);
            
            // initialize new instance of TermRecognizer with stopwords
            TermRecognizer termRecognizer;
			try {
				termRecognizer = new TermRecognizer(stopWordsFileName);
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
				return FileVisitResult.TERMINATE;
			}
			
			// read the whole file and recognize terms
			String fileText = Util.readFileAsString(filename);			
			HashMap<String, Integer> fileTerms = (HashMap<String, Integer>) termRecognizer.termFrequency(fileText);
			
			// for each recognized term
			// if the term already exist, add the filename with the number of occurrences to the map
			// otherwise add the term, the filename and the occurrences
			for (String term : fileTerms.keySet()) {
				termsIndex.addOrUpdateTermWithFileOccurences(term, filename, fileTerms.get(term));
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.out.println("File visit failed: " + file.toString());
		return FileVisitResult.TERMINATE;
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public TermsIndex getTermsIndex() {
		return termsIndex;
	}
}
