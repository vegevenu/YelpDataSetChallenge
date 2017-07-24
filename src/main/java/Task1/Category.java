package Task1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Class creates a HashMap where every child category gets mapped to its parent category.
 */
public class Category {
    private String name;
    private ArrayList<Category> subcategory;

    public Category(String name) {
        this.name = name;
        subcategory = new ArrayList<Category>();
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Category> getSubcateories() {
        return this.subcategory;
    }

    public void addSubcategories(Category category) {
        this.subcategory.add(category);
    }

    /**
     * This method parses the categories.json file and creates a hashMap
     * @return Map<String,ArrayList<String></>
     * @throws IOException
     */
    public static Map<String, ArrayList<String>> getCategories() throws IOException {
        JSONParser parser = new JSONParser();
        Map<String, ArrayList<String>> map = new HashMap<>();
        try {
            Object obj = parser.parse(new FileReader("categories.json"));
            JSONArray jsonArray = (JSONArray) obj;
            for (Object jsonObj : jsonArray) {
                JSONObject jb = (JSONObject) jsonObj;
                JSONArray parents = (JSONArray) jb.get("parents");
                String subcategory = jb.get("title").toString();
                for (Object object : parents) {
                    if (map.containsKey(object.toString()))
                        map.get(object.toString()).add(subcategory);
                    else {
                        map.put(object.toString(), new ArrayList<String>());
                        map.get(object.toString()).add(subcategory);
                    }
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }
}


