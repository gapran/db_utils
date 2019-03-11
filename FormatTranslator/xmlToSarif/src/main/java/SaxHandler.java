import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SaxHandler extends DefaultHandler
{

    ArrayList<logicalLocations> logicalLocations = new ArrayList<logicalLocations>();
    logicalLocations tempLogical = new logicalLocations();
    boolean readFirstLine = false;


    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException
    {
        if(qName.equals("BugInstance"))
        {
            String type = attributes.getValue("type");
            tempLogical.setMessage(type);

        }
        else if(qName.equals("Class"))
        {
            String className = attributes.getValue("classname");
            tempLogical.setClassName(className);
        }
        else if(qName.equals("SourceLine"))
        {
            String sourceFile = attributes.getValue("sourcefile");
            String sourcePath = attributes.getValue("sourcepath");
            String startLine = attributes.getValue("start");
            tempLogical.setSourceFile(sourceFile);
            tempLogical.setSourcePath(sourcePath);
            if(readFirstLine == false)
            {
                tempLogical.setStartLine(startLine);
                readFirstLine = true;
            }

        }
        else if(qName.equals("Method"))
        {
            String methodName = attributes.getValue("name");
            tempLogical.setMethodName(methodName);
        }
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName.equalsIgnoreCase("BugInstance")) {
            // add Employee object to list
            logicalLocations.add(tempLogical);
            tempLogical = new logicalLocations();
            readFirstLine = false;
            System.out.println(tempLogical.methodName);
        }



    }

}
