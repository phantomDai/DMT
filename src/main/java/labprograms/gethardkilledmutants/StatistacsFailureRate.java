package labprograms.gethardkilledmutants;

import labprograms.constant.Constant;

import java.io.*;
import java.util.*;

import static java.io.File.separator;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/05/05
 */
public class StatistacsFailureRate {

    public void getFailureRate(String objectName, double failurRate){
        String path = Constant.gethardkilledmutantsPath + separator + objectName;
        File file = new File(path);
        List<String> names = new ArrayList<>();
        BufferedReader bufferedReader = null;
        int min = 10000;
        String minName = "";
        Set<String> namesSet = new HashSet<>();
        Map<String, List<String>> namesMap = new HashMap<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String temp = "";
            double threshod = 0;

            if (objectName.equals("ACMS"))
                threshod = failurRate * Constant.numberofMr4ACMS;
            else if (objectName.equals("CUBS"))
                threshod = failurRate * Constant.numberofMr4CUBS;
            else if (objectName.equals("ERS"))
                threshod = failurRate * Constant.numberofMr4ERS;
            else
                threshod = failurRate * Constant.numberofMr4MOS;

            while((temp = bufferedReader.readLine()) != null){
                String[] content = temp.split(":");
                if (Integer.parseInt(content[1]) <= threshod && Integer.parseInt(content[1]) != 0){
                    names.add(content[0]);
                    namesSet.add(content[0]);
                    String indexs = content[2].replace("[", "").
                            replace("]","");
                    String[] indexsArray = indexs.split(", ");
                    List<String> tempList = Arrays.asList(indexsArray);
                    namesMap.put(content[0], tempList);
                }

                if (Integer.parseInt(content[1]) <= min && Integer.parseInt(content[1]) != 0){
                    min = Integer.parseInt(content[1]);
                    minName = content[0];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(namesSet.toString());
        System.out.println("失效率最小的是变异体：" + minName + ":" + String.valueOf(min));
    }


    public static void main(String[] args) {
        StatistacsFailureRate statistacsFailureRate = new StatistacsFailureRate();
        statistacsFailureRate.getFailureRate("CUBS", 0.05);
    }


}
