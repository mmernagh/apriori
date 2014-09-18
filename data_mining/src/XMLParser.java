import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
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

  private Set<ArticleData> articleDataSet;

  // The current ArticleData (may get discarded if no body text is found).
  private ArticleData articleData;

  // Stores the body text.
  private StringBuffer buffer;
  private boolean inBody = false;

  // Custom stop word set.
  private CharArraySet charArraySet = StandardAnalyzer.STOP_WORDS_SET;

  // Used to create a temporary IgnoreContentHandler when needed.
  private XMLReader xmlReader;

  public XMLParser(XMLReader xmlReader, Set<ArticleData> articleDataSet) {
    this.xmlReader = xmlReader;
    this.articleDataSet = articleDataSet;
    initStopWords();
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
        AddContentHandler addPlaceHandler = new AddContentHandler(xmlReader, xmlReader.getContentHandler() , articleData.getPlaces());
        xmlReader.setContentHandler(addPlaceHandler);
        break;
      case TOPICS:
        AddContentHandler addTopicHandler = new AddContentHandler(xmlReader, xmlReader.getContentHandler(), articleData.getTopics());
        xmlReader.setContentHandler(addTopicHandler);
        break;
      case BODY:
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

  /**
   * Set up the list of stop words.
   */
  private void initStopWords() {
    charArraySet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
    charArraySet.add("blah");
  }

  /**
   * Tokenizes the buffer, ignoring stop words, and adds the words to articleData.
   */
  private void parseBodyWords() {
    // Remove stop words
    TokenStream stream = new StopFilter(new LowerCaseTokenizer(new StringReader(buffer.toString())), charArraySet);

    // Stem tokens
    stream = new PorterStemFilter(stream);

    // Boilerplate to obtain tokens.
    CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);

    // Add filtered tokens to article data.
    try {
      stream.reset();
      while (stream.incrementToken()) {
        articleData.addWord(charTermAttribute.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Wrap things up.
    try {
      stream.end();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
