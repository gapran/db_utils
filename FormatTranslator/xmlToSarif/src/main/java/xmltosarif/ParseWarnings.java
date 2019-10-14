package xmltosarif;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ParseWarnings {


    public void parseWarnings() {

        String sarifFiles[] = new String[1];
        //sarifFiles[0] = "findbugsXmltosarif.sarif";
        //sarifFiles[1] = "checkStyleXmltosarif.sarif";
        sarifFiles[0] = "CheckmarxCsvToSarif.sarif";

        List<String> fileNames = new ArrayList<>();



        for( int sarifCount =0; sarifCount<sarifFiles.length;sarifCount++)
        {
            try {
                Object obj = new JSONParser().parse(new FileReader(sarifFiles[sarifCount]));
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray runs = (JSONArray) jsonObject.get("runs");

                for(int i=0; i<runs.size();i++) {

                    JSONObject tempRun = (JSONObject) runs.get(i);
                    JSONObject files = (JSONObject) tempRun.get("files");
                    //JSONObject fileName = (JSONObject) files.values();
                    for (Object key: files.keySet()){
                        String fNames[] = key.toString().split("/");
                        String fName = fNames[fNames.length-1].replace(".","_");

                        if(fileNames.size()== 0)
                        {
                            fileNames.add(fName);
                        }
                        else
                        {
                            if(!fileNames.contains(fName))
                            {
                                fileNames.add(fName);
                            }
                        }
                    }



                }



            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        System.out.println("File names"+ fileNames);

        for(int i =0;i<fileNames.size();i++)
        {

            JSONObject error = new JSONObject();


            for( int j =0; j<sarifFiles.length;j++)
            {
                try
                {
                    Object obj = new JSONParser().parse(new FileReader(sarifFiles[j]));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray runs = (JSONArray) jsonObject.get("runs");
                    JSONObject tempRun = (JSONObject) runs.get(0);
                    JSONObject rules = (JSONObject) tempRun.get("rules");
                    JSONArray results = (JSONArray) tempRun.get("results");

                    for(int l=0;l<results.size();l++)
                    {
                        JSONObject fileContent = new JSONObject();

                        JSONObject tempResult = (JSONObject) results.get(l);

                        String ruleId = (String) tempResult.get("ruleId");
                        String message = (String) tempResult.get("message");

                        JSONArray locations = (JSONArray) tempResult.get("locations");

                        JSONObject tempLocation = (JSONObject) locations.get(0);
                        JSONObject analysisTarget = (JSONObject) tempLocation.get("analysisTarget");
                        JSONObject region = (JSONObject) analysisTarget.get("region");
                        String startLine = (String) region.get("startLine");
                        String uri = (String) analysisTarget.get("uri");
                        String fileArray[] = uri.split("/");
                        String fileName = fileArray[fileArray.length-1];
                        String fName = fileName.replace(".","_");

                        JSONObject getRuleHandler = (JSONObject) rules.get(ruleId);
                        String longMessage = (String) getRuleHandler.get("description");

                        if(fileNames.get(i).equals(fName))
                        {

                            fileContent.put("startLine",startLine);
                            fileContent.put("shortMessage",message);
                            fileContent.put("longMessage", longMessage);

                            error.put(ruleId,fileContent);
                        }



                    }




                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }


            }

            try
            {
                FileWriter file = new FileWriter(fileNames.get(i)+".json");
                file.write(error.toString());
                file.close();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }


}


