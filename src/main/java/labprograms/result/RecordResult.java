package labprograms.result;

import labprograms.constant.Constant;
import labprograms.partition.Partition4MOS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.io.File.separator;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/04/30
 */
public class RecordResult {

    public static void recordResult(String objectName, List<Integer> fmeasure, List<Integer> tmeasure,
                                    List<Long> firstSeleteArray, List<Long> firstGenerateArray,
                                    List<Long> firstExecuteArray, List<Long> allSeleteArray,
                                    List<Long> allGenerateArray, List<Long> allExecuteArray,
                                    double averageFmeasure, double averageTmeasure,
                                    double averagefirstSelete, double averagefirstGenerate,
                                    double averagefirstExecute, double averageallSelete,
                                    double averageallGenerate, double averageallExecute){

        String path = Constant.resultPath +separator + objectName;
        File file = new File(path);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(file,true));
            printWriter.write("F:" + fmeasure.toString() + "\n");
            printWriter.write("T:" + tmeasure.toString() + "\n");
            printWriter.write("firstSeleteArray:" + firstSeleteArray.toString() + "\n");
            printWriter.write("firstGenerateArray:" + firstGenerateArray.toString() + "\n");
            printWriter.write("firstExecuteArray:" + firstExecuteArray.toString() + "\n");
            printWriter.write("allSeleteArray:" + allSeleteArray.toString() + "\n");
            printWriter.write("allGenerateArray:" + allGenerateArray.toString() + "\n");
            printWriter.write("allExecuteArray:" + allExecuteArray.toString() + "\n");

            printWriter.write("averageF:" + String.valueOf(averageFmeasure) + "\n");
            printWriter.write("averageT:" + String.valueOf(averageTmeasure) + "\n");
            printWriter.write("averagefirstSelete:" + String.valueOf(averagefirstSelete) + "\n");
            printWriter.write("averagefirstGenerate:" + String.valueOf(averagefirstGenerate) + "\n");
            printWriter.write("averagefirstExecute:" + String.valueOf(averagefirstExecute) + "\n");
            printWriter.write("averageallSelete:" + String.valueOf(averageallSelete) + "\n");
            printWriter.write("averageallGenerate:" + String.valueOf(averageallGenerate) + "\n");
            printWriter.write("averageallExecute:" + String.valueOf(averageallExecute) + "\n");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
