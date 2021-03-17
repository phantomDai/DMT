package labprograms.mr;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static java.io.File.separator;


/**
 * this class is responsible to obtain test frames of ACMS, CUBS, ERS, and MOS
 * from the ACMS, CUBS, ERS, and MOS file, respectively.
 */
public class ObtainTestFrames {
    private String path = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr";

    private String testFrameFilePath = "";


    public ObtainTestFrames(String objectName){
        this.path = this.path + separator + objectName;
        this.testFrameFilePath = this.path + "_" + "testFrames";
    }

    /**
     * get test frames
     */
    public void obtainTestFrames(){
        //记录测试祯
        Set<String> testFrames = new HashSet<>();

        File file = new File(this.path);
        File newFile = new File(this.testFrameFilePath);
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        StringBuffer stringBuffer = new StringBuffer();

        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempStr = "";
            while ((tempStr = bufferedReader.readLine()) != null){
                String[] tempArray = tempStr.split(";");
                testFrames.add(tempArray[0]);
                testFrames.add(tempArray[1]);
            }
            bufferedReader.close();
            System.out.println(testFrames.size());

            //打印测试祯
            for (String str : testFrames){
                stringBuffer.append(str + "\n");
            }
            printWriter = new PrintWriter(new FileWriter(newFile));
            printWriter.write(stringBuffer.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ObtainTestFrames obtainTestFrames = new ObtainTestFrames("MOS");
        obtainTestFrames.obtainTestFrames();
    }



}
