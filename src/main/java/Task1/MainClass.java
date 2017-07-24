package Task1;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the driver class for our project.
 */
public class MainClass {

    public static void main(String[] args) {

        makeIndex();
        List<String> businessIdList = searchInBusinessIndex("businessIndex");
        makeFiles("reviewIndex", businessIdList);

        makeCategoryVsBusinessIdMap();

        makePOSMalletIndex();

        searchInIndex("testCategory");
    }

    /**
        This method is used to create POS and Mallet Index. An enriched Index.
     */
    private static void makePOSMalletIndex() {
        String pathToReviewDirectory = "trainCategory";

        POSIndex posIndex = new POSIndex(pathToReviewDirectory, "POSAndReviewIndex");

        posIndex.makeIndex();

        System.out.println("Index has been made");
    }

    /**
     * This method is a helper method to print all field names in the index.
     */
    private static void getAllFields() {
        Search search = new Search("reviewIndex");
        List<String> allFieldsNames = search.getAllFieldsNames();

        for (String allFieldsName : allFieldsNames) {
            System.out.println(allFieldsName);
        }

    }

    /**
     * This method will search in train index for all test data and print cumulative precision and recall.
     *
     * @param directoryName
     */
    private static void searchInIndex(String directoryName) {

        List<String> allTestStrings = getAllStrings(directoryName);

        double[] precisionTestList = new double[allTestStrings.size()];
        double[] recallTestList = new double[allTestStrings.size()];
        double[] precisionPosList = new double[allTestStrings.size()];
        double[] recallPosList = new double[allTestStrings.size()];

        List<String> tags = new ArrayList<>(Arrays.asList("NN", "NNS", "NNPS", "NNP", "JJ", "POS", "FW"));
        List<String> predictedCategories = new ArrayList<>();

        int index = 0;
        Search search = new Search("POSAndReviewIndex");
        Search reviewIndexSearch = new Search("reviewIndex");
        MaxentTagger tagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
        POSTagger posTagger = new POSTagger(tagger);
        Evaluation evaluation = new Evaluation(reviewIndexSearch);

        List<Document> testQueryHits = new ArrayList<>();
        List<Document> posHits = new ArrayList<>();

        for (String testString : allTestStrings) {

            predictedCategories.clear();

            if (testString.length() > 10) {
                String posString = getPOSString(posTagger, testString, tags);

                evaluation.getTrueCategories(testString);

                Query testQuery = search.getTextQuery("review", testString);
                if (posString.length() == 0) {
                    posString = testString;
                }
                Query posQuery = search.getTextQuery("review", posString);

                System.out.println("---------------------");
                testQueryHits.clear();
                testQueryHits = search.findHits(3, testQuery);
                printHits(testQueryHits);
                predictedCategories = getPredictedCategories(testQueryHits, predictedCategories);

                double precisionTest = evaluation.getPrecision(predictedCategories);
                double recallTest = evaluation.getRecall(predictedCategories);
                System.out.println(precisionTest);
                System.out.println(recallTest);
                precisionTestList[index] = precisionTest;
                recallTestList[index] = recallTest;

                System.out.println("------------------------");
                posHits.clear();
                posHits = search.findHits(7, posQuery);
                printHits(posHits);
                predictedCategories.clear();
                predictedCategories = getPredictedCategories(posHits, predictedCategories);

                double precisionPos = evaluation.getPrecision(predictedCategories);
                double recallPos = evaluation.getRecall(predictedCategories);
                System.out.println(precisionPos);
                System.out.println(recallPos);
                precisionPosList[index] = precisionPos;
                recallPosList[index] = recallPos;

                index++;
            }
        }

        printAvg(precisionTestList);
        printAvg(recallTestList);
        printAvg(precisionPosList);
        printAvg(recallPosList);
        System.out.println(evaluation.atleastOneCorrect);
    }

    /**
     * This method will print avg of all the precision and recall.
     *
     * @param list
     */
    private static void printAvg(double[] list) {
        double sum = 0.0;
        for (Double aDouble : list) {
            sum = sum + aDouble;
        }

        System.out.println(sum / list.length);
    }

    /**
     * This method will return all test reviews from a particular test file.
     *
     * @param fileName
     * @return
     */
    private static List<String> getAllStrings(String fileName) {
        fileName = "Hospitals.txt";
        Path path = Paths.get("testCategory", fileName);
//        Path path = Paths.get(".", fileName);

        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path.toFile()));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(stringBuilder.toString().split("~~~"));
    }

    /**
     * This method will return the POS string for a given input test string.
     *
     * @param posTagger
     * @param testString
     * @param tags
     * @return
     */
    private static String getPOSString(POSTagger posTagger, String testString, List<String> tags) {
        String posString = testString;

        try {
            posString = posTagger.tag(testString, (ArrayList<String>) tags);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(posString);
        return posString;
    }

    /**
     * This method will return the predicted queries by algorithm.
     *
     * @param testQueryHits
     * @param predictedCategories
     * @return
     */
    private static List<String> getPredictedCategories(List<Document> testQueryHits, List<String> predictedCategories) {
        for (Document testQueryHit : testQueryHits) {
            String category = testQueryHit.get("category").replace(".txt", "");
            if (category.equals("Restaurants1") || category.equals("Restaurants2")) {
                if (!predictedCategories.contains("Restaurants")) {
                    predictedCategories.add("Restaurants");
                }
            } else {
                predictedCategories.add(category);
            }
        }

        if (!predictedCategories.contains("Restaurants")) {
            predictedCategories.remove(predictedCategories.size() - 1);
            predictedCategories.add("Restaurants");
        }

        return predictedCategories;
    }

    /**
     * This method will print the hits given the list of documents.
     *
     * @param hits
     */
    private static void printHits(List<Document> hits) {
        for (Document hit : hits) {
            System.out.println(hit.get("category"));
        }
    }

    /**
     * This method is used to search in business index.
     *
     * @param businessIndex
     * @return
     */
    private static List<String> searchInBusinessIndex(String businessIndex) {
        Search searchInReview = new Search(businessIndex);
        return searchInReview.makeUniqueIdList();
    }

    /**
     * This method is used to make businessId vs review text
     */
    private static void makeIndex() {
        LuceneIndexWriter luceneIndexWriter;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Please provide the path to file to index ");
            String pathToFile = bufferedReader.readLine();
//            String pathToFile = "yelp_academic_dataset_business.json";
            luceneIndexWriter = new LuceneIndexWriter(pathToFile, "businessIndexWithCategories");
            parseAndMakeIndex(pathToFile, luceneIndexWriter, "business_id");
            luceneIndexWriter.finish();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to make category name vs business Id map.
     */
    private static void makeCategoryVsBusinessIdMap() {
        String pathToBusinessFile = "businessIndexWithCategories";

        Search search = new Search(pathToBusinessFile);
        search.readIndex();
        List<String> categoryList = search.makeUniqueCategoryList();
        search.makeCategoryBusinessIdMap(categoryList);


    }

    /**
     * This method searches in tip and review index
     *
     * @param indexPath
     * @param businessIdList
     */
    private static void makeFiles(String indexPath, List<String> businessIdList) {
        Search searchInReview = new Search(indexPath);
        searchInReview.makeFiles(businessIdList);

        Search searchInTip = new Search("tipIndex");
        searchInTip.readIndex();
    }

    /**
     * This method makes index of 1.6 million records by reading 10,000 lines at a time. (Batch processing).
     *
     * @param pathToJsonFile
     * @param luceneIndexWriter
     * @param review
     * @throws IOException
     * @throws ParseException
     */
    private static void parseAndMakeIndex(String pathToJsonFile, LuceneIndexWriter luceneIndexWriter, String review) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(new File(pathToJsonFile)));
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        String line;

        int index = 1;
        while ((line = br.readLine()) != null) {
            if (jsonObjects.size() < 10000) {
                jsonObjects.add((JSONObject) jsonParser.parse(line));
            } else if (jsonObjects.size() == 10000) {
                makeIndex(index, jsonObjects, luceneIndexWriter, review);
                jsonObjects.clear();
                jsonObjects.add((JSONObject) jsonParser.parse(line));
                index++;
            }
        }
        if (jsonObjects.size() < 10000) {
            makeIndex(index, jsonObjects, luceneIndexWriter, review);
        }
    }

    /**
     * This method calls the generic class to make index.
     *
     * @param index
     * @param jsonObjects
     * @param luceneIndexWriter
     * @param fieldName
     */
    private static void makeIndex(int index, ArrayList<JSONObject> jsonObjects, LuceneIndexWriter luceneIndexWriter, String fieldName) {
        System.out.println(index);
        System.out.println(jsonObjects.size());

        luceneIndexWriter.createIndex(jsonObjects, fieldName);
    }
}
