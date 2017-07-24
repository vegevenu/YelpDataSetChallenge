package Task1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * This class is a which adds POS tagged words in lucene.
 */
public class POSIndex {


    public StandardAnalyzer analyzer;
    public String pathToDirectory;
    public String indexPath;
    public IndexWriter indexWriter;
    public Directory dir;
    public IndexWriterConfig iwc;
    public String pathToReviewDirectory;

    public POSIndex(String pathToReviewDirectory, String indexPath) {
        try {
            this.pathToReviewDirectory = pathToReviewDirectory;
            this.indexPath = indexPath;
            this.dir = FSDirectory.open(Paths.get(indexPath));
            this.analyzer = new StandardAnalyzer();
            this.iwc = new IndexWriterConfig(analyzer);
            this.iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            this.indexWriter = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method makes lucene index with 60% text and probable words given mallet.
     */
    public void makeIndex() {
        File file = new File(this.pathToReviewDirectory);
        List<String> tags = new ArrayList<>(Arrays.asList("NN", "NNS", "NNPS", "NNP", "JJ", "POS", "FW"));

        if (file.isDirectory()) {
            for (File categoryFile : file.listFiles()) {

                String categoryName = categoryFile.getName();
                if (categoryName.endsWith(".txt")) {
                    System.out.println(categoryName);

                    String reviewString = getReviewString(categoryName, pathToReviewDirectory);

                    String malletString = getMalletString(categoryName);

                    addToLucene(categoryName, reviewString, malletString);
                }
            }
            finish();
        }

    }

    /**
     * This method returns all important words returned by Mallet for a particular word.
     * @param categoryName
     * @return malletString
     *
     */
    private String getMalletString(String categoryName) {
        File malletFile;

        if (categoryName.contains("Restaurants")) {
            malletFile = new File("malletOutput" + File.separator + "Restaurants_keys.txt");
        } else {
            malletFile = new File("malletOutput" + File.separator + categoryName.replace(".txt", "").replace(" ", "_") + "_keys.txt");
        }
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(malletFile));
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\\t");

                stringBuilder.append(split[2]).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * This method returns nouns and adjectives for a particular string
     * @param tags
     * @param categoryFile
     * @return
     */
    private String getNounsAdjectives(List<String> tags, String categoryFile) {
//        POSTagger posTagger = new POSTagger();
        File file = new File(pathToReviewDirectory + File.separator + categoryFile);
        StringBuilder posStringBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String posString;
            while ((line = br.readLine()) != null) {
//                posString = posTagger.tag(line, (ArrayList<String>) tags);
//                posStringBuilder.append(posString);
            }
            System.out.println(posStringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posStringBuilder.toString();
    }

    /**
     * This method returns 60% text for a particular category.
     * @param categoryFile
     * @param pathToReviewDirectory
     * @return
     */
    private String getReviewString(String categoryFile, String pathToReviewDirectory) {
        File file = new File(pathToReviewDirectory + File.separator + categoryFile);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * This method creates a lucene document and adds it to lucene.
     * @param categoryName
     * @param reviewText
     * @param malletString
     */
    private void addToLucene(String categoryName, String reviewText, String malletString) {

        try {
            Document document = new Document();
            document.add(new StringField("category", categoryName, Field.Store.YES));
            document.add(new TextField("review", reviewText, Field.Store.YES));
            document.add(new TextField("mallet", malletString, Field.Store.YES));

            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method commits all documents to lucene.
     */
    public void finish() {
        try {
            indexWriter.forceMerge(1);
            indexWriter.commit();
            indexWriter.close();
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("We had a problem closing the index: " + e.getMessage());
        }
    }

}
