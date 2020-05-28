package io.octolith.indexer;

import java.util.ArrayList;
import java.util.Arrays;

public class IndexSearcherProgram {

    public static void main(String[] args) {
        
        // 2. feladat
        // Index beolvasása és keresés
        
        
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
        
        // Az alábbi parancs az összes fájlt kilistázná, melyben bármelyik kulcsszó előfordul
        //termsIndex.listItems(searchTerms);
        
        // Az alábbi parancs az összes az indexben szereplő kifejezést kilistázná
        //termsIndex.listAllItemsByOccurrence();
        
        
        // Csak azon találatokat adja vissza, melyekben mindkét szó szerepel
        FileOccurrence exactOccurrences = termsIndex.searchExact(searchTerms);
        // találatok listázása
        exactOccurrences.listItems();
        
        // Az összes találatot visszaadja, függetlenül attól, hogy csak egy vagy az összes szó szerepel-e bennük
        FileOccurrence fuzzyOccurrences = termsIndex.searchFuzzy(searchTerms);
        fuzzyOccurrences.listItems();
        
        // 2. feladat vége
    }

}
