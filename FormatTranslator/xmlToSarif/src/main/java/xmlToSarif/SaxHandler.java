package xmlToSarif;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SaxHandler extends DefaultHandler
{
    public ArrayList<SarifModel> SarifModel = new ArrayList<SarifModel>();
    public SarifModel tempLogical = new SarifModel();
    private boolean readFirstLine = false;


    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException
    {
        if("BugInstance".equals(qName))
        {
            String type = attributes.getValue("type");
            tempLogical.setMessage(type);

        }
        else if("Class".equals(qName))
        {
            String className = attributes.getValue("classname");
            tempLogical.setClassName(className);
        }
        else if("SourceLine".equals(qName))
        {
            String sourceFile = attributes.getValue("sourcefile");
            String sourcePath = attributes.getValue("sourcepath");
            String startLine = attributes.getValue("start");
            tempLogical.setSourceFile(sourceFile);
            tempLogical.setSourcePath(sourcePath);
            if(!readFirstLine)
            {
                tempLogical.setStartLine(startLine);
                readFirstLine = true;
            }

        }
        else if("Method".equals(qName))
        {
            String methodName = attributes.getValue("name");
            tempLogical.setMethodName(methodName);
        }
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName.equalsIgnoreCase("BugInstance")) {
            SarifModel.add(tempLogical);
            tempLogical = new SarifModel();
            readFirstLine = false;
            System.out.println(tempLogical.methodName);
        }



    }

}
