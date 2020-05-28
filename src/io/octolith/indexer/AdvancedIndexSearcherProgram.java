package io.octolith.indexer;

import java.util.ArrayList;
import java.util.Arrays;

public class AdvancedIndexSearcherProgram {

    public static void main(String[] args) {

        // 3. feladat
        // Kiterjesztett keresés
        
        // index beolvasása fájlból
        TermsIndex termsIndex = new TermsIndex();
        termsIndex.readFromFile("index.txt");
        
        // keresési szavak kiolvasása parancssori parancssori paraméterekből
        // illetve ha hiányoznak, akkor alapértelmezett keresési szavak használata
        ArrayList<String> searchTerms = new ArrayList<String>();
        if(args.length != 0) {
            searchTerms.addAll(Arrays.asList(args));
        }
        else {
            searchTerms.add("monitor");
            searchTerms.add("konfiguráció");
        }
        
        // Csak azon találatokat adja vissza, melyekben a lehető
        // legtöbb szavak összes előfordulási száma
        // az ontológia alapján kiegészített lista szerint
        FileOccurrence expandedBestMatchedOccurrences = termsIndex.searchWithSemanticExpansionBestMatched(searchTerms);
        expandedBestMatchedOccurrences.listItems();
        
        // Az összes találatot visszaadja, függetlenül attól,
        // hogy csak egy vagy az összes szó szerepel-e bennük
        // az ontológia alapján kiegészített lista szerint
        FileOccurrence expandedFuzzyMatchedOccurrences = termsIndex.searchWithSemanticExpansionFuzzyMatched(searchTerms);
        expandedFuzzyMatchedOccurrences.listItems();
        
        // 3. feladat vége
    }

}
