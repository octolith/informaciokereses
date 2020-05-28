package io.octolith.indexer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bme.mit.iir.ReasoningExample;
import org.bme.mit.iir.Util;
import org.semanticweb.owlapi.model.OWLClass;

public class TermsIndex {
	private HashMap<String, HashMap<String, Integer>> index;
	public static final String PCSHOP_ONTOLOGY_FNAME = "c:/temp/pc_shop/pc_shop.owl";
	
	public HashMap<String, HashMap<String, Integer>> getIndex() {
		return index;
	}

	public TermsIndex() {
		index = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public void addOrUpdateTermWithFileOccurences(String term, String filename, Integer occurrences) {
		if(index.containsKey(term)) {
			index.get(term).put(filename, occurrences);
		}
		else {
			HashMap<String, Integer> occurencesInFile = new HashMap<String, Integer>();
			occurencesInFile.put(filename, occurrences);
			index.put(term, occurencesInFile);
		}
	}
	
	public void printToFile(String fileName) {
	    System.out.println("Fájlba írás");
        System.out.println(fileName);
        
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(fileName));
            
            for(String term: index.keySet()) {
                printWriter.println(term + "                  " + index.get(term).size());
                for(String filename: index.get(term).keySet()) {
                    printWriter.println(filename + "                  " + index.get(term).get(filename));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        finally {
            if(printWriter != null) {
                printWriter.close();
            }
        }
	    
	    System.out.println();
	    System.out.println();
	}
	
	public void readFromFile(String fileName) {
	    System.out.println("Fájlból olvasás");
        System.out.println(fileName);
        
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null && !currentLine.isEmpty()) {
                String[] termWithNumber = currentLine.split("                  ");
                int lines = Integer.parseInt(termWithNumber[1]);
                for(int i = 0; i < lines; i++) {
                    currentLine = bufferedReader.readLine();
                    String[] fileNameWithNumber = currentLine.split("                  ");
                    int occurrences = Integer.parseInt(fileNameWithNumber[1]);
                    this.addOrUpdateTermWithFileOccurences(termWithNumber[0], fileNameWithNumber[0], occurrences);
                }
            }
            
            bufferedReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public FileOccurrence searchExact(ArrayList<String> terms) {
		HashSet<String> setOfTerms = arrayToSet(terms);
		
		FileOccurrence allOccurrences = new FileOccurrence();
		
		for(String term: setOfTerms) {
			HashMap<String, Integer> fileMatch = index.get(term);
			if(fileMatch != null) {
				allOccurrences.addOrUpdate(term, fileMatch);
			}
		}
		
		FileOccurrence exactlyMatchedOccurrences = allOccurrences.getExactlyMatched(terms.size());
		
		return exactlyMatchedOccurrences;
	}
	
	public FileOccurrence searchFuzzy(ArrayList<String> terms) {
        HashSet<String> setOfTerms = arrayToSet(terms);
        
        FileOccurrence allOccurrences = new FileOccurrence();
        
        for(String term: setOfTerms) {
            HashMap<String, Integer> fileMatch = index.get(term);
            if(fileMatch != null) {
                allOccurrences.addOrUpdate(term, fileMatch);
            }
        }
        
        return allOccurrences;
    }
	
	public FileOccurrence searchWithSemanticExpansionBestMatched(ArrayList<String> terms) {
		HashSet<String> setOfTerms = arrayToSet(terms);
		HashSet<String> semanticExtension = new HashSet<String>();
		semanticExtension.addAll(setOfTerms);
		
    	for(String term: setOfTerms) {
    	    ArrayList<String> expandedTerms = expandTerm(term);
            semanticExtension.addAll(expandedTerms);
    	}
		
		FileOccurrence allOccurrences = new FileOccurrence();		
		
		for(String term: semanticExtension) {
			HashMap<String, Integer> fileMatch = index.get(term);
			if(fileMatch != null) {
				allOccurrences.addOrUpdate(term, fileMatch);
			}
		}
	
		FileOccurrence bestMatchedOccurrences = allOccurrences.getBestMatched();
		
		return bestMatchedOccurrences;
	}
	
	private HashSet<String> arrayToSet(ArrayList<String> array) {
		HashSet<String> set = new HashSet<String>();
		
		for(String string: array) {
			set.add(string);
		}
		
		return set;
	}
	
	// Beolvassa a pc-shop OWL ontol�gi�t, majd list�zza a kulsz�hoz tartoz�
	// oszt�ly valamennyi lesz�rmazottj�t �s azok annot�ci�it.
	private ArrayList<String> expandTerm(String term) {
		
	    ArrayList<String> terms = new ArrayList<String>();
	    terms.add(term);
	    
		ReasoningExample p = new ReasoningExample(
    			PCSHOP_ONTOLOGY_FNAME);
    	
        // V�gezz�k keres�sz�-kieg�sz�t�st a kulcssz�ra az oszt�ly lesz�rmazottai szerint!

    	Set<OWLClass> descendants = p.getSubClasses(term, false);
        System.out.println("Query expansion a lesz�rmazottak szerint: ");
    	for (OWLClass cls : descendants) {
    		// Az eredmények közül a beépített OWL entitásokat ki kell sz�rn�nk.
    		// Ezek itt az osztályhierarchia tetejét és alját jelölő
    		// "owl:Thing" és "owl:Nothing" lehetnek.
    		if (!cls.isBuiltIn()) {
                // Kérdezzük le az osztályok címkéit (annotation rdfs:label).
                Set<String> labels = p.getClassAnnotations(cls);
    			System.out.println("\t- "
    					+ term + " -> " + cls.getIRI().getFragment()
                        + " [" + Util.join(labels, ", ") + "]");
    			
    			terms.add(cls.getIRI().getFragment());
    			
    			terms.addAll(labels);
            }
    	}
    	
    	return terms;
	}
	
	public FileOccurrence searchWithSemanticExpansionFuzzyMatched(ArrayList<String> terms) {
        HashSet<String> setOfTerms = arrayToSet(terms);
        HashSet<String> semanticExtension = new HashSet<String>();
        semanticExtension.addAll(setOfTerms);
        
        // TODO: expand list of terms
        
        // Peldanyositsuk a fenti egyszeru Pellet kovetkezteto osztalyt.
        for(String term: setOfTerms) {
            ArrayList<String> expandedTerms = expandTerm(term);
            semanticExtension.addAll(expandedTerms);
        }
        
        FileOccurrence allOccurrences = new FileOccurrence();       
        
        for(String term: semanticExtension) {
            HashMap<String, Integer> fileMatch = index.get(term);
            if(fileMatch != null) {
                allOccurrences.addOrUpdate(term, fileMatch);
            }
        }

        return allOccurrences;
    }
	
	public void listAllItems() {
	    System.out.println("Összes elem listázása:");
	    System.out.println();
		for(String term: index.keySet()) {
			System.out.println(term + ":");
			for(String filename: index.get(term).keySet()) {
				System.out.println("\t" + index.get(term).get(filename) + "\t" + filename);
			}
		}
		System.out.println();
		System.out.println();
	}
	
	public void listItem(String term) {
	    System.out.println("Összes fájl, melyben szerepel az alábbi kifejezés:");
	    System.out.println();
		System.out.println(term + ":");
		for(String filename: index.get(term).keySet()) {
			System.out.println("\t" + index.get(term).get(filename) + "\t" + filename);
		}
		System.out.println();
		System.out.println();
	}
	
	public void listItems(ArrayList<String> terms) {
	    System.out.println("Összes fájl, melyben szerepelnek az alábbi kifejezések:");
	    System.out.println();
		for(String term: terms) {
			System.out.println(term + ":");
			
			HashMap<String, Integer> files = index.get(term);
			if(files != null) {
				for(String filename: files.keySet()) {
					System.out.println("\t" + files.get(filename) + "\t" + filename);
				}
			}
		}
		System.out.println();
		System.out.println();
	}
	
	public void listAllItemsByOccurrence() {
	    
	    System.out.println("Összes szó, előfordulások száma szerint:");
	    System.out.println();
		
		ArrayList<TermOccurrence> items = new ArrayList<TermOccurrence>();

		for(String term: index.keySet()) {
			items.add(new TermOccurrence(term, index.get(term).size()));
		}
		
		Collections.sort(items, Collections.reverseOrder());
		
		for(int i = 0; i < 200; i++) {
			System.out.println(items.get(i).term + " " + items.get(i).numberOfOccurrences);
		}
		System.out.println();
		System.out.println();
	}
}
