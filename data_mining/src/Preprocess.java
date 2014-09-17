import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Preprocesses reuters articles in .sgm files in the local directory, creating feature vectors for each.
 * The feature vectors are composed of the words in the xml tags and a frequency list of the top XX rare words
 * in the document body.
 * // TODO(mernagh) consider adding td-idf.
 */
public class Preprocess {

  private static final String pathToArticles = "/home/0/srini/WWW/674/public/reuters";

  /**
   * Combines all the word frequencies of ArticleData into a single combined frequency map.
   *
   * @param articleDataSet The ArticleData
   * @return The combined word frequency map.
   */
  private static Map<String, Integer> combineWordFrequencies(final Set<ArticleData> articleDataSet) {
    Map<String, Integer> map = new HashMap<String, Integer>();

    for (ArticleData data : articleDataSet) {
      for (Map.Entry<String, Integer> entry : data.getWordFrequencies().entrySet()) {
        String key = entry.getKey();
        if (map.containsKey(key)) {
          map.put(key, map.get(key) + entry.getValue());
        } else {
          map.put(key, entry.getValue());
        }
      }
    }

    return map;
  }
  /**
   * Reads and parses the reuters articles, outputting feature vector representations of each.
   *
   * @param args
   */
  public static void main(String[] args) {

    Set<ArticleData> articleDataSet = new HashSet<ArticleData>();

    // Parse all the files in the given directory
    try {
      Files.walkFileTree(Paths.get(pathToArticles),new HashSet<FileVisitOption>() /* no file options */, 1 /* depth */,
          new ParsingFileVisitor(articleDataSet));
    } catch (IOException e) {
      e.printStackTrace();
    }

    Map<String, Integer> wordFrequencies = combineWordFrequencies(articleDataSet);
  }

}