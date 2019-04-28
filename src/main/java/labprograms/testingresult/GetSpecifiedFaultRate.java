package labprograms.testingresult;

import labprograms.constant.Constant;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;

/**
 * describe:
 * get the mutants which satisfies the specified failure rate
 * @author phantom
 * @date 2019/04/21
 */
public class GetSpecifiedFaultRate {

    public void getMutants(double failureRate, String objectName){
        Constant constant = new Constant();
        List<String> mutants = new ArrayList<>();
        //向下去整
        int threshhold = (int) (constant.number * failureRate);
        String path = System.getProperty("user.dir") + separator + "src" + separator + "main" +
                separator + "java" + separator + "labprograms" + separator + "testingresult" +
                separator + objectName;

        File file = new File(path);
        if (!file.exists()){
            System.out.println("文件不存在！");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tempStr = "";
            while((tempStr = br.readLine()) != null){
                String[] tempArray = tempStr.split(";");
                if (Integer.parseInt(tempArray[1]) != 0 && Integer.parseInt(tempArray[1]) < threshhold){
                    mutants.add(tempArray[0]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(mutants.toString());
    }

    public static void main(String[] args) {
        GetSpecifiedFaultRate getSpecifiedFaultRate = new GetSpecifiedFaultRate();
        getSpecifiedFaultRate.getMutants(0.15, "MOS");
    }
}
