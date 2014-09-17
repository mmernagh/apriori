import java.util.*;

/**
 * An ArticleData stores information about an article, including its topics, places, and a frequency list
 * of the rare words in the article body. The frequency list is provided as PriorityQueue in which
 * words with a higher frequency are before words with a lower frequency. Once the PriorityQueue is obtained,
 * no further words can be added to the ArticleData.
 *
 * Author: mernagh
 */
public class ArticleData {

  /**
   * EntryComparator compares map entries based on their value. A larger value precedes a smaller value.
   */
  private class EntryComparator implements Comparator<Map.Entry<String, Integer>> {

    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
      if (o1 == o2) {
        return 0;
      }

      if (o1 != null) {
        if (o2 != null) {
          return o2.getValue() - o1.getValue();
        } else {
          return -1;
        }
      } else {
        return 1;
      }
    }
  }
  private Set<String> places;
  private Set<String> topics;
  private Map<String, Integer> wordFrequencies;
  private Queue<Map.Entry<String, Integer>> sortedWordFrequencies = null;
  private boolean isSorted;


  public ArticleData() {
    places = new HashSet<String>();
    topics = new HashSet<String>();
    wordFrequencies = new HashMap<String, Integer>();
    isSorted = false;
  }

  public void addPlace(String place) {
    places.add(place);
  }

  public void addTopic(String topic) {
    topics.add(topic);
  }

  /**
   * Adds a word from the article body. If getSortedWordFrequencies has already been called on this object,
   * this call will silently fail.
   *
   * @param word The word to add.
   */
  public void addWord(String word) {
    if (!isSorted) {
      if (wordFrequencies.containsKey(word)) {
        wordFrequencies.put(word, wordFrequencies.get(word) + 1);
      } else {
        wordFrequencies.put(word, 1);
      }
    }
  }

  public Set<String> getPlaces() {
    return places;
  }

  public Set<String> getTopics() {
    return topics;
  }

  public Map<String, Integer> getWordFrequencies() {
    return wordFrequencies;
  }

  public Queue<Map.Entry<String, Integer>> getSortedWordFrequencies() {
    sort();
    return sortedWordFrequencies;
  }

  private void sort() {
    if (!isSorted) {
      sortedWordFrequencies = new PriorityQueue<Map.Entry<String, Integer>>(wordFrequencies.size(), new EntryComparator());
      sortedWordFrequencies.addAll(wordFrequencies.entrySet());
      isSorted = true;
    }
  }
}
