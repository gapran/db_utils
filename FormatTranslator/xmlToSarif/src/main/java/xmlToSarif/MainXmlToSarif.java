package xmltosarif;

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

    }
}
