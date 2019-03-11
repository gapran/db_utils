package xmltosarif;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class FindBugsParser {
    private SaxHandler handler;
    private String currentNameSpace;

    private void parseXmlFile() {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            InputStream xmlInput = new FileInputStream("findbugs_report_webgoat.xml");

            SAXParser saxParser = factory.newSAXParser();
            handler = new SaxHandler();
            saxParser.parse(xmlInput, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getRuleId(int id) {
        return "FB" + id;
    }


    private void addTools(JSONObject tools) {
        tools.put("name", "FindBugs");
        tools.put("semanticVersion", "3.0.1");
    }


    private void addFiles(JSONObject files, String sourcePath) {
        JSONObject mimeType = new JSONObject();
        mimeType.put("mimeType", "text/java");
        files.put(sourcePath, mimeType);
    }


    private void addRules(JSONObject ruleObj, String ruleId, String description) {
        JSONObject ruleIdObj = new JSONObject();
        ruleIdObj.put("id", ruleId);
        ruleIdObj.put("description", description);

        ruleObj.put(ruleId, ruleIdObj);
    }


    private void addLogicalLocations(JSONObject logicalLocations, String className, String methodName) {
        String[] nameSpaces = className.split("[.]");
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

        if (methodName != null) {
            JSONObject nameSpaceObject = new JSONObject();
            nameSpaceObject.put("name", methodName);
            nameSpaceObject.put("kind", "function");
            nameSpaceObject.put("parentKey", currentNameSpace);
            currentNameSpace += "::" + methodName;
            logicalLocations.put(currentNameSpace, nameSpaceObject);
        }
    }


    private void addResults(JSONArray results, String ruleId, String message, String startLine, String uri) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("ruleId", ruleId);
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
    }




    public static void main(String[] args) {
        FindBugsParser findBugsParser = new FindBugsParser();
        findBugsParser.parseXmlFile();

        JSONObject tools = new JSONObject();
        findBugsParser.addTools(tools);

        JSONObject files = new JSONObject();
        JSONObject logicalLocations = new JSONObject();
        JSONObject rules = new JSONObject();
        JSONObject ruleObj = new JSONObject();
        JSONArray results = new JSONArray();

        JSONObject root = new JSONObject();
        JSONArray runs = new JSONArray();
        JSONObject runsObj = new JSONObject();

        ArrayList<SarifModel> sarifModelArray = findBugsParser.handler.SarifModel;

        for (int i = 0; i < sarifModelArray.size(); i++) {
            findBugsParser.addFiles(files, sarifModelArray.get(i).sourcePath);

            findBugsParser.addLogicalLocations(logicalLocations, sarifModelArray.get(i).getClassName(), sarifModelArray.get(i).getMethodName());

            String ruleId = findBugsParser.getRuleId(i + 1);
            findBugsParser.addRules(ruleObj, ruleId, sarifModelArray.get(i).getMessage());

            findBugsParser.addResults(results, ruleId, sarifModelArray.get(i).getMessage(), sarifModelArray.get(i).getStartLine(), sarifModelArray.get(i).getSourcePath());
        }

        rules.put("rules",ruleObj);
        root.put("version", "1.0.0");
        runsObj.put("tool", tools);
        runsObj.put("files", files);
        runsObj.put("logicalLocations", logicalLocations);
        runsObj.put("results", results);
        runsObj.put("rules", rules);
        runs.add(runsObj);
        root.put("runs", runs);


        try {
            FileWriter file = new FileWriter("xmltosarif.json");
            file.write(root.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + root);
            file.close();
        }
        catch(java.io.IOException e)
        {
            e.printStackTrace();
        }
    }
}