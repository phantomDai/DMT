package labprograms.constant;

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
}
