package xmltosarif;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class CheckmarxHandler
{
    private static final String SAMPLE_CSV_FILE_PATH = "checkmarx_report_webgoat.csv";

    public ArrayList<CheckmarxModel> parseCsv()
    {

        ArrayList<CheckmarxModel> checkmarxModel = new ArrayList<CheckmarxModel>();
        try

        {


            CheckmarxModel tempModel = new CheckmarxModel();
            Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            for (CSVRecord csvRecord : csvParser)
            {

                String srcFileName = csvRecord.get("SrcFileName");
                String lineNumber = csvRecord.get("DestLine");
                String methodName = csvRecord.get("DestName");
                String message = csvRecord.get("Query");

                String pci = csvRecord.get("PCI DSS v3.2");
                String owasp2013 = csvRecord.get("OWASP Top 10 2013");
                String fisma2014 = csvRecord.get("FISMA 2014");
                String nist = csvRecord.get("NIST SP 800-53");
                String owasp2017 = csvRecord.get("OWASP Top 10 2017");
                String owasp2016 = csvRecord.get("OWASP Mobile Top 10 2016");
                String fullDescription = pci+" "+owasp2013+" "+fisma2014+" "+nist+" "+owasp2017+" "+owasp2016;


                tempModel.setSourceFile(srcFileName);
                tempModel.setLineNumber(lineNumber);
                tempModel.setMethodName(methodName);
                tempModel.setMessage(message);
                tempModel.setFullDescription(fullDescription);
                checkmarxModel.add(tempModel);
                tempModel = new CheckmarxModel();

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return checkmarxModel;
    }

}