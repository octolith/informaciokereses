package io.octolith.indexer;

import java.util.ArrayList;
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
	
	public FileOccurrence searchExact(ArrayList<String> terms) {
		HashSet<String> setOfTerms = arrayToSet(terms);
		
		FileOccurrence allOccurrences = new FileOccurrence();
		
		for(String term: terms) {
			HashMap<String, Integer> fileMatch = index.get(term);
			if(fileMatch != null) {
				allOccurrences.addOrUpdate(term, fileMatch);
			}
		}
		
		FileOccurrence exactlyMatchedOccurrences = allOccurrences.getExactlyMatched(terms.size());
		
		return exactlyMatchedOccurrences;
	}
	
	public FileOccurrence searchWithSemanticExpansion(ArrayList<String> terms) {
		HashSet<String> setOfTerms = arrayToSet(terms);
		HashSet<String> semanticExtension = new HashSet<String>();
		semanticExtension.addAll(setOfTerms);
		
		// TODO: expand list of terms
		
		// Peldanyositsuk a fenti egyszeru Pellet kovetkezteto osztalyt.
    	for(String term: setOfTerms) {
    		expandTerm(term);
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
	
	// Beolvassa a pc-shop OWL ontológiát, majd listázza a kulszóhoz tartozó
	// osztály valamennyi leszármazottját és azok annotációit.
	private void expandTerm(String term) {
		
		ReasoningExample p = new ReasoningExample(
    			PCSHOP_ONTOLOGY_FNAME);
    	
        // Végezzük keresöszó-kiegészítést a kulcsszóra az osztály leszármazottai szerint!

    	Set<OWLClass> descendants = p.getSubClasses(term, false);
        System.out.println("Query expansion a leszármazottak szerint: ");
    	for (OWLClass cls : descendants) {
    		// Az eredmények közül a beépített OWL entitásokat ki kell szürnünk.
    		// Ezek itt az osztályhierarchia tetejét és alját jelölö
    		// "owl:Thing" és "owl:Nothing" lehetnek.
    		if (!cls.isBuiltIn()) {
                // Kérdezzük le az osztály címkéit (annotation rdfs:label).
                Set<String> labels = p.getClassAnnotations(cls);
    			System.out.println("\t- "
    					+ term + " -> " + cls.getIRI().getFragment()
                        + " [" + Util.join(labels, ", ") + "]");
            }
    	}
	}
}
