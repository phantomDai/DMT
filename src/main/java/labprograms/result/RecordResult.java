package labprograms.result;

import labprograms.constant.Constant;
import labprograms.partition.Partition4MOS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static java.io.File.separator;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/04/30
 */
public class RecordResult {

    public static void recordResult(String objectName, int idVersion,
                                    double FAverage, double F2Average){

        String path = Constant.resultPath + separator + objectName;
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
            printWriter.write("id-version: " + String.valueOf(idVersion) + "\n");
            printWriter.write("Fmeasure: " + String.valueOf(FAverage) + "\n");
            printWriter.write("F2measure: " + String.valueOf(F2Average) + "\n");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
