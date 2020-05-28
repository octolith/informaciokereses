package io.octolith.indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;


public class IndexerProgram {

	public static void main(String[] args) {
	    
	    // 1. feladat
        // Index előállítása
	    
	    
	    // Mappaútvonal kiolvasása parancssori paraméterből
	    // ha nincs ilyen, akkor előre meghatározott mappaútvonal
	    String folderpath;		
	    if(args.length == 0) {
	        folderpath = "C:\\temp\\indexer\\corpus";
	    }
	    else {
	        folderpath = args[0];
	    }
	    
	    // Könyvtárszerkezet bejárása saját FileVisitor (DirectoryIndexer) segítségével	    
		DirectoryIndexer indexer = new DirectoryIndexer();
		
		// A DirectoryIndexer a fájlokon végighaladva létrehozza az indexet
		try {
			Files.walkFileTree(Paths.get(folderpath), indexer);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		// A DirectoryIndexerből a bejárás után kinyerjük az indexet		
		TermsIndex oldTermsIndex = indexer.getTermsIndex();
		
		// Majd pedig fájlba írjuk az indexet
		oldTermsIndex.printToFile("index.txt");
		
		// 1. feladat vége
	}
}
