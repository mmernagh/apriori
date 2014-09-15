import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * An XMLParser parses a given valid XML document and outputs corresponding feature vector based on the relevant tags
 * and the body text.
 *
 * Author: mernagh
 */
public class XMLParser extends DefaultHandler {

  // Sample code
  private Hashtable<String, Integer> tags;

  // Used to create a temporary IgnoreContentHandler when needed.
  private XMLReader xmlReader;

  public XMLParser(XMLReader xmlReader) {
    this.xmlReader = xmlReader;
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

    // Example handler for tags to ignore.
    if (qName.equals("UNKNOWN")) {
      xmlReader.setContentHandler(new IgnoreContentHandler(xmlReader, this));
    }

    // Sample code that keeps track of each tag
    String key = qName;
    Object value = tags.get(key);

    if (value == null) {
      tags.put(key, new Integer(1));
    }
    else {
      int count = ((Integer)value).intValue();
      count++;
      tags.put(key, new Integer(count));
    }
  }
}
