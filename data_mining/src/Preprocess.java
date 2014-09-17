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
   * Creates a Map<String, Integer> mapping all terms from all documents to the number of documents that each term
   * appears in.
   *
   * @param articleDataSet The ArticleData
   * @return The combined word frequency map.
   */
  private static Map<String, Integer> generateWordFrequencies(final Set<ArticleData> articleDataSet) {
    Map<String, Integer> map = new HashMap<String, Integer>();

    for (ArticleData data : articleDataSet) {
      for (String word : data.getWordFrequencies().keySet()) {
        if (map.containsKey(word)) {
          map.put(word, map.get(word) + 1);
        } else {
          map.put(word, 1);
        }
      }
    }

    return map;
  }

  /**
   * Generates a tf-idf score for a term. tf(x) = count of occurrences of the word in the document.
   * idf(x) = log (total number of documents / number of documents that contain the term ).
   * The tf-idf score of x is tf(x) * idf(x).
   *
   * @param localWordFrequency The count of the occurrences of the term in the document.
   * @param numberOfDocs The total number of documents.
   * @param combinedWordFrequencies A Map<String, Integer> of terms to the number of documents containing that term.
   * @return The tf-idf score.
   */
  private static double getWordScore(Map.Entry<String, Integer> localWordFrequency, int numberOfDocs,
                                     Map<String, Integer> combinedWordFrequencies) {
    return localWordFrequency.getValue()
        * Math.log((double) numberOfDocs / combinedWordFrequencies.get(localWordFrequency.getKey()));
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

    Map<String, Integer> wordFrequencies = generateWordFrequencies(articleDataSet);

  }

}