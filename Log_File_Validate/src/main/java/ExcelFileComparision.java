import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelFileComparision {

    public void CompareExcel() throws IOException {
        FileInputStream FIS1 = new FileInputStream(new File(FindFileExtension.filePath1));
        FileInputStream FIS2 = new FileInputStream(new File(FindFileExtension.filePath2));

        XSSFWorkbook Wb1 = new XSSFWorkbook(FIS1);
        XSSFWorkbook Wb2 = new XSSFWorkbook(FIS2);

        XSSFSheet Sheet1 = Wb1.getSheetAt(0);
        XSSFSheet Sheet2 = Wb2.getSheetAt(0);

        ArrayList<String> List1 = new ArrayList<String>();
        ArrayList<String> List2 = new ArrayList<String>();

        int lastRowNum1 = Sheet1.getLastRowNum();

        for(int i=0; i<=lastRowNum1; i++) {
            int colNum1 = Sheet1.getRow(i).getLastCellNum();
            for (int j = 0; j <= colNum1; j++) {

                List1.add(Sheet1.getRow(i).getCell(j).toString());

            }
        }

        int lastRowNum2 = Sheet2.getLastRowNum();

        for(int i=0; i<=lastRowNum2; i++){
            int colNum2 = Sheet2.getRow(i).getLastCellNum();
            for(int j=0; j<= colNum2; j++){

                List2.add(Sheet2.getRow(i).getCell(j).toString());
            }
        }

        if(List1.equals(List2)){
            System.out.println("Files are Matching");
        }
        else{
            System.out.println("Files are not Matching");
            List<String> tmpList = new ArrayList<String>(List1);
            tmpList.removeAll(List2);
            System.out.println("content from First file which is not there in second File");
            for (int i = 0; i < tmpList.size(); i++) {
                System.out.println(tmpList.get(i)); //content from First file which is not there in second File
            }
            System.out.println("content from Second file which is not there in First File");
            tmpList = List2;
            tmpList.removeAll(List1);
            for (int i = 0; i < tmpList.size(); i++) {
                System.out.println(tmpList.get(i)); //content from Second file which is not there in First File
            }





        }


        }



    }









