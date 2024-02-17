import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileIO {

    private FileWriter file ;

    // constructor
    public FileIO() {
        try {
            // opens output file and delete
            file = new FileWriter("output.txt");
            file.close();
            //opens output file in append mode
            file = new FileWriter("output.txt", true);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    //reads input files and returns the contents as an arraylist
    public ArrayList<String[]> read(String input) {
        ArrayList<String[]> lines = new ArrayList<String[]>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(input));
            String line = reader.readLine();
            while (line != null) {
                if(line.endsWith("\n")) {
                    line = line.substring(0, line.indexOf("\n"));
                }

                lines.add(line.split("\t"));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    // writes to output file given commands and the contents of the given arraylist
    public void output(String command,ArrayList<FlightPlan> plan){
        try{
            if(!command.endsWith("\n")){
                command = command + "\n";
            }
            file.write("command : " + command);
            if(plan.isEmpty()){
                if(command.startsWith("diameterOfGraph") || command.startsWith("pageRankOfNode")){
                    file.write("Not implemented\n");
                }
                else {
                    file.write("No suitable flight plan is found\n");
                }
            }
            else {
                for (FlightPlan p : plan) {
                    file.write(p.toString());
                }
            }
            file.write("\n\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public FileWriter getFile() {
        return file;
    }
}

