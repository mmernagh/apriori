import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Preprocesses reuters articles in .sgm files in the local directory, creating feature vectors for each.
 * The feature vectors are composed of the words in the xml tags and a frequency list of the top XX rare words
 * in the document body.
 * // TODO(mernagh) consider adding td-idf.
 */
public class Preprocess {

  /**
   * Parses all of the reuters articles in a given file and outputs the feature vector representation to stdout.
   *
   * @param file The given file.
   */
  private static void parseFeatureVectors(File file) {
    // TODO
  }

  /**
   * Reads and parses the reuters articles, outputting feature vector representations of each.
   *
   * @param args
   */
  public static void main(String[] args) {

    String pathToArticles = "./";
    String fileExtension = ".sgm";

    // Read each .sgm file from local directory
    try {
      Files.walk(Paths.get(pathToArticles)).forEach(filePath -> {
        if (Files.isRegularFile(filePath) && filePath.endsWith(fileExtension)) {
          parseFeatureVectors(filePath.toFile());
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}