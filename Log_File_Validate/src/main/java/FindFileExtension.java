import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FindFileExtension {
    final String inputDir = "/Users/anishanal/IdeaProjects/Log_File_Validate/src/main/resources/";
    final String outputDir = "";
    String fileExtension1 = "";
    String fileExtension2 = "";
    Scanner Scan = new Scanner(System.in);
    static String filePath1;
    static String filePath2;

    public String findExtension(){

        System.out.println("Enter the Name of First File");
        filePath1=inputDir+Scan.nextLine();
        System.out.println("Enter the Name of Second File");
        filePath2=inputDir+Scan.nextLine();
        int i = filePath1.lastIndexOf('.');
        if(i>0){
            fileExtension1 = filePath1.substring(i+1);
            System.out.println(fileExtension1);
        }
        else{
            System.out.println("Invalid File Format");
        }

        int j = filePath2.lastIndexOf('.');
        if(j>0){
            fileExtension2 = filePath2.substring(j+1);
            System.out.println(fileExtension2);
        }
        else{
            System.out.println("Invalid File Format");
        }

        if(fileExtension1.equals(fileExtension2)){
            System.out.println("Both File Types are matching");
        }
        else{
            System.out.println("Both File Types are different");
            System.exit(-1);
        }

        return fileExtension1;

    }


    public void performComparision() throws IOException {
        switch(fileExtension1){
            case "txt":
                new FlatFileComparision().FlatFileComparision();
                break;
            case "xlsx":
                new ExcelFileComparision().CompareExcel();
                break;
            case "csv":
                new CSVFileComparision().CSVFileCompare();
                break;
            case "xml":
                new XMLFileComparision().compareXMLFiles();
                break;
            default:
                new FlatFileComparision().FlatFileComparision();
                break;
        }



    }
    public static void main(String[] args) throws IOException {

        FindFileExtension FFE = new FindFileExtension();
        FFE.findExtension();
        FFE.performComparision();
    }

}
