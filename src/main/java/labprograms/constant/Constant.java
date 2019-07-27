package labprograms.constant;

import lombok.Getter;
import lombok.Setter;

import static java.io.File.separator;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/04/19
 */
public class Constant {

    /**
     * this is the number of generating test cases
     */
    public static final int number = 10000;

    public static final String acmssource = "labprograms.ACMS.sourceCode.AirlinesBaggageBillingService";

    public static final String cubssource = "labprograms.CUBS.sourceCode.BillCalculation";

    public static final String testingresultdir = System.getProperty("user.dir") +
            separator + "src" + separator + "main" + separator + "java"
            + separator + "labprograms" + separator + "testingresult";


    @Getter
    public String mrPath = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr";


    @Getter
    public static final String partitionPath = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "partition";


    /**the number of repeating times for testing*/
    public static final int repeatNumber = 30;


    /**the path of the file that includes the test frames and corresponding MRs for ACMS*/
    public static final String mrPath4ACMS = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr" + separator + "ACMS";


    /**the path of the dir that includes log of each object*/
    public static final String logPath = System.getProperty("user.dir") + separator + "log";

    public static final String resultPath = System.getProperty("user.dir") + separator + "result";

    public static final String killedmutantinfo = System.getProperty("user.dir") +
            separator + "src" + separator + "main" + separator + "java"
            + separator + "labprograms" + separator + "gethardkilledmutants";

    /**the path of the file that includes the test frames and corresponding MRs for CUBS*/
    public static final String mrPath4CUBS = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr" + separator + "CUBS";

    public static final String mrDirPath = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr";


    /**the path of the file that includes the test frames and corresponding MRs for ERS*/
    public static final String mrPath4ERS = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr" + separator + "ERS";

    /**the path of the file that includes the test frames and corresponding MRs for MT4MOS*/
    public static final String mrPath4MOS = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "mr" + separator + "MOS";

    public static final String gethardkilledmutantsPath = System.getProperty("user.dir") + separator + "src" + separator
            + "main" + separator + "java" + separator + "labprograms" + separator + "gethardkilledmutants";

    public static final int numberofMr4ACMS = 735;

    public static final int numberofMr4CUBS = 142;

    public static final int numberofMr4ERS = 1130;

    public static final int numberofMr4MOS = 3512;


    public static final int K4ARTSUM = 10;


    public static int getPartitionNumber(String objectName){
        if (objectName.equals("ACMS"))
            return 8;
        else if (objectName.equals("CUBS"))
            return 3;
        else if (objectName.equals("ERS"))
            return 12;
        else
            return 10;
    }


    /**
     * 返回不同测试对象的变异体数目
     * @param objectName 测试对象的名字：ACMS CUBS ERS MOS
     * @return 变异体的数目
     */
    public static int getMutantsNumber(String objectName){
        if (objectName.equals("ACMS"))
            return 3;
        else if (objectName.equals("CUBS"))
            return 1;
        else if (objectName.equals("ERS"))
            return 1;
        else
            return 1;
    }





}
