import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 * An XMLParser parses a given valid XML document and outputs corresponding feature vector based on the relevant tags
 * and the body text.
 *
 * Author: mernagh
 */
public class XMLParser extends DefaultHandler {

  /**
   * This enum lists the recognized tags that will be parsed. Any other tags will be ignored.
   */
  private enum Tag {
    UNKOWN ("UNKNOWN"), MERMAID ("MERMAID"), REUTERS ("REUTERS"), PLACES ("PLACES"), TOPICS ("TOPICS"), TEXT ("TEXT"), BODY("BODY");

    private final String value;

    Tag(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  // Sample code
  private Hashtable<String, Integer> tags;
  private Set<ArticleData> articleDataSet;
  private ArticleData articleData;
  private StringBuffer buffer;
  private boolean inBody = false;

  private CharArraySet charArraySet = StandardAnalyzer.STOP_WORDS_SET;

  // Used to create a temporary IgnoreContentHandler when needed.
  private XMLReader xmlReader;

  public XMLParser(XMLReader xmlReader, Set<ArticleData> articleDataSet) {
    this.xmlReader = xmlReader;
    this.articleDataSet = articleDataSet;
    initStopWords();
  }

  @Override
  public void startDocument() throws SAXException {
    tags = new Hashtable<String, Integer>();
  }

  @Override
  public void endDocument() throws SAXException {

    // Example code
    Enumeration<String> e = tags.keys();
    while (e.hasMoreElements()) {
      String tag = (String)e.nextElement();
      int count = ((Integer)tags.get(tag)).intValue();
      System.out.println("Local Name \"" + tag + "\" occurs "
          + count + " times");
    }
  }

  @Override
  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

    // Compare qName to recognized Tags.
    Tag tag = null;
    try {
      tag = Enum.valueOf(Tag.class, qName);
    } catch (IllegalArgumentException e) {
      // If qName is not a recognized tag.
      xmlReader.setContentHandler(new IgnoreContentHandler(xmlReader, this));
      tag = Tag.UNKOWN;
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    switch (tag) {
      case MERMAID:
      case TEXT:
        // Continue parsing within MERMAID and TEXT
        break;
      case REUTERS:
        articleData = new ArticleData();
        break;
      case PLACES:
        // TODO(kfritschie): create a new content handler and pass it articleData.getPlaces
          AddContentHandler addPlaceHandler = new AddContentHandler(xmlReader, xmlReader.getContentHandler() , articleData.getPlaces());
            xmlReader.setContentHandler(addPlaceHandler);
          break;
      case TOPICS:
        // TODO(kfritschie): create a new content handler and pass it articleData.getTopics
          AddContentHandler addTopicHandler = new AddContentHandler(xmlReader, xmlReader.getContentHandler(), articleData.getTopics());
          xmlReader.setContentHandler(addTopicHandler);
        break;
      case BODY:
        // TODO: add lucene parsing to body.
        inBody = true;
        buffer = new StringBuffer();
        break;
      default:
        xmlReader.setContentHandler(new IgnoreContentHandler(xmlReader, this));
    }
  }

  @Override
  public void endElement(String namespaceURI, String localName, String qName) {

    // Compare qName to recognized Tags.
    Tag tag = null;
    try {
      tag = Enum.valueOf(Tag.class, qName);
    } catch (IllegalArgumentException e) {
      // If qName is not a recognized tag.
      tag = Tag.UNKOWN;
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    switch (tag) {
      case REUTERS:
        // Add articleData if it contains any body terms.
        if (articleData != null && articleData.getWordFrequencies().size() > 0) {
          articleDataSet.add(articleData);
        }
        break;
      case BODY:
        parseBodyWords();
        inBody = false;
        break;
      default:
    }
  }

  @Override
  public void characters(char ch[], int start, int length){
    if (inBody) {
      buffer.append(ch, start, length);
    }
  }

  private void initStopWords() {
    charArraySet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
    charArraySet.add("blah");
  }
  /**
   * Tokenizes the buffer, ignoring stop words, and adds the words to articleData.
   */
  private void parseBodyWords() {
    TokenStream stream = new StopFilter(new LowerCaseTokenizer(new StringReader(buffer.toString())), charArraySet);
    CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);

    try {
      stream.reset();
      while (stream.incrementToken()) {
        articleData.addWord(charTermAttribute.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
