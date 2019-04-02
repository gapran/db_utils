package xmltosarif;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class CheckStyleHandler extends DefaultHandler
{
    public ArrayList<CheckStyleSarifModel> CheckStyleSarifModel = new ArrayList<CheckStyleSarifModel>();
    public CheckStyleSarifModel tempSarifModel = new CheckStyleSarifModel();
    public ArrayList<String> messages = new ArrayList<String>();
    public ArrayList<String> startLines = new ArrayList<String>();
    int i=0;

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException
    {
        if("file".equals(qName))
        {

            String name = attributes.getValue("name");
            String[] fileNameDelimeter = name.split("java/");
            tempSarifModel.setSourceFile(fileNameDelimeter[1]);

        }
        else if("error".equals(qName))
        {
            String message = attributes.getValue("message");
            messages.add(i,message);

            String startLine = attributes.getValue("line");
            startLines.add(i,startLine);

            i++;
        }

    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException
    {
        if (qName.equalsIgnoreCase("file"))
        {
            tempSarifModel.setMessage(messages);
            tempSarifModel.setStartLine(startLines);
            CheckStyleSarifModel.add(tempSarifModel);
            tempSarifModel = new CheckStyleSarifModel();
            messages = new ArrayList<String>();
            startLines = new ArrayList<String>();
            i=0;
        }
    }

}
