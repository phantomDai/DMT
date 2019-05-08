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
import java.util.Random;

/**
 * describe: there are 4 strategies
 * the method of selecting source test case: R-APT, M-APT
 * the method of selecting MR: RMRS 和 PBRS
 * @author phantom
 * @date 2019/05/07
 */
public class DMT implements Strategy{

    /**the test profile of RAPT*/
    private double[] RAPT;

    /**the test profile of MAPT*/
    private double[][] MAPT;


    /**
     * initialize the test profile of RAPT
     * @param numberOfPartitions the number of partitions
     */
    private void initializeRAPT(int numberOfPartitions){
        RAPT = new double[numberOfPartitions];
        for (int i = 0; i < RAPT.length; i++) {
            RAPT[i] = 1.0 / numberOfPartitions;
        }
    }


    /**
     * initialize the test profile of RAPT
     * @param numberOfPartitions the number of partitions
     */
    private void initializeMAPT(int numberOfPartitions){
        MAPT = new double[numberOfPartitions][numberOfPartitions];
        for (int i = 0; i < numberOfPartitions; i++) {
            for (int j = 0; j < numberOfPartitions; j++) {
                MAPT[i][j] = 1 / numberOfPartitions;
            }
        }
    }


    /**
     * get a index of partition
     * Note that the first number of partitions is 0
     * @return the index
     */
    private int nextPartition4RAPT(){
        int index = -1;
        double randomNumber = new Random().nextDouble();
        double sum = 0;
        do {
            index++;
            sum += RAPT[index];
        } while (randomNumber >= sum && index < RAPT.length);
        return index;
    }


    /**
     * get a index of partition
     * Note that the first number of partitions is 0
     * @return the index
     */
    private int nextPartition4MAPT(int formerPartitionNumber){
        double[] tempArray = new double[MAPT.length];
        for (int i = 0; i < tempArray.length; i++) {
            tempArray[i] = MAPT[formerPartitionNumber][i];
        }
        int index = -1;
        double randomNumber = new Random().nextDouble();
        double sum = 0;
        do {
            index++;
            sum += tempArray[index];
        } while (randomNumber >= sum && index < tempArray.length);
        return index;
    }


    private void adjustRAPT(int formerPartitionIndex, boolean isKilledMutant){
        if (isKilledMutant){

        }
    }

    private void adjustMAPT(int formerPartitionIndex, boolean isKilledMutans){

    }






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
