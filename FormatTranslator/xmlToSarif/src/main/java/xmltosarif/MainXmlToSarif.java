package xmltosarif;

import java.util.ArrayList;
import java.util.List;

public class MainXmlToSarif {

    public static void main(String[] args)
    {
        FindBugsParser findBugsParser = new FindBugsParser();
        findBugsParser.parseXmlFile();
        String findBugsSarifString = findBugsParser.generateFindBugsSarif(findBugsParser);
        findBugsParser.writeDataToFileFindBugs(findBugsSarifString);

        CheckStyleParser checkStyleParser = new CheckStyleParser();
        checkStyleParser.parseXmlFile();
        String checkStyleString = checkStyleParser.generateCheckStyleSarif(checkStyleParser);
        checkStyleParser.writeDataToFileCheckStyle(checkStyleString);
        System.out.println("CheckStyle results "+checkStyleString);

        CheckmarxHandler checkmarxHandler = new CheckmarxHandler();
        ArrayList<CheckmarxModel> checkMarxModel = checkmarxHandler.parseCsv();
        CheckmarxParser checkmarxParser = new CheckmarxParser();
        String checkMarxString = checkmarxParser.generateCheckmarxSarif(checkMarxModel, checkmarxParser);
        checkmarxParser.writeDataToFileCheckMarx(checkMarxString);

        ParseWarnings parseWarnings = new ParseWarnings();
        List<String> fileNames = parseWarnings.getFileNames();
        parseWarnings.parseWarnings(fileNames);


    }
}
