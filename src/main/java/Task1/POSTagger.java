package Task1;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is a generic class which returns POS tagged for a given string.
 */
public class POSTagger {

    public MaxentTagger tagger;

    public POSTagger(MaxentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * This method returns POS tagged string for a given input string.
     * @param str
     * @param tags
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public String tag(String str, ArrayList<String> tags) throws IOException, ClassNotFoundException {

        String tagged = tagger.tagString(str);

        StringBuilder stringBuilder = new StringBuilder();

        String[] split = tagged.split(" ");

        for (String s : split) {
            int lastIndex = s.lastIndexOf("_");
            if (tags.contains(s.substring(lastIndex + 1))) {
                String substring = s.substring(0, lastIndex);
                stringBuilder.append(substring + " ");
            }
        }

        return stringBuilder.toString();

    }

}
