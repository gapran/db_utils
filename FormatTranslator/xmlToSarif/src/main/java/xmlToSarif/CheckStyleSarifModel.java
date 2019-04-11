package xmltosarif;

import java.util.ArrayList;

public class CheckStyleSarifModel
{
    private String sourceFile;
    private ArrayList<String> message ;
    private ArrayList<String> startLine ;

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

    public ArrayList<String> getStartLine() {
        return startLine;
    }

    public void setStartLine(ArrayList<String> startLine) {
        this.startLine = startLine;
    }


}
