import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatFileComparision{

    public void FlatFileComparision() throws IOException {
        try {
            BufferedReader Br = new BufferedReader(new FileReader(FindFileExtension.filePath1));
            BufferedReader Brd = new BufferedReader(new FileReader(FindFileExtension.filePath2));

            String sCurrentLine = " ";
            String pCurrentLine = " ";
            List<String> L1 = new ArrayList<String>();
            List<String> L2 = new ArrayList<String>();
            while ((sCurrentLine = Br.readLine()) != null) {
                System.out.println(sCurrentLine);
                L1.add(sCurrentLine);
            }


            while ((pCurrentLine = Brd.readLine()) != null) {
                System.out.println(pCurrentLine);
                L2.add(pCurrentLine);
            }

            if(L1.equals(L2)){
                System.out.println("Files Matching");
            }
            else{
                System.out.println("Files are not matching");
                List<String> tmpList = new ArrayList<String>(L1);
                tmpList.removeAll(L2);
                System.out.println("content from First file which is not there in second File");
                for (int i = 0; i < tmpList.size(); i++) {
                    System.out.println(tmpList.get(i)); //content from First file which is not there in second File
                }
                System.out.println("content from Second file which is not there in First File");

                tmpList = L2;
                tmpList.removeAll(L1);
                for (int i = 0; i < tmpList.size(); i++) {
                    System.out.println(tmpList.get(i)); //content from Second file which is not there in First File
                }
            }

        } catch (IOException e){
            System.out.println("File not Found Exception");
            e.printStackTrace();
        }

    }


}


