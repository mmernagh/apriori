import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Set;

/**
 * AddContentHandler writes each element value that it encounters to a Set<String> that is provided.
 *
 * Created by kfritschie on 9/17/2014.
 */
public class AddContentHandler extends DefaultHandler{

    private int depth = 1;
    private XMLReader xmlReader;
    private ContentHandler contentHandler;
    private Set<String> toPlStrSet;

    public AddContentHandler (XMLReader xmlReader, ContentHandler contentHandler, Set<String> topPlStrSet){
        this.contentHandler = contentHandler;
        this.xmlReader = xmlReader;
        this.toPlStrSet = topPlStrSet;
    }
    @Override public void characters(char ch[], int start, int length){
    toPlStrSet.add(new String(ch, start, length));
    }

    @Override public void startElement ( String uri, String localName, String qName, Attributes atts){
        depth++;
    }
    public void endElement (String uri, String localName, String qName)
      throws SAXException{
        depth --;
        if(0 == depth){
            xmlReader.setContentHandler(contentHandler);
        }
    }
}
