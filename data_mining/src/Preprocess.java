import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Preprocesses reuters articles in .sgm files in the local directory, creating feature vectors for each.
 * The feature vectors are composed of the words in the xml tags and a frequency list of the top XX rare words
 * in the document body.
 */
public class Preprocess {

  private static final int numberOfBodyWordsInFeatureVector = 5;
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
   * Generates the feature vector (using tf-idf) for a given {@link ArticleData}, pretty prints the feature vector
   * to stdout, in the form "fv<term=rank term=rank ...><place1 place2><topic1 topic2>".
   *
   * @param articleData The article data.
   * @param wordFrequency The combined word frequency map.
   * @param numDocs The total number of documents.
   */
  private static void generateFeatureVector(ArticleData articleData, Map<String, Integer> wordFrequency, int numDocs) {

    // A tree map of words and scores. A custom comparator ensures that duplicate scores don't get overwritten.
    TreeMap<Double, String> map = new TreeMap<Double, String>(new Comparator<Double>() {
      public int compare(Double a, Double b) {
        if (Double.compare(a, b) >= 0 ) {
          return -1;
        } else {
          return 1;
        }
      }
    });

    Queue<Map.Entry<String, Integer>> sortedWordFrequencies = articleData.getSortedWordFrequencies();

    // Get initial word scores.
    for (int i = 0; i < numberOfBodyWordsInFeatureVector; ++i) {
      if (sortedWordFrequencies.peek() != null) {
        Map.Entry<String, Integer> entry = sortedWordFrequencies.poll();
        double score = getWordScore(entry, numDocs, wordFrequency);
        map.put(score, entry.getKey());
      }
    }

    // Add more words if their scores are higher than the lowest score, until a word is found whose score is not
    // higher than the lowest score.
    Map.Entry<String, Integer> nextEntry = sortedWordFrequencies.poll();

    while (nextEntry != null && Double.compare(map.lastKey(), getWordScore(nextEntry, numDocs, wordFrequency)) < 0) {
      map.remove(map.lastKey());
      map.put(getWordScore(nextEntry, numDocs, wordFrequency), nextEntry.getKey());
      nextEntry = sortedWordFrequencies.poll();
    }

    // Print feature vector
    System.out.print("<");
    for (Map.Entry<Double, String> entry : map.entrySet()) {
      System.out.print(" " + entry.getValue() + "=" + entry.getKey());
    }
    System.out.print("><");
    for (String place : articleData.getPlaces()) {
      System.out.print(" " + place);
    }
    System.out.print("><");
    for (String topic : articleData.getTopics()) {
      System.out.print(" " + topic);
    }
    System.out.println(">");
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
    for (ArticleData articleData : articleDataSet) {
      generateFeatureVector(articleData, wordFrequencies, articleDataSet.size());
    }
  }

}