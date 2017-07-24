package Task1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates precision and recall.
 *
 * */
public class Evaluation {

    public List<String> trueCategories;
    public Search search;
    public QueryParser queryParser;
    public Search businessIndexSearch;
    public int atleastOneCorrect;

    public Evaluation(Search search) {
        this.search = search;
        this.search.searcher.setSimilarity(new DefaultSimilarity());
        this.queryParser = new QueryParser("REVIEW", new StandardAnalyzer());
        this.businessIndexSearch = new Search("businessIndexWithCategories");
        this.trueCategories = new ArrayList<>();
        this.atleastOneCorrect = 0;
    }

    /**
     * This method returns trueCategories for a particular review
     * @param review
     */
    public void getTrueCategories(String review) {

        Query query = null;
        try {
            query = queryParser.parse(QueryParser.escape(review));
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

//        query = search.getTermQuery("REVIEW",review);
        List<Document> hits = search.findHits(1, query);
        String business_Id = hits.get(0).get("business_id");
        System.out.println(business_Id);

        getCategories(business_Id);

    }

    /**
     * This method finds all the categories for a given a business_id.
     * @param business_id
     */
    private void getCategories(String business_id) {
        this.trueCategories.clear();
        Query query = this.businessIndexSearch.getTermQuery("business_id", business_id);
        int count = 0;

        try {
            count = this.businessIndexSearch.getCount(query);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Document> docs = this.businessIndexSearch.findHits(count, query);

        for (Document doc : docs) {
            this.trueCategories.add(doc.get("category"));
        }

        for (String category : this.trueCategories) {
            System.out.println(category);
        }
    }

    /**
     * This method calculates precision
     * @param predictedCategories
     * @return precision
     */
    public double getPrecision(List<String> predictedCategories) {

        int truePositive = getTruePositive(predictedCategories);

        if (truePositive > 0) {
            this.atleastOneCorrect++;
        }

        int falsePositive = predictedCategories.size() - truePositive;


        return ((double) truePositive / (truePositive + falsePositive));
    }

    /**
     * This method calculates truePositives given the predictedCategories
     * @param predictedCategories
     * @return
     */
    private int getTruePositive(List<String> predictedCategories) {
        int truePositive = 0;
        for (String predictedCategory : predictedCategories) {
            if (this.trueCategories.contains(predictedCategory)) {
                truePositive++;
            }
        }

        return truePositive;

    }

    /**
     * This method calculates Recall
     * @param predictedCategories
     * @return
     */
    public double getRecall(List<String> predictedCategories) {
        int truePositive = getTruePositive(predictedCategories);

        int falseNegative = this.trueCategories.size() - truePositive;


        return ((double) truePositive / (truePositive + falseNegative));
    }
}
