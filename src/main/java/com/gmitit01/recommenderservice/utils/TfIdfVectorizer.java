package com.gmitit01.recommenderservice.utils;

import lombok.Data;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;


public class TfIdfVectorizer {

    private StandardAnalyzer analyzer;
    private ByteBuffersDirectory index;
    private List<String> vocabulary;

    public TfIdfVectorizer() {
        this.analyzer = new StandardAnalyzer();
        this.index = new ByteBuffersDirectory();
        this.vocabulary = new ArrayList<>();
    }

    public TfIdfVectorizer(List<String> vocabulary) {
        this.analyzer = new StandardAnalyzer();
        this.index = new ByteBuffersDirectory();
        this.vocabulary = vocabulary;
    }

    public void fit(List<Set<String>> amenitiesList) throws IOException {
        createIndex(amenitiesList);
        extractVocabulary();
    }

    private void createIndex(List<Set<String>> amenitiesList) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(index, config)) {
            for (Set<String> amenities : amenitiesList) {
                Document doc = new Document();
                String amenitiesText = String.join(", ", amenities);
                doc.add(new TextField("amenities", amenitiesText, Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }

    private void extractVocabulary() throws IOException {
        try (IndexReader reader = DirectoryReader.open(index)) {
            Map<String, Integer> termFreqMap = new HashMap<>();
            for (LeafReaderContext context : reader.leaves()) {
                LeafReader leafReader = context.reader();
                Terms terms = leafReader.terms("amenities");
                if (terms != null) {
                    TermsEnum termsEnum = terms.iterator();
                    BytesRef term;
                    while ((term = termsEnum.next()) != null) {
                        String termString = term.utf8ToString();
                        int docFreq = termsEnum.docFreq();
                        termFreqMap.put(termString, termFreqMap.getOrDefault(termString, 0) + docFreq);
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : termFreqMap.entrySet()) {
                if (entry.getValue() >= 5) {
                    vocabulary.add(entry.getKey());
                }
            }
        }
    }


    public double[] transform(Set<String> amenities) throws IOException {
        double[] tfIdfVector = new double[vocabulary.size()];
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            for (int i = 0; i < vocabulary.size(); i++) {
                String term = vocabulary.get(i);
                TermQuery query = new TermQuery(new Term("amenities", term));
                TopDocs topDocs = searcher.search(query, amenities.size());
                double tfIdf = topDocs.totalHits.value * Math.log((double) reader.numDocs() / topDocs.totalHits.value);
                tfIdfVector[i] = tfIdf;
            }
        }
        return tfIdfVector;
    }

    public List<String> getVocabulary() {
        return vocabulary;
    }
}
