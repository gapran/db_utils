package xmltosarif;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CheckmarxParser
{
    private String currentNameSpace = "";
    private final String ruleId = "de.upb.gpa.Checkmarx";
    private int ruleIdExtend = 1;

    private void addTools(JSONObject tools) {
        tools.put("name", "Checkmarx");
        tools.put("semanticVersion", "8.8.0");
    }

    private void addFiles(JSONObject files, String sourcePath) {
        JSONObject mimeType = new JSONObject();
        mimeType.put("mimeType", "text/java");
        files.put(sourcePath, mimeType);
    }

    private void addLogicalLocations(JSONObject logicalLocations, String sourceFile, String methodName) {
        String[] nameSpaces = sourceFile.split("[/]");
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

    private void addRulesAndResults(JSONObject ruleObj, JSONArray results, String sourceFile, String lineNumber, String message, String fullDescription)
    {
        JSONObject ruleIdObj = new JSONObject();
        ruleIdObj.put("id", ruleId + "." + ruleIdExtend);
        ruleIdObj.put("description", fullDescription);
        ruleObj.put(ruleId + "." + ruleIdExtend, ruleIdObj);


        JSONObject resultObj = new JSONObject();
        resultObj.put("ruleId", ruleId + "." + ruleIdExtend);
        resultObj.put("message", message);

        JSONArray locArray = new JSONArray();
        JSONObject locObj = new JSONObject();
        JSONObject analysisTarget = new JSONObject();
        JSONObject region = new JSONObject();

        region.put("startLine", lineNumber);
        analysisTarget.put("uri", sourceFile);
        analysisTarget.put("region", region);

        locObj.put("analysisTarget", analysisTarget);
        locObj.put("fullyQualifiedLogicalName", currentNameSpace);
        locArray.add(locObj);

        resultObj.put("locations", locArray);

        results.add(resultObj);


        ruleIdExtend++;

    }

    public String generateCheckmarxSarif(ArrayList<CheckmarxModel> checkmarxModel, CheckmarxParser checkmarxParser)
    {
        JSONObject tools = new JSONObject();
        checkmarxParser.addTools(tools);

        JSONObject files = new JSONObject();
        JSONObject logicalLocations = new JSONObject();

        JSONObject ruleObj = new JSONObject();
        JSONArray results = new JSONArray();

        JSONObject root = new JSONObject();
        JSONArray runs = new JSONArray();
        JSONObject runsObj = new JSONObject();



        for (int i = 0; i < checkmarxModel.size(); i++) {
            checkmarxParser.addFiles(files, checkmarxModel.get(i).getSourceFile());

            checkmarxParser.addLogicalLocations(logicalLocations, checkmarxModel.get(i).getSourceFile() , checkmarxModel.get(i).getMethodName());

            checkmarxParser.addRulesAndResults(ruleObj, results, checkmarxModel.get(i).getSourceFile(), checkmarxModel.get(i).getLineNumber(), checkmarxModel.get(i).getMessage(), checkmarxModel.get(i).getFullDescription());
        }

        root.put("version", "1.0.0");
        runsObj.put("tool", tools);
        runsObj.put("files", files);
        runsObj.put("logicalLocations", logicalLocations);
        runsObj.put("results", results);
        runsObj.put("rules", ruleObj);
        runs.add(runsObj);
        root.put("runs", runs);

        String sarifFindBugsString = root.toString();

        return sarifFindBugsString;

    }

    public void writeDataToFileCheckMarx(String data)
    {
        try {
            FileWriter file = new FileWriter("CheckmarxCsvToSarif.sarif");
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
