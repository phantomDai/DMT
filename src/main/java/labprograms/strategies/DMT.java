package labprograms.strategies;

import labprograms.gethardkilledmutants.MT4ACMS;
import labprograms.gethardkilledmutants.MT4CUBS;
import labprograms.gethardkilledmutants.MT4ERS;
import labprograms.gethardkilledmutants.MT4MOS;
import labprograms.testCase.TestCase4ACMS;
import labprograms.testCase.TestCase4CUBS;
import labprograms.testCase.TestCase4ERS;
import labprograms.testCase.TestCase4MOS;

import java.util.List;

/**
 * describe: there are 4 strategies
 * the method of selecting source test case: R-APT, M-APT
 * the method of selecting MR: RMRS 和 PBRS
 * @author phantom
 * @date 2019/05/07
 */
public class DMT implements Strategy{




    @Override
    public Object getTestCase(String objectName, String testframe) {
        if (objectName.equals("ACMS")){
            TestCase4ACMS tc = new MT4ACMS().generateTestCase(testframe);
            return tc;
        }else if (objectName.equals("CUBS")){
            TestCase4CUBS tc = new MT4CUBS().generateTestCase(testframe);
            return tc;
        }else if (objectName.equals("ERS")){
            TestCase4ERS tc = new MT4ERS().generateTestCase(testframe);
            return tc;
        }else {
            TestCase4MOS tc = new MT4MOS().generateTestCase(testframe);
            return tc;
        }
    }







    @Override
    public void executeTestCase(String objectName) {

    }

    /**
     * calculate average time
     * @param time the array of all time
     * @return the average time
     */
    private double getAverageTime(List<Long> time){
        long temp = 0;
        for(long t : time){
            temp += t;
        }
        double result = temp / time.size();
        return result;
    }

    private double getAveragemeasure(List<Integer> time){
        int temp = 0;
        for(long t : time){
            temp += t;
        }
        double result = temp / time.size();
        return result;
    }


}
