package xmltosarif;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CheckStyleParser
{
    private CheckStyleHandler checkStyleHandler;
    private String currentNameSpace;
    private final String ruleId = "de.upb.gpa.Checkstyle";
    private int ruleIdExtend = 1;
    private ArrayList<String> tempRules = new ArrayList<String>();

    public void parseXmlFile()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            InputStream xmlCheckStyleInput = new FileInputStream("checkstyle_report.xml");
            SAXParser saxParser3 = factory.newSAXParser();
            checkStyleHandler = new CheckStyleHandler();
            saxParser3.parse(xmlCheckStyleInput, checkStyleHandler);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addTools(JSONObject tools) {
        tools.put("name", "CheckStyle");
        tools.put("semanticVersion", "8.8.0");
    }

    private void addFiles(JSONObject files, String sourcePath)
    {
        JSONObject mimeType = new JSONObject();
        mimeType.put("mimeType", "text/java");
        files.put(sourcePath, mimeType);
    }

    private void addLogicalLocations(JSONObject logicalLocations, String sourcePath) {
        String[] nameSpaces = sourcePath.split("[/]");
        currentNameSpace = "";

        for (int j = 0; j < nameSpaces.length; j++) {
            JSONObject nameSpaceObject = new JSONObject();

            nameSpaceObject.put("name", nameSpaces[j]);
            nameSpaceObject.put("kind", (j == nameSpaces.length - 1 ? "type" : "namespace"));

            if (j == 0) {
                currentNameSpace = nameSpaces[j];
            } else {
                nameSpaceObject.put("parentKey", currentNameSpace);
                currentNameSpace += "::" + nameSpaces[j];
            }

            logicalLocations.put(currentNameSpace, nameSpaceObject);

        }

    }

    private void addRulesAndResults( JSONObject ruleObj, JSONArray results, String message, String startLine, String uri)
    {
        boolean isRule = false;

        JSONObject resultObj = new JSONObject();
        resultObj.put("message", message);

        JSONArray locArray = new JSONArray();
        JSONObject locObj = new JSONObject();
        JSONObject analysisTarget = new JSONObject();
        JSONObject region = new JSONObject();
        region.put("startLine", startLine);
        analysisTarget.put("uri", uri);
        analysisTarget.put("region", region);

        locObj.put("analysisTarget", analysisTarget);
        locObj.put("fullyQualifiedLogicalName", currentNameSpace);
        locArray.add(locObj);

        resultObj.put("locations", locArray);
        results.add(resultObj);

        JSONObject ruleIdObj = new JSONObject();

        String tempruleID = "";
        String tempMessage = "";
        tempMessage = message.replaceAll("\\(.*?\\)","");

        resultObj.put("ruleId", ruleId+"."+ruleIdExtend);
        ruleIdObj.put("id", ruleId+"."+ruleIdExtend);
        ruleIdObj.put("description", tempMessage);
        ruleObj.put(ruleId+"."+ruleIdExtend, ruleIdObj);
        ruleIdExtend++;


        /*
        if(tempRules.size()==0)
        {

            resultObj.put("ruleId", ruleId+"."+ruleIdExtend);
            ruleIdObj.put("id", ruleId+"."+ruleIdExtend);
            ruleIdObj.put("description", tempMessage);
            ruleObj.put(ruleId+"."+ruleIdExtend, ruleIdObj);
            tempRules.add(ruleIdExtend-1,tempMessage);
            ruleIdExtend++ ;
        }

        else
        {
            for(int i=0;i<tempRules.size();i++)
            {
                if(tempMessage.equals(tempRules.get(i)))
                {
                    isRule = true;
                    int j = i+1;
                    tempruleID = ruleId+"."+j;
                    break;
                }
            }

            if(isRule)
            {
                resultObj.put("ruleId", tempruleID);
                ruleIdObj.put("id", tempruleID);
                ruleIdObj.put("description", tempMessage);
                ruleObj.put(tempruleID, ruleIdObj);

            }
            else
            {
                resultObj.put("ruleId", ruleId+"."+ruleIdExtend);
                ruleIdObj.put("id", ruleId+"."+ruleIdExtend);
                ruleIdObj.put("description", tempMessage);
                ruleObj.put(ruleId+"."+ruleIdExtend, ruleIdObj);
                tempRules.add(ruleIdExtend-1,tempMessage);
                ruleIdExtend++ ;

            }
        }*/



    }




    public String generateCheckStyleSarif(CheckStyleParser checkStyleParser)
    {
        JSONObject tools = new JSONObject();
        checkStyleParser.addTools(tools);

        JSONObject files = new JSONObject();
        JSONObject logicalLocations = new JSONObject();
        JSONObject ruleObj = new JSONObject();
        JSONArray results = new JSONArray();


        JSONObject root = new JSONObject();
        JSONArray runs = new JSONArray();
        JSONObject runsObj = new JSONObject();


        ArrayList<CheckStyleSarifModel> checkStyleSarifModels = checkStyleParser.checkStyleHandler.CheckStyleSarifModel;

        for(int i=0;i<checkStyleSarifModels.size();i++)
        {
            checkStyleParser.addFiles(files, checkStyleSarifModels.get(i).getSourceFile());

            checkStyleParser.addLogicalLocations(logicalLocations, checkStyleSarifModels.get(i).getSourceFile() );

            for(int j =0;j<checkStyleSarifModels.get(i).getMessage().size();j++)
            {

                checkStyleParser.addRulesAndResults(ruleObj, results, checkStyleSarifModels.get(i).getMessage().get(j), checkStyleSarifModels.get(i).getStartLine().get(j), checkStyleSarifModels.get(i).getSourceFile() );
            }

        }

        root.put("version", "1.0.0");
        runsObj.put("tool", tools);
        runsObj.put("files", files);
        runsObj.put("logicalLocations", logicalLocations);
        runsObj.put("results", results);
        runsObj.put("rules", ruleObj);
        runs.add(runsObj);
        root.put("runs", runs);


        String sarifCheckStyleString = root.toString();

        return sarifCheckStyleString;

    }

    public void writeDataToFileCheckStyle(String data)
    {
        try {
            FileWriter file = new FileWriter("checkStyleXmltosarif.sarif");
            file.write(data);
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + data);
            file.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
