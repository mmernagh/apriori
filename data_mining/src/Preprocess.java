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

  private static Map<String, Integer> combineWordFrequencies(Set<ArticleData> articleDataSet) {
    return null;
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