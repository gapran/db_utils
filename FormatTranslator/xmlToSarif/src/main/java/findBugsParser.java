import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class findBugsParser {
        public static void main(String[] args)
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            try {
                InputStream xmlInput  =
                        new FileInputStream("C:\\findbugs_report_webgoat.xml");

                SAXParser saxParser = factory.newSAXParser();
                SaxHandler handler   = new SaxHandler();
                saxParser.parse(xmlInput,handler);

                JSONObject root = new JSONObject();
                JSONArray runs = new JSONArray();
                JSONObject runsObj = new JSONObject();

                JSONObject tools = new JSONObject();
                tools.put("name","FindBugs");
                tools.put("semanticVersion","3.0.1");

                JSONObject files = new JSONObject();
                JSONObject logicalLocations = new JSONObject();
                JSONObject rules = new JSONObject();
                JSONObject ruleObj = new JSONObject();
                JSONArray results = new JSONArray();

                ArrayList<logicalLocations> logicalLocationsArray = handler.logicalLocations;


                for(int i=0;i<handler.logicalLocations.size();i++)
                {
                    JSONObject mimeType = new JSONObject();
                    mimeType.put("mimeType","text/java");
                    files.put(logicalLocationsArray.get(i).sourcePath,mimeType);
                    String[] nameSpaces = logicalLocationsArray.get(i).getClassName().split("[.]");
                    String newNameSpace ="" ;


                    for(int j=0;j<nameSpaces.length;j++)
                    {

                        JSONObject nameSpaceObject = new JSONObject();

                        nameSpaceObject.put("name",nameSpaces[j]);
                        nameSpaceObject.put("kind", (j == nameSpaces.length - 1 ? "type" : "namespace"));

                        if(j==0)
                        {
                            newNameSpace = nameSpaces[j];
                        }
                        else
                        {
                            nameSpaceObject.put("parentKey", newNameSpace);
                            newNameSpace+= "::"+nameSpaces[j];
                        }

                        logicalLocations.put(newNameSpace,nameSpaceObject);

                    }

                    if(logicalLocationsArray.get(i).getMethodName()!=null)
                    {
                        JSONObject nameSpaceObject = new JSONObject();
                        nameSpaceObject.put("name", logicalLocationsArray.get(i).getMethodName());
                        nameSpaceObject.put("kind", "function");
                        nameSpaceObject.put("parentKey", newNameSpace);
                        newNameSpace += "::" + logicalLocationsArray.get(i).getMethodName();
                        logicalLocations.put(newNameSpace, nameSpaceObject);

                    }
                    String rule = "FB";
                    int number = 1;
                    String ruleId = rule + String.valueOf(number);

                    JSONObject ruleIdObj = new JSONObject();
                    ruleIdObj.put("id",ruleId);
                    ruleIdObj.put("description",logicalLocationsArray.get(i).getMessage());
                    ruleObj.put(ruleId,ruleIdObj);
                    System.out.println(logicalLocations.toString());

                    JSONObject resultObj = new JSONObject();
                    resultObj.put("ruleId",ruleId);
                    resultObj.put("message",logicalLocationsArray.get(i).getMessage());
                    JSONArray locArray = new JSONArray();
                    JSONObject locObj = new JSONObject();
                    JSONObject analysisTarget = new JSONObject();
                    JSONObject region = new JSONObject();
                    region.put("startLine",logicalLocationsArray.get(i).getStartLine());
                    analysisTarget.put("uri",logicalLocationsArray.get(i).getSourcePath());
                    analysisTarget.put("region",region);

                    locObj.put("analysisTarget",analysisTarget);
                    locObj.put("fullyQualifiedLogicalName",newNameSpace);
                    locArray.add(locObj);

                    resultObj.put("locations",locArray);
                    results.add(resultObj);

                    number++;

                }
                rules.put("rules",ruleObj);
                System.out.println("test");

                root.put("version","1.0.0");
                runsObj.put("tool",tools);
                runsObj.put("files",files);
                runsObj.put("logicalLocations",logicalLocations);
                runsObj.put("results",results);
                runsObj.put("rules",rules);
                runs.add(runsObj);
                root.put("runs",runs);


                FileWriter file = new FileWriter("file1.json");
                file.write(root.toJSONString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + root);
                file.close();



            }
            catch (Throwable err) {
                err.printStackTrace ();
            }

        }

}
