import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This content handler consumes all data that it receives without performing any processing on it.
 *
 * Author: mernagh
 */
public class IgnoreContentHandler extends DefaultHandler {

  private int depth = 1;
  private XMLReader xmlReader;
  private ContentHandler contentHandler;

  public IgnoreContentHandler(XMLReader xmlReader, ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
    this.xmlReader = xmlReader;
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    depth++;
  }

  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    depth--;
    if(0 == depth) {
      xmlReader.setContentHandler(contentHandler);
    }
  }
}
