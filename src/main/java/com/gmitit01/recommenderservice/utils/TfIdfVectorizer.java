package com.gmitit01.recommenderservice.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.util.*;

/**
 * A utility class that calculates the TF-IDF (Term Frequency-Inverse Document Frequency) vectors
 * for a list of tokens. The class utilizes Apache Lucene for creating an index and
 * performing the TF-IDF calculations.
 */
public class TfIdfVectorizer {

    // Constants
    private final int MIN_TERM_FREQ = 1;

    private StandardAnalyzer analyzer;
    private ByteBuffersDirectory index;
    private List<String> vocabulary;


    /**
     * Constructs a new TfIdfVectorizer with an empty vocabulary.
     */
    public TfIdfVectorizer() {
        this.analyzer = new StandardAnalyzer();
        this.index = new ByteBuffersDirectory();
        this.vocabulary = new ArrayList<>();
    }

    /**
     * Constructs a new TfIdfVectorizer with the provided vocabulary and index.
     *
     * @param vocabulary A list of unique terms to be used as the vocabulary for the vectorizer.
     */
    public TfIdfVectorizer(Map<String, byte[]> serializedIndex, List<String> vocabulary) throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.index = new ByteBuffersDirectory();
        this.vocabulary = vocabulary;
        deserializeIndex(serializedIndex);
    }


    /**
     * Creates an index of the provided amenities list and extracts a vocabulary based on term frequency.
     *
     * @param amenitiesList A list of sets of amenities, where each set represents the amenities of a single document.
     *
     * @throws IOException If there is an error during index creation or vocabulary extraction.
     */
    public void fit(List<Set<String>> amenitiesList) throws IOException {
        createIndex(amenitiesList);
        extractVocabulary();
    }

    /**
     * Creates an index using the provided amenities list.
     *
     * @param amenitiesList A list of sets of amenities to be indexed.
     *
     * @throws IOException If there is an error during index creation.
     */
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

    /**
     * Extracts a vocabulary from the index based on term frequency.
     * A term is included in the vocabulary if its document frequency is greater than
     * or equal to MIN_TERM_FREQ.
     *
     * @throws IOException If there is an error during vocabulary extraction.
     */
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
                if (entry.getValue() >= MIN_TERM_FREQ) {
                    vocabulary.add(entry.getKey());
                }
            }
        }
    }

    /**
     * Transforms a single set of tokens into a TF-IDF vector using the vocabulary.
     *
     * @param amenities A set of tokens to be transformed into a TF-IDF vector.
     *
     * @return A double array representing the TF-IDF vector for the given amenities.
     *
     * @throws IOException If there is an error during the transformation.
     */
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

    public Map<String, byte[]> serializeIndex() throws IOException {
        Map<String, byte[]> serializedIndex = new HashMap<>();
        for (String fileName : index.listAll()) {
            byte[] fileData;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 IndexInput indexInput = index.openInput(fileName, IOContext.DEFAULT)) {
                byte[] buffer = new byte[1024];
                long bytesRead;
                long remainingBytes = indexInput.length();
                while (remainingBytes > 0) {
                    bytesRead = Math.min(remainingBytes, buffer.length);
                    indexInput.readBytes(buffer, 0, (int) bytesRead);
                    baos.write(buffer, 0, (int) bytesRead);
                    remainingBytes -= bytesRead;
                }
                fileData = baos.toByteArray();
            }
            serializedIndex.put(fileName, fileData);
        }
        return serializedIndex;
    }


    public void deserializeIndex(Map<String, byte[]> serializedIndex) throws IOException {
        for (Map.Entry<String, byte[]> entry : serializedIndex.entrySet()) {
            String fileName = entry.getKey();
            byte[] fileData = entry.getValue();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
                 IndexOutput indexOutput = index.createOutput(fileName, IOContext.DEFAULT)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bais.read(buffer)) != -1) {
                    indexOutput.writeBytes(buffer, bytesRead);
                }
            }
        }
    }

    public List<String> getVocabulary() {
        return vocabulary;
    }

    public ByteBuffersDirectory getIndex() {
        return index;
    }
}
