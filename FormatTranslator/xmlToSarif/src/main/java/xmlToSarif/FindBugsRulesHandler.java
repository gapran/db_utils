package xmltosarif;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class FindBugsRulesHandler extends DefaultHandler
{
    public HashMap<String, FindBugsRule> rules = new HashMap<String, FindBugsRule>();
    public FindBugsRule tempRule = new FindBugsRule();

    private boolean bMessage = false;
    private boolean bDescription = false;
    private StringBuilder data = null;


    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException
    {
        if("rule".equals(qName))
        {
            String id = attributes.getValue("key");
            tempRule.setId(id);
        }
        else if("name".equals(qName))
        {
            bMessage = true;
        }
        else if("description".equals(qName))
        {
            bDescription = true;
        }

        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (bMessage) {
            tempRule.setMessage(data.toString());
            bMessage = false;
        }
        else if (bDescription) {
            String description = data.toString();
            String strRegEx = "<[^>]*>";
            tempRule.setDescription(description.replaceAll(strRegEx, "").replaceAll("\n", "").trim().replaceAll("( )+"," "));
            bDescription = false;
        }

        if (qName.equalsIgnoreCase("rule")) {
            rules.put(tempRule.getId(), tempRule);
            tempRule = new FindBugsRule();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}
