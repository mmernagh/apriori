import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Vector;

/**
 * File visitor that parses any .sgm files using an {@link XMLParser}.
 */
public class ParsingFileVisitor extends SimpleFileVisitor<Path> {

  private static final String fileExtension = ".sgm";

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    if (attrs.isRegularFile() && file.getFileName().toString().endsWith(fileExtension)) {
      try {
        parseFeatureVectors(file.toFile());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    System.err.println(exc);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  /**
   * Parses all of the reuters articles in a given file and outputs the feature vector representation to stdout.
   *
   * @param file The given file.
   */
  private static void parseFeatureVectors(File file) throws FileNotFoundException {

    InputSource inputSource = wrapFile(file);

    // Get an XMLReader.
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(false);

    try {
      SAXParser saxParser = spf.newSAXParser();
      XMLReader xmlReader = saxParser.getXMLReader();
      xmlReader.setContentHandler(new XMLParser(xmlReader));
      xmlReader.parse(inputSource);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        inputSource.getByteStream().close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Converts a given file to an {@link org.xml.sax.InputSource} prepending required headers and appending required
   * footers.
   *
   * @param file The file.
   * @return The InputSource
   */
  private static InputSource wrapFile(File file) throws FileNotFoundException {

    // Add the xml version, and a top-level element (so the document is a well-formed XML Document)
    String appendToTop = "<?xml version=\"1.1\"?>\n<MERMAID>";
    String appendToEnd = "</MERMAID>";

    InputStream fileInputStream = new FileInputStream(file);

    // Consume the first line of the file, which is the <!DOCTYPE> line and would throw a fatal error if located
    // after the top level element "MERMAID"
    try {
      fileInputStream.skip(36);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Create streams to add before and after the document
    InputStream topStream =  new ByteArrayInputStream(appendToTop.getBytes(StandardCharsets.UTF_8));
    InputStream endStream =  new ByteArrayInputStream(appendToEnd.getBytes(StandardCharsets.UTF_8));

    // Combine streams.
    Vector<InputStream> streams = new Vector<InputStream>();
    streams.add(topStream);
    streams.add(fileInputStream);
    streams.add(endStream);

    Enumeration<InputStream> enu = streams.elements();
    SequenceInputStream sis = new SequenceInputStream(enu);

    return new InputSource(sis);
  }
}
