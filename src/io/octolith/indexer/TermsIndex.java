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
	
	// Beolvassa a pc-shop OWL ontol�gi�t, majd list�zza a kulsz�hoz tartoz�
	// oszt�ly valamennyi lesz�rmazottj�t �s azok annot�ci�it.
	private void expandTerm(String term) {
		
		ReasoningExample p = new ReasoningExample(
    			PCSHOP_ONTOLOGY_FNAME);
    	
        // V�gezz�k keres�sz�-kieg�sz�t�st a kulcssz�ra az oszt�ly lesz�rmazottai szerint!

    	Set<OWLClass> descendants = p.getSubClasses(term, false);
        System.out.println("Query expansion a lesz�rmazottak szerint: ");
    	for (OWLClass cls : descendants) {
    		// Az eredm�nyek k�z�l a be�p�tett OWL entit�sokat ki kell sz�rn�nk.
    		// Ezek itt az oszt�lyhierarchia tetej�t �s alj�t jel�l�
    		// "owl:Thing" �s "owl:Nothing" lehetnek.
    		if (!cls.isBuiltIn()) {
                // K�rdezz�k le az oszt�ly c�mk�it (annotation rdfs:label).
                Set<String> labels = p.getClassAnnotations(cls);
    			System.out.println("\t- "
    					+ term + " -> " + cls.getIRI().getFragment()
                        + " [" + Util.join(labels, ", ") + "]");
            }
    	}
	}
}
