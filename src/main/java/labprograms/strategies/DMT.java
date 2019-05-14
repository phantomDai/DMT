package labprograms.strategies;

import labprograms.MOS.sourceCode.MSR;
import labprograms.constant.Constant;
import labprograms.gethardkilledmutants.MT4ACMS;
import labprograms.gethardkilledmutants.MT4CUBS;
import labprograms.gethardkilledmutants.MT4ERS;
import labprograms.gethardkilledmutants.MT4MOS;
import labprograms.method.Methods4Testing;
import labprograms.mutants.Mutant;
import labprograms.mutants.UsedMutantsSet;
import labprograms.result.RecordResult;
import labprograms.strategies.util.Control;
import labprograms.strategies.util.GetNumber;
import labprograms.testCase.TestCase4ACMS;
import labprograms.testCase.TestCase4CUBS;
import labprograms.testCase.TestCase4ERS;
import labprograms.testCase.TestCase4MOS;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * describe: there are 4 strategies
 * the method of selecting source test case: R-APT, M-APT
 * the method of selecting MR: RMRS 和 PBRS
 * @author phantom
 * @date 2019/05/07
 */
public class DMT implements Strategy{

    private static final int[] pun4ACMS = {79,73,67,60,96,84,29,23};
    private static final int[] pun4CUBS = {50,5,43};
    private static final int[] pun4ERS = {91,89,88,126,62,39,39,51,55,52,35,53};
    private static final int[] pun4MOS = {390,298,403,261,403,217,226,84,132,21};
    /**the test profile of RAPT*/
    private double[] RAPT;

    /**the test profile of MAPT*/
    private double[][] MAPT;

    private double RAPT_epsilon = 0.05;

    private double RAPT_delta;

    /**the factor of reward*/
    private int[] rew;

    /**the factor of punishment*/
    private int[] bou;

    private int[] pun;

    private double MAPT_gamma = 0.1;

    private double MAPT_tau = 0.1;


    public void setRAPT_delta(double RAPT_delta) {
        this.RAPT_delta = RAPT_delta;
    }



    public void setBou_RAPT(String objectName, int[] punArray){
        if (objectName.equals("ACMS")){
            rew = new int[8];
            pun = new int[8];
            bou = new int[8];
            for (int i = 0; i < punArray.length; i++) {
                bou[i] = punArray[i];
            }
        }else if(objectName.equals("CUBS")){
            rew = new int[3];
            pun = new int[3];
            bou = new int[3];
            for (int i = 0; i < punArray.length; i++) {
                bou[i] = punArray[i];
            }
        }else if(objectName.equals("ERS")){
            rew = new int[12];
            pun = new int[12];
            bou = new int[12];
            for (int i = 0; i < punArray.length; i++) {
                bou[i] = punArray[i];
            }
        }else {
            rew = new int[10];
            pun = new int[10];
            bou = new int[10];
            for (int i = 0; i < punArray.length; i++) {
                bou[i] = punArray[i];
            }
        }

    }

    /**
     * initialize the test profile of RAPT
     * @param numberOfPartitions the number of partitions
     */
    private void initializeRAPT(int numberOfPartitions){
        RAPT = new double[numberOfPartitions];
        for (int i = 0; i < RAPT.length; i++) {
            RAPT[i] = 1.0 / numberOfPartitions;
            pun[i] = 0;
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
                MAPT[i][j] = 1.0 / numberOfPartitions;
            }
        }
    }


    /**
     * get a index of partition
     * Note that the first number of partitions is 0
     * @return the index
     */
    private int nextPartition4RAPT(){
        boolean flag = false;
        int partitionindex = 0;
        for (int i = 0; i < rew.length; i++) {
            if (rew[i] > 0){
                flag = true;
                partitionindex = i;
                break;
            }
        }
        if (flag){
            return partitionindex;
        }else {
            int index = -1;
            double randomNumber = new Random().nextDouble();
            double sum = 0;
            do {
                index++;
                sum += RAPT[index];
            } while (randomNumber >= sum && index < RAPT.length - 1);
            return index;
        }
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
        } while (randomNumber >= sum && index < tempArray.length - 1);
        return index;
    }


    /**
     * adjust the test profile for RAPT testing
     * @param
     * @param isKilledMutant
     */
    private void adjustRAPT(int formersourcePartitionIndex, int formerfollowUpPartitionIndex, boolean isKilledMutant){
        double old_i = RAPT[formersourcePartitionIndex];
        double old_f = RAPT[formerfollowUpPartitionIndex];

        if (formersourcePartitionIndex == formerfollowUpPartitionIndex){
            if (isKilledMutant){
                double sum = 0;
                for (int i = 0; i < RAPT.length; i++) {
                    if (i != formersourcePartitionIndex){
                        RAPT[i] -= (1 + Math.log(rew[formersourcePartitionIndex]))
                                * RAPT_epsilon / (RAPT.length - 1);
                        if (RAPT[i] < 0){
                            RAPT[i] = 0;
                        }
                    }
                    sum += RAPT[i];
                }
                RAPT[formersourcePartitionIndex] = 1 - sum;
            }else {
                for (int i = 0; i < RAPT.length; i++) {
                    if (i == formersourcePartitionIndex){
                        if (old_i >= RAPT_delta){
                            RAPT[i] -= RAPT_delta;
                        }
                        if (old_i < RAPT_delta || bou[i] == pun[i]){
                            RAPT[i] = 0;
                        }
                    }else {
                        if (old_i >= RAPT_delta){
                            RAPT[i] += RAPT_delta / (RAPT.length - 1);
                        }
                        if (old_i < RAPT_delta || bou[i] == pun[i]){
                            RAPT[i] += old_i / (RAPT.length - 1);
                        }
                    }
                }
            }
        }else { // not belong to the same partition
            if (isKilledMutant){
                double sum = 0;
                for (int i = 0; i < RAPT.length; i++) {
                    if (i != formersourcePartitionIndex &&i != formerfollowUpPartitionIndex){
                        if (RAPT[i] > (RAPT_epsilon *(1 + Math.log(rew[i])) / (RAPT.length - 2))){
                            RAPT[i] -= RAPT_epsilon *(1 + Math.log(rew[i])) / (RAPT.length - 2);
                        }else {
                            RAPT[i] = 0;
                        }
                    }
                    sum += RAPT[i];
                }
                RAPT[formersourcePartitionIndex] = old_i + ((1 - sum) - old_i - old_f) / 2;
                RAPT[formerfollowUpPartitionIndex] = old_f + ((1 - sum) - old_f - old_i) / 2;
            }else { // not reveal a mutant
                if (old_i > RAPT_delta){
                    RAPT[formersourcePartitionIndex] = old_i - RAPT_delta;
                }
                if (old_i <= RAPT_delta || pun[formersourcePartitionIndex] == bou[formersourcePartitionIndex]){
                    RAPT[formersourcePartitionIndex] = 0;
                    if(pun[formersourcePartitionIndex] == bou[formersourcePartitionIndex]){
                        pun[formersourcePartitionIndex] = 0;
                    }
                }
                if (old_f > RAPT_delta){
                    RAPT[formerfollowUpPartitionIndex] = old_f - RAPT_delta;
                }
                if (old_f <= RAPT_delta || pun[formerfollowUpPartitionIndex] == bou[formerfollowUpPartitionIndex]){
                    RAPT[formerfollowUpPartitionIndex] = 0;
                    if(pun[formersourcePartitionIndex] == bou[formersourcePartitionIndex]){
                        pun[formersourcePartitionIndex] = 0;
                    }
                }

                for (int i = 0; i < RAPT.length; i++) {
                    if (i != formersourcePartitionIndex && i != formerfollowUpPartitionIndex){
                        RAPT[i] += (( old_i - RAPT[formersourcePartitionIndex]) +
                                (old_f - RAPT[formerfollowUpPartitionIndex])) / (RAPT.length - 2);
                    }
                }
            }
        }

    }

    /**
     * adjust the test profile for MAPT testing
     * @param formerSourcePartitionIndex
     * @param formerfollowUpPartitionIndex
     * @param isKilledMutans
     */
    private void adjustMAPT(int formerSourcePartitionIndex, int formerfollowUpPartitionIndex, boolean isKilledMutans){
        //the source test case and follow-up test case belong to the same partition
        double old_i = MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex];
        double old_f = MAPT[formerSourcePartitionIndex][formerfollowUpPartitionIndex];


        if (formerSourcePartitionIndex == formerfollowUpPartitionIndex){
            // the test case killed a mutant
            if (isKilledMutans){ //same partition and killed a mutant
                double sum = 0;
                double threshold = MAPT_gamma * old_i / (MAPT.length - 1);
                for (int i = 0; i < MAPT.length; i++) {
                    if (i != formerSourcePartitionIndex){
                        if (MAPT[formerSourcePartitionIndex][i] > threshold){
                            MAPT[formerSourcePartitionIndex][i] -= threshold;
                        }
                    }
                    sum += MAPT[formerSourcePartitionIndex][i];
                }
                MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex] = 1 - sum ;
            }else { // same partition and do not kill a mutant
                double threshod = MAPT_tau * (1 - old_i) / (MAPT.length - 1);
                for (int i = 0; i < MAPT.length; i++) {
                    if (i != formerSourcePartitionIndex){
                        if (MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex] > threshod){
                            MAPT[formerSourcePartitionIndex][i] +=
                                    MAPT_tau * MAPT[formerSourcePartitionIndex][i] / (MAPT.length - 1);
                        }
                    }else {
                        if (MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex] > threshod){
                            MAPT[i][i] -= MAPT_tau * (1 - MAPT[i][i]) / (MAPT.length - 1);
                        }
                    }
                }
            }
        }else { //source test case and follow-up test case do not belong to same partition
            // the test case do not kill a mutant
            if (isKilledMutans){
                double sum = 0;
                double threshold = MAPT_gamma * (old_i + old_f) / (MAPT.length - 2);

                for (int i = 0; i < MAPT.length; i++) {
                    if (i != formerSourcePartitionIndex && i != formerfollowUpPartitionIndex){
                        if (MAPT[formerSourcePartitionIndex][i] > threshold){
                            MAPT[formerSourcePartitionIndex][i] -= threshold;
                        }
                    }
                    sum += MAPT[formerSourcePartitionIndex][i];
                }

                MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex] = old_i +
                        ((1 - sum) - old_i - old_f) / 2;
                MAPT[formerSourcePartitionIndex][formerfollowUpPartitionIndex] = old_f +
                        ((1 - sum) - old_i - old_f) / 2;
            }else { // source test case and follow-up test case are not belonging to the same partition and do not reveal a mutant

                double threshold = (MAPT_tau * (1 - old_i - old_f)) / (MAPT.length - 2);
                for (int i = 0; i < MAPT.length; i++) {
                    if (i != formerSourcePartitionIndex && i != formerfollowUpPartitionIndex){
                        if ( old_i > threshold || old_f > threshold){
                            MAPT[formerSourcePartitionIndex][i] += MAPT_tau *
                                    MAPT[formerSourcePartitionIndex][i] / (MAPT.length - 2);
                        }
                    }
                }
                if (old_i > threshold){
                    MAPT[formerSourcePartitionIndex][formerSourcePartitionIndex] -= threshold;
                }
                if (old_f > threshold){
                    MAPT[formerSourcePartitionIndex][formerfollowUpPartitionIndex] -= threshold;
                }

            }
        }
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
     * select test case : MAPT
     * select MR: RT
     */
    public void MAPTwithRandomlySelectMR(String objectName){

        //record all the time of selecting test cases for detecting the first fault
        List<Long> firstSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting the first fault
        List<Long> firstgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting the first fault
        List<Long> firstExecuteTestCaseArray = new ArrayList<>();

        //record all the time of selecting test cases for detecting all faults
        List<Long> allSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting all faults
        List<Long> allgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting allfaults
        List<Long> allExecuteTestCaseArray = new ArrayList<>();

        List<Integer> FmeasureArray = new ArrayList<>();

        List<Integer> TmeasureArray = new ArrayList<>();

        //实例化control类
        Control control = new Control(objectName);

        int numberOfMr = 0;
        String path = "";
        if (objectName.equals("ACMS")){
            numberOfMr = Constant.numberofMr4ACMS;
            path = Constant.mrPath4ACMS;
        } else if (objectName.equals("CUBS")){
            numberOfMr = Constant.numberofMr4CUBS;
            path = Constant.mrPath4CUBS;
        }else if (objectName.equals("ERS")){
            numberOfMr = Constant.numberofMr4ERS;
            path = Constant.mrPath4ERS;
        }else {
            numberOfMr = Constant.numberofMr4MOS;
            path = Constant.mrPath4MOS;
        }


        BufferedReader bufferedReader = null;

        Map<String,String> mrInfo = new HashMap<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String tempStr = "";
            int tempInteger = 1;
            while((tempStr = bufferedReader.readLine()) != null){
                mrInfo.put(String.valueOf(tempInteger), tempStr);
                tempInteger++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Constant.repeatNumber; i++) {

            System.out.println("DMT4" + objectName + "使用MAPT+RSMR:" + "执行第"+ String.valueOf(i + 1) + "次测试：" );
            //初始化测试剖面
            initializeMAPT(Constant.getPartitionNumber(objectName));

            UsedMutantsSet mutantsSet = new UsedMutantsSet(objectName);
            Map<String, Mutant> mutantMap = mutantsSet.getMutants();
            // the list that includes the killed mutants
            Set<String> killedMutants = new HashSet<>();

            String methodName = new Methods4Testing(objectName).getMethodName();
            // the counter
            int counter = 0;

            // the selecting time of killing the first mutant
            long firstSelectingTime = 0;
            // the generating time of killing the first mutant
            long firstGeneratingTime = 0;
            // the executing time of killing the first muatant
            long firstExecutingTime = 0;

            // the selecting time of killing all mutants
            long allSelectingTime = 0;
            // the generating time of killing all mutants
            long allGeneratingTime = 0;
            // the executing time of killing all mutants
            long allExecutingTime = 0;

            // the F-measure
            int Fmeasure = 0;
            // the T-measure
            int Tmeasure = 0;

            for (int j = 0; j < 10000; j++) {
                //get a test case
                String testframesAndMr = "";
                // the number of executing test case add 1
                counter++;
                boolean flag = false;
                String sourceTestFrame = "";
                String followTestFrame = "";

                /**choose a partition and a source test case*/
                long startSelectTestCase = System.nanoTime();
                // choose a partition according to the test profile
                int partitionIndex = 0;
                if (counter == 1){
                    partitionIndex = new Random().
                        nextInt(Constant.getPartitionNumber(objectName));
                }else {
                    partitionIndex = nextPartition4MAPT(partitionIndex);
                }

                //select a test case
                while(!flag){
                    int index = new Random().nextInt(numberOfMr) + 1;
                    testframesAndMr = mrInfo.get(String.valueOf(index));
                    sourceTestFrame = testframesAndMr.split(";")[0];
                    flag = control.isTestFrameBelongToPartition(partitionIndex, sourceTestFrame);
                }
                long endSelectTestCase = System.nanoTime();
                if (killedMutants.size() == 0){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }
                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                /**choose an MR*/
                String MR = control.randomlyGetMR(partitionIndex,sourceTestFrame);
                followTestFrame = MR.split(";")[1];
                if (!objectName.equals("MOS")){
                    MR = MR.split(";")[2];
                }else {
                    String tempStr = "";
                    for (int z = 2; z < MR.split(";").length; z++) {
                        tempStr += MR.split(";")[z] + ";";
                    }
                    MR = tempStr;
                    MR = MR.substring(0, MR.length() - 1);
                    // delete the medel change in the MR
                    String[] choice = MR.split(";");
                    MR = choice[0] +";" + choice[1]+";" + choice[2]+";" + choice[3]+";"
                            + choice[5]+";" + choice[6]+";" + choice[7];
                }

                int partitionIndexOffollowTestCase = control.
                        judgeThePartitionOfFollowTestFrame(objectName, followTestFrame.split(";")[1]);
                boolean isKilledMutants = false;
                for (Map.Entry<String, Mutant> entry : mutantMap.entrySet()){

                    if (killedMutants.contains(entry.getKey())){
                        continue;
                    }
                    Mutant mutant = entry.getValue();
                    Object mutantInstance = null;
                    Method mutantMethod = null;
                    Class mutantClazz = null;
                    try {
                        mutantClazz = Class.forName(mutant.getFullName());
                        Constructor mutantConstructor = mutantClazz.getConstructor();
                        mutantInstance = mutantConstructor.newInstance();
                        if (objectName.equals("ACMS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, int.class, int.class,
                                    boolean.class, double.class, double.class);
                            // generate source test case and follow-up test case
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ACMS sourceTestCase = (TestCase4ACMS) stc;
                            TestCase4ACMS followUpTestCase = (TestCase4ACMS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getAirClass(),sourceTestCase.getArea(),
                                    sourceTestCase.isStudent(),sourceTestCase.getLuggage(),
                                    sourceTestCase.getEconomicfee());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getAirClass(),followUpTestCase.getArea(),
                                    followUpTestCase.isStudent(),followUpTestCase.getLuggage(),
                                    followUpTestCase.getEconomicfee());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);

                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                        } else if (objectName.equals("CUBS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, int.class,
                                    int.class, int.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4CUBS sourceTestCase = (TestCase4CUBS) stc;
                            TestCase4CUBS followUpTestCase = (TestCase4CUBS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getPlanType(),
                                    sourceTestCase.getPlanFee(), sourceTestCase.getTalkTime(),sourceTestCase.getFlow());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getPlanType(),
                                    followUpTestCase.getPlanFee(), followUpTestCase.getTalkTime(),followUpTestCase.getFlow());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }else if (objectName.equals("ERS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, double.class,
                                    double.class, double.class, double.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ERS sourceTestCase = (TestCase4ERS) stc;
                            TestCase4ERS followUpTestCase = (TestCase4ERS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,sourceTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,followUpTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }else {
                            MSR sourceResult = null;
                            MSR followUpResult = null;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, String.class, int.class,
                                    String.class,int.class,int.class,int.class);

                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4MOS sourceTestCase = (TestCase4MOS) stc;
                            TestCase4MOS followUpTestCase = (TestCase4MOS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (MSR) mutantMethod.invoke(mutantInstance,sourceTestCase.getAircraftmodel(),
                                    sourceTestCase.getChangeinthenumberofcrewmembers(),sourceTestCase.getNewnumberofcrewmembers(),
                                    sourceTestCase.getChangeinthenumberofpilots(),sourceTestCase.getNewnumberofpilots(),
                                    sourceTestCase.getNumberofchildpassengers(),sourceTestCase.getNumberofrequestedbundlesofflowers());

                            followUpResult = (MSR) mutantMethod.invoke(mutantInstance,followUpTestCase.getAircraftmodel(),
                                    followUpTestCase.getChangeinthenumberofcrewmembers(),followUpTestCase.getNewnumberofcrewmembers(),
                                    followUpTestCase.getChangeinthenumberofpilots(),followUpTestCase.getNewnumberofpilots(),
                                    followUpTestCase.getNumberofchildpassengers(),followUpTestCase.getNumberofrequestedbundlesofflowers());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);


                            String resultRelation = "";


                            if (sourceResult.numberOfMealsForCrewMembers == followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForCrewMembers > followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfMealsForPilots == followUpResult.numberOfMealsForPilots){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForPilots > followUpResult.numberOfMealsForPilots){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfChildMeals == followUpResult.numberOfChildMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfChildMeals > followUpResult.numberOfChildMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBundlesOfFlowers == followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBundlesOfFlowers > followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfFirstClassMeals == followUpResult.numberOfFirstClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfFirstClassMeals > followUpResult.numberOfFirstClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBusinessClassMeals == followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBusinessClassMeals > followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfEconomicClassMeals == followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "do not change";
                            }else if (sourceResult.numberOfEconomicClassMeals > followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "decrease";
                            }else {
                                resultRelation += "increase";
                            }

                            if (!MR.equals(resultRelation)){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }//mutants
                adjustMAPT(partitionIndex,partitionIndexOffollowTestCase, isKilledMutants);
                if (killedMutants.size() == Constant.getMutantsNumber(objectName)){
                    break;
                }
//                printMAPT();
            }
            FmeasureArray.add(Fmeasure);
            TmeasureArray.add(Tmeasure);


            //add each time to array
            firstSelectTestCaseArray.add(firstSelectingTime);
            firstgenerateTestCaseArray.add(firstGeneratingTime);
            firstExecuteTestCaseArray.add(firstExecutingTime);
            allSelectTestCaseArray.add(allSelectingTime);
            allgenerateTestCaseArray.add(allGeneratingTime);
            allExecuteTestCaseArray.add(allExecutingTime);
        }

        RecordResult.recordResult("DMTwithMAPT_RT4" + objectName, FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));
    }


    /**
     * select test case : MAPT
     * select MR: PBMR
     */
    public void MAPTwithPSMRSelectMR(String objectName){
        //record all the time of selecting test cases for detecting the first fault
        List<Long> firstSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting the first fault
        List<Long> firstgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting the first fault
        List<Long> firstExecuteTestCaseArray = new ArrayList<>();

        //record all the time of selecting test cases for detecting all faults
        List<Long> allSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting all faults
        List<Long> allgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting allfaults
        List<Long> allExecuteTestCaseArray = new ArrayList<>();

        List<Integer> FmeasureArray = new ArrayList<>();

        List<Integer> TmeasureArray = new ArrayList<>();

        //实例化control类
        Control control = new Control(objectName);

        int numberOfMr = 0;
        String path = "";
        if (objectName.equals("ACMS")){
            numberOfMr = Constant.numberofMr4ACMS;
            path = Constant.mrPath4ACMS;
        } else if (objectName.equals("CUBS")){
            numberOfMr = Constant.numberofMr4CUBS;
            path = Constant.mrPath4CUBS;
        }else if (objectName.equals("ERS")){
            numberOfMr = Constant.numberofMr4ERS;
            path = Constant.mrPath4ERS;
        }else {
            numberOfMr = Constant.numberofMr4MOS;
            path = Constant.mrPath4MOS;
        }


        BufferedReader bufferedReader = null;

        Map<String,String> mrInfo = new HashMap<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String tempStr = "";
            int tempInteger = 1;
            while((tempStr = bufferedReader.readLine()) != null){
                mrInfo.put(String.valueOf(tempInteger), tempStr);
                tempInteger++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Constant.repeatNumber; i++) {

            System.out.println("DMT4" + objectName + "使用MAPT+RSMR:" + "执行第"+ String.valueOf(i + 1) + "次测试：" );
            //初始化测试剖面
            initializeMAPT(Constant.getPartitionNumber(objectName));

            UsedMutantsSet mutantsSet = new UsedMutantsSet(objectName);
            Map<String, Mutant> mutantMap = mutantsSet.getMutants();
            // the list that includes the killed mutants
            Set<String> killedMutants = new HashSet<>();

            String methodName = new Methods4Testing(objectName).getMethodName();
            // the counter
            int counter = 0;

            // the selecting time of killing the first mutant
            long firstSelectingTime = 0;
            // the generating time of killing the first mutant
            long firstGeneratingTime = 0;
            // the executing time of killing the first muatant
            long firstExecutingTime = 0;

            // the selecting time of killing all mutants
            long allSelectingTime = 0;
            // the generating time of killing all mutants
            long allGeneratingTime = 0;
            // the executing time of killing all mutants
            long allExecutingTime = 0;

            // the F-measure
            int Fmeasure = 0;
            // the T-measure
            int Tmeasure = 0;

            for (int j = 0; j < 10000; j++) {
                //get a test case
                String testframesAndMr = "";
                // the number of line (begin form 1)
                int lineNumber = 0;
                // the number of executing test case add 1
                counter++;
                boolean flag = false;
                String sourceTestFrame = "";
                String followTestFrame = "";

                /**choose a partition and a source test case*/
                long startSelectTestCase = System.nanoTime();
                // choose a partition according to the test profile
                int partitionIndex = 0;
                if (counter == 1){
                    partitionIndex = new Random().
                            nextInt(Constant.getPartitionNumber(objectName));
                }else {
                    partitionIndex = nextPartition4MAPT(partitionIndex);
                }

                //select a test case
                while(!flag){
                    int index = new Random().nextInt(numberOfMr) + 1;
                    testframesAndMr = mrInfo.get(String.valueOf(index));
                    sourceTestFrame = testframesAndMr.split(";")[0];
                    flag = control.isTestFrameBelongToPartition(partitionIndex, sourceTestFrame);
                }
                long endSelectTestCase = System.nanoTime();
                if (killedMutants.size() == 0){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }
                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                /**choose an MR*/
                String MR = control.PBMRGetMR(partitionIndex,sourceTestFrame);
                followTestFrame = MR.split(";")[1];
                if (!objectName.equals("MOS")){
                    MR = MR.split(";")[2];
                }else {
                    String tempStr = "";
                    for (int z = 2; z < MR.split(";").length; z++) {
                        tempStr += MR.split(";")[z] + ";";
                    }
                    MR = tempStr;
                    MR = MR.substring(0, MR.length() - 1);
                    // delete the medel change in the MR
                    String[] choice = MR.split(";");
                    MR = choice[0] +";" + choice[1]+";" + choice[2]+";" + choice[3]+";"
                            + choice[5]+";" + choice[6]+";" + choice[7];
                }

                int partitionIndexOffollowTestCase = control.
                        judgeThePartitionOfFollowTestFrame(objectName, followTestFrame.split(";")[1]);
                boolean isKilledMutants = false;
                for (Map.Entry<String, Mutant> entry : mutantMap.entrySet()){

                    if (killedMutants.contains(entry.getKey())){
                        continue;
                    }
                    Mutant mutant = entry.getValue();
                    Object mutantInstance = null;
                    Method mutantMethod = null;
                    Class mutantClazz = null;
                    try {
                        mutantClazz = Class.forName(mutant.getFullName());
                        Constructor mutantConstructor = mutantClazz.getConstructor();
                        mutantInstance = mutantConstructor.newInstance();
                        if (objectName.equals("ACMS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, int.class, int.class,
                                    boolean.class, double.class, double.class);
                            // generate source test case and follow-up test case
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ACMS sourceTestCase = (TestCase4ACMS) stc;
                            TestCase4ACMS followUpTestCase = (TestCase4ACMS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getAirClass(),sourceTestCase.getArea(),
                                    sourceTestCase.isStudent(),sourceTestCase.getLuggage(),
                                    sourceTestCase.getEconomicfee());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getAirClass(),followUpTestCase.getArea(),
                                    followUpTestCase.isStudent(),followUpTestCase.getLuggage(),
                                    followUpTestCase.getEconomicfee());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);

                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                        } else if (objectName.equals("CUBS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, int.class,
                                    int.class, int.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4CUBS sourceTestCase = (TestCase4CUBS) stc;
                            TestCase4CUBS followUpTestCase = (TestCase4CUBS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getPlanType(),
                                    sourceTestCase.getPlanFee(), sourceTestCase.getTalkTime(),sourceTestCase.getFlow());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getPlanType(),
                                    followUpTestCase.getPlanFee(), followUpTestCase.getTalkTime(),followUpTestCase.getFlow());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }else if (objectName.equals("ERS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, double.class,
                                    double.class, double.class, double.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ERS sourceTestCase = (TestCase4ERS) stc;
                            TestCase4ERS followUpTestCase = (TestCase4ERS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,sourceTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,followUpTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }else {
                            MSR sourceResult = null;
                            MSR followUpResult = null;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, String.class, int.class,
                                    String.class,int.class,int.class,int.class);

                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4MOS sourceTestCase = (TestCase4MOS) stc;
                            TestCase4MOS followUpTestCase = (TestCase4MOS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (MSR) mutantMethod.invoke(mutantInstance,sourceTestCase.getAircraftmodel(),
                                    sourceTestCase.getChangeinthenumberofcrewmembers(),sourceTestCase.getNewnumberofcrewmembers(),
                                    sourceTestCase.getChangeinthenumberofpilots(),sourceTestCase.getNewnumberofpilots(),
                                    sourceTestCase.getNumberofchildpassengers(),sourceTestCase.getNumberofrequestedbundlesofflowers());

                            followUpResult = (MSR) mutantMethod.invoke(mutantInstance,followUpTestCase.getAircraftmodel(),
                                    followUpTestCase.getChangeinthenumberofcrewmembers(),followUpTestCase.getNewnumberofcrewmembers(),
                                    followUpTestCase.getChangeinthenumberofpilots(),followUpTestCase.getNewnumberofpilots(),
                                    followUpTestCase.getNumberofchildpassengers(),followUpTestCase.getNumberofrequestedbundlesofflowers());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);


                            String resultRelation = "";


                            if (sourceResult.numberOfMealsForCrewMembers == followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForCrewMembers > followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfMealsForPilots == followUpResult.numberOfMealsForPilots){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForPilots > followUpResult.numberOfMealsForPilots){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfChildMeals == followUpResult.numberOfChildMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfChildMeals > followUpResult.numberOfChildMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBundlesOfFlowers == followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBundlesOfFlowers > followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfFirstClassMeals == followUpResult.numberOfFirstClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfFirstClassMeals > followUpResult.numberOfFirstClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBusinessClassMeals == followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBusinessClassMeals > followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfEconomicClassMeals == followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "do not change";
                            }else if (sourceResult.numberOfEconomicClassMeals > followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "decrease";
                            }else {
                                resultRelation += "increase";
                            }

                            if (!MR.equals(resultRelation)){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }//mutants
                adjustMAPT(partitionIndex,partitionIndexOffollowTestCase, isKilledMutants);
                if (killedMutants.size() == Constant.getMutantsNumber(objectName)){
                    break;
                }
//                printMAPT();
            }
            FmeasureArray.add(Fmeasure);
            TmeasureArray.add(Tmeasure);


            //add each time to array
            firstSelectTestCaseArray.add(firstSelectingTime);
            firstgenerateTestCaseArray.add(firstGeneratingTime);
            firstExecuteTestCaseArray.add(firstExecutingTime);
            allSelectTestCaseArray.add(allSelectingTime);
            allgenerateTestCaseArray.add(allGeneratingTime);
            allExecuteTestCaseArray.add(allExecutingTime);
        }

        RecordResult.recordResult("DMTwithMAPT_PBmr4" + objectName, FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));
    }

    /**
     * select test case : RAPT
     * select MR: RT
     */
    public void RAPTwithRandomlySelectMR(String objectName){

        GetNumber getNumber = new GetNumber();
        //record all the time of selecting test cases for detecting the first fault
        List<Long> firstSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting the first fault
        List<Long> firstgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting the first fault
        List<Long> firstExecuteTestCaseArray = new ArrayList<>();

        //record all the time of selecting test cases for detecting all faults
        List<Long> allSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting all faults
        List<Long> allgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting allfaults
        List<Long> allExecuteTestCaseArray = new ArrayList<>();

        List<Integer> FmeasureArray = new ArrayList<>();

        List<Integer> TmeasureArray = new ArrayList<>();

        //实例化control类
        Control control = new Control(objectName);

        int numberOfMr = 0;
        String path = "";
        if (objectName.equals("ACMS")){
            numberOfMr = Constant.numberofMr4ACMS;
            path = Constant.mrPath4ACMS;
        } else if (objectName.equals("CUBS")){
            numberOfMr = Constant.numberofMr4CUBS;
            path = Constant.mrPath4CUBS;
        }else if (objectName.equals("ERS")){
            numberOfMr = Constant.numberofMr4ERS;
            path = Constant.mrPath4ERS;
        }else {
            numberOfMr = Constant.numberofMr4MOS;
            path = Constant.mrPath4MOS;
        }


        BufferedReader bufferedReader = null;

        Map<String,String> mrInfo = new HashMap<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String tempStr = "";
            int tempInteger = 1;
            while((tempStr = bufferedReader.readLine()) != null){
                mrInfo.put(String.valueOf(tempInteger), tempStr);
                tempInteger++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Constant.repeatNumber; i++) {
            setParameters4RAPT(objectName);

            System.out.println("DMT4" + objectName + "使用RAPT+RSMR:" + "执行第"+ String.valueOf(i + 1) + "次测试：" );
            //初始化测试剖面
            initializeRAPT(Constant.getPartitionNumber(objectName));

            UsedMutantsSet mutantsSet = new UsedMutantsSet(objectName);
            Map<String, Mutant> mutantMap = mutantsSet.getMutants();
            // the list that includes the killed mutants
            Set<String> killedMutants = new HashSet<>();

            String methodName = new Methods4Testing(objectName).getMethodName();
            // the counter
            int counter = 0;

            // the selecting time of killing the first mutant
            long firstSelectingTime = 0;
            // the generating time of killing the first mutant
            long firstGeneratingTime = 0;
            // the executing time of killing the first muatant
            long firstExecutingTime = 0;

            // the selecting time of killing all mutants
            long allSelectingTime = 0;
            // the generating time of killing all mutants
            long allGeneratingTime = 0;
            // the executing time of killing all mutants
            long allExecutingTime = 0;

            // the F-measure
            int Fmeasure = 0;
            // the T-measure
            int Tmeasure = 0;

            for (int j = 0; j < 10000; j++) {
                //get a test case
                String testframesAndMr = "";

                // the number of executing test case add 1
                counter++;
                boolean flag = false;
                String sourceTestFrame = "";
                String followTestFrame = "";

                /**choose a partition and a source test case*/
                long startSelectTestCase = System.nanoTime();
                // choose a partition according to the test profile
                int partitionIndex = 0;

                double[] tempArray = RAPT;
                partitionIndex = nextPartition4RAPT();

                int index = 0;
                //select a test case
                while(!flag){
                    index = new Random().nextInt(numberOfMr) + 1;
                    testframesAndMr = mrInfo.get(String.valueOf(index));
                    sourceTestFrame = testframesAndMr.split(";")[0];
                    flag = control.isTestFrameBelongToPartition(partitionIndex, sourceTestFrame);
                }
                long endSelectTestCase = System.nanoTime();
                if (killedMutants.size() == 0){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }
                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                /**choose an MR*/
                String MR = control.randomlyGetMR(partitionIndex,sourceTestFrame);

                int realIndex = getNumber.getNumber(MR,objectName);
                double a = tempArray[0] + tempArray[1] + tempArray[2]+ tempArray[3]+ tempArray[4]+ tempArray[5]
                        + tempArray[6]+ tempArray[7];
                followTestFrame = MR.split(";")[1];
                if (!objectName.equals("MOS")){
                    MR = MR.split(";")[2];
                }else {
                    String tempStr = "";
                    for (int z = 2; z < MR.split(";").length; z++) {
                        tempStr += MR.split(";")[z] + ";";
                    }
                    MR = tempStr;
                    MR = MR.substring(0, MR.length() - 1);
                    // delete the medel change in the MR
                    String[] choice = MR.split(";");
                    MR = choice[0] +";" + choice[1]+";" + choice[2]+";" + choice[3]+";"
                            + choice[5]+";" + choice[6]+";" + choice[7];
                }

                int partitionIndexOffollowTestCase = control.
                        judgeThePartitionOfFollowTestFrame(objectName, followTestFrame);
                boolean isKilledMutants = false;
                for (Map.Entry<String, Mutant> entry : mutantMap.entrySet()){

                    if (killedMutants.contains(entry.getKey())){
                        continue;
                    }
                    Mutant mutant = entry.getValue();
                    Object mutantInstance = null;
                    Method mutantMethod = null;
                    Class mutantClazz = null;
                    try {
                        mutantClazz = Class.forName(mutant.getFullName());
                        Constructor mutantConstructor = mutantClazz.getConstructor();
                        mutantInstance = mutantConstructor.newInstance();
                        if (objectName.equals("ACMS")){

                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, int.class, int.class,
                                    boolean.class, double.class, double.class);
                            // generate source test case and follow-up test case
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ACMS sourceTestCase = (TestCase4ACMS) stc;
                            TestCase4ACMS followUpTestCase = (TestCase4ACMS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getAirClass(),sourceTestCase.getArea(),
                                    sourceTestCase.isStudent(),sourceTestCase.getLuggage(),
                                    sourceTestCase.getEconomicfee());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getAirClass(),followUpTestCase.getArea(),
                                    followUpTestCase.isStudent(),followUpTestCase.getLuggage(),
                                    followUpTestCase.getEconomicfee());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);

                            if (MR.equals("The output will not change") && sourceResult != followUpResult){

                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        } else if (objectName.equals("CUBS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, int.class,
                                    int.class, int.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4CUBS sourceTestCase = (TestCase4CUBS) stc;
                            TestCase4CUBS followUpTestCase = (TestCase4CUBS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getPlanType(),
                                    sourceTestCase.getPlanFee(), sourceTestCase.getTalkTime(),sourceTestCase.getFlow());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getPlanType(),
                                    followUpTestCase.getPlanFee(), followUpTestCase.getTalkTime(),followUpTestCase.getFlow());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }




                        }else if (objectName.equals("ERS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, double.class,
                                    double.class, double.class, double.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ERS sourceTestCase = (TestCase4ERS) stc;
                            TestCase4ERS followUpTestCase = (TestCase4ERS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,sourceTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,followUpTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }



                        }else {
                            MSR sourceResult = null;
                            MSR followUpResult = null;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, String.class, int.class,
                                    String.class,int.class,int.class,int.class);

                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4MOS sourceTestCase = (TestCase4MOS) stc;
                            TestCase4MOS followUpTestCase = (TestCase4MOS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (MSR) mutantMethod.invoke(mutantInstance,sourceTestCase.getAircraftmodel(),
                                    sourceTestCase.getChangeinthenumberofcrewmembers(),sourceTestCase.getNewnumberofcrewmembers(),
                                    sourceTestCase.getChangeinthenumberofpilots(),sourceTestCase.getNewnumberofpilots(),
                                    sourceTestCase.getNumberofchildpassengers(),sourceTestCase.getNumberofrequestedbundlesofflowers());

                            followUpResult = (MSR) mutantMethod.invoke(mutantInstance,followUpTestCase.getAircraftmodel(),
                                    followUpTestCase.getChangeinthenumberofcrewmembers(),followUpTestCase.getNewnumberofcrewmembers(),
                                    followUpTestCase.getChangeinthenumberofpilots(),followUpTestCase.getNewnumberofpilots(),
                                    followUpTestCase.getNumberofchildpassengers(),followUpTestCase.getNumberofrequestedbundlesofflowers());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);


                            String resultRelation = "";


                            if (sourceResult.numberOfMealsForCrewMembers == followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForCrewMembers > followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfMealsForPilots == followUpResult.numberOfMealsForPilots){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForPilots > followUpResult.numberOfMealsForPilots){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfChildMeals == followUpResult.numberOfChildMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfChildMeals > followUpResult.numberOfChildMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBundlesOfFlowers == followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBundlesOfFlowers > followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfFirstClassMeals == followUpResult.numberOfFirstClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfFirstClassMeals > followUpResult.numberOfFirstClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBusinessClassMeals == followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBusinessClassMeals > followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfEconomicClassMeals == followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "do not change";
                            }else if (sourceResult.numberOfEconomicClassMeals > followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "decrease";
                            }else {
                                resultRelation += "increase";
                            }

                            if (!MR.equals(resultRelation)){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }//mutants

                if (isKilledMutants){
                    if (partitionIndex == partitionIndexOffollowTestCase){
                        rew[partitionIndex]++;
                        pun[partitionIndex] = 0;
                    }else {
                        rew[partitionIndex]++;
                        rew[partitionIndexOffollowTestCase]++;
                        pun[partitionIndex] = 0;
                        pun[partitionIndexOffollowTestCase] = 0;
                    }
                }else {
                    adjustRAPT(partitionIndex,partitionIndexOffollowTestCase,isKilledMutants);
                    if (partitionIndex == partitionIndexOffollowTestCase){
                        rew[partitionIndex] = 0;
                        pun[partitionIndex]++;
                    }else {
                        rew[partitionIndex] =0;
                        rew[partitionIndexOffollowTestCase] = 0;
                        pun[partitionIndex]++;
                        pun[partitionIndexOffollowTestCase]++;
                    }
                }
//                adjustRAPT(partitionIndex,partitionIndexOffollowTestCase,isKilledMutants);
//                adjustMAPT(partitionIndex,partitionIndexOffollowTestCase, isKilledMutants);
                if (killedMutants.size() == Constant.getMutantsNumber(objectName)){
                    break;
                }
//                printMAPT();
            }
            FmeasureArray.add(Fmeasure);
            TmeasureArray.add(Tmeasure);


            //add each time to array
            firstSelectTestCaseArray.add(firstSelectingTime);
            firstgenerateTestCaseArray.add(firstGeneratingTime);
            firstExecuteTestCaseArray.add(firstExecutingTime);
            allSelectTestCaseArray.add(allSelectingTime);
            allgenerateTestCaseArray.add(allGeneratingTime);
            allExecuteTestCaseArray.add(allExecutingTime);
        }

        RecordResult.recordResult("DMTwithRAPT_RT4" + objectName, FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));
    }


    /**
     * select test case : RAPT
     * select MR: PBMR
     */
    public void RAPTwithPSMRSelectMR(String objectName){
        GetNumber getNumber = new GetNumber();
        //record all the time of selecting test cases for detecting the first fault
        List<Long> firstSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting the first fault
        List<Long> firstgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting the first fault
        List<Long> firstExecuteTestCaseArray = new ArrayList<>();

        //record all the time of selecting test cases for detecting all faults
        List<Long> allSelectTestCaseArray = new ArrayList<>();

        //record all the time of generate test cases for detecting all faults
        List<Long> allgenerateTestCaseArray = new ArrayList<>();

        //record all the time of execute test cases for detecting allfaults
        List<Long> allExecuteTestCaseArray = new ArrayList<>();

        List<Integer> FmeasureArray = new ArrayList<>();

        List<Integer> TmeasureArray = new ArrayList<>();

        //实例化control类
        Control control = new Control(objectName);

        int numberOfMr = 0;
        String path = "";
        if (objectName.equals("ACMS")){
            numberOfMr = Constant.numberofMr4ACMS;
            path = Constant.mrPath4ACMS;
        } else if (objectName.equals("CUBS")){
            numberOfMr = Constant.numberofMr4CUBS;
            path = Constant.mrPath4CUBS;
        }else if (objectName.equals("ERS")){
            numberOfMr = Constant.numberofMr4ERS;
            path = Constant.mrPath4ERS;
        }else {
            numberOfMr = Constant.numberofMr4MOS;
            path = Constant.mrPath4MOS;
        }


        BufferedReader bufferedReader = null;

        Map<String,String> mrInfo = new HashMap<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String tempStr = "";
            int tempInteger = 1;
            while((tempStr = bufferedReader.readLine()) != null){
                mrInfo.put(String.valueOf(tempInteger), tempStr);
                tempInteger++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Constant.repeatNumber; i++) {
            setParameters4RAPT(objectName);

            System.out.println("DMT4" + objectName + "使用RAPT+RSMR:" + "执行第"+ String.valueOf(i + 1) + "次测试：" );
            //初始化测试剖面
            initializeRAPT(Constant.getPartitionNumber(objectName));

            UsedMutantsSet mutantsSet = new UsedMutantsSet(objectName);
            Map<String, Mutant> mutantMap = mutantsSet.getMutants();
            // the list that includes the killed mutants
            Set<String> killedMutants = new HashSet<>();

            String methodName = new Methods4Testing(objectName).getMethodName();
            // the counter
            int counter = 0;

            // the selecting time of killing the first mutant
            long firstSelectingTime = 0;
            // the generating time of killing the first mutant
            long firstGeneratingTime = 0;
            // the executing time of killing the first muatant
            long firstExecutingTime = 0;

            // the selecting time of killing all mutants
            long allSelectingTime = 0;
            // the generating time of killing all mutants
            long allGeneratingTime = 0;
            // the executing time of killing all mutants
            long allExecutingTime = 0;

            // the F-measure
            int Fmeasure = 0;
            // the T-measure
            int Tmeasure = 0;

            for (int j = 0; j < 10000; j++) {
                //get a test case
                String testframesAndMr = "";

                // the number of executing test case add 1
                counter++;
                boolean flag = false;
                String sourceTestFrame = "";
                String followTestFrame = "";

                /**choose a partition and a source test case*/
                long startSelectTestCase = System.nanoTime();
                // choose a partition according to the test profile
                int partitionIndex = 0;

                double[] tempArray = RAPT;
                partitionIndex = nextPartition4RAPT();

                int index = 0;
                //select a test case
                while(!flag){
                    index = new Random().nextInt(numberOfMr) + 1;
                    testframesAndMr = mrInfo.get(String.valueOf(index));
                    sourceTestFrame = testframesAndMr.split(";")[0];
                    flag = control.isTestFrameBelongToPartition(partitionIndex, sourceTestFrame);
                }
                long endSelectTestCase = System.nanoTime();
                if (killedMutants.size() == 0){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }
                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                /**choose an MR*/
//                String MR = control.randomlyGetMR(partitionIndex,sourceTestFrame);
                String MR = control.PBMRGetMR(partitionIndex, sourceTestFrame);
                int realIndex = getNumber.getNumber(MR,objectName);
                double a = tempArray[0] + tempArray[1] + tempArray[2]+ tempArray[3]+ tempArray[4]+ tempArray[5]
                        + tempArray[6]+ tempArray[7];
                followTestFrame = MR.split(";")[1];
                if (!objectName.equals("MOS")){
                    MR = MR.split(";")[2];
                }else {
                    String tempStr = "";
                    for (int z = 2; z < MR.split(";").length; z++) {
                        tempStr += MR.split(";")[z] + ";";
                    }
                    MR = tempStr;
                    MR = MR.substring(0, MR.length() - 1);
                    // delete the medel change in the MR
                    String[] choice = MR.split(";");
                    MR = choice[0] +";" + choice[1]+";" + choice[2]+";" + choice[3]+";"
                            + choice[5]+";" + choice[6]+";" + choice[7];
                }

                int partitionIndexOffollowTestCase = control.
                        judgeThePartitionOfFollowTestFrame(objectName, followTestFrame);
                boolean isKilledMutants = false;
                for (Map.Entry<String, Mutant> entry : mutantMap.entrySet()){

                    if (killedMutants.contains(entry.getKey())){
                        continue;
                    }
                    Mutant mutant = entry.getValue();
                    Object mutantInstance = null;
                    Method mutantMethod = null;
                    Class mutantClazz = null;
                    try {
                        mutantClazz = Class.forName(mutant.getFullName());
                        Constructor mutantConstructor = mutantClazz.getConstructor();
                        mutantInstance = mutantConstructor.newInstance();
                        if (objectName.equals("ACMS")){

                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, int.class, int.class,
                                    boolean.class, double.class, double.class);
                            // generate source test case and follow-up test case
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ACMS sourceTestCase = (TestCase4ACMS) stc;
                            TestCase4ACMS followUpTestCase = (TestCase4ACMS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getAirClass(),sourceTestCase.getArea(),
                                    sourceTestCase.isStudent(),sourceTestCase.getLuggage(),
                                    sourceTestCase.getEconomicfee());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getAirClass(),followUpTestCase.getArea(),
                                    followUpTestCase.isStudent(),followUpTestCase.getLuggage(),
                                    followUpTestCase.getEconomicfee());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult){

                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                        } else if (objectName.equals("CUBS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, int.class,
                                    int.class, int.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4CUBS sourceTestCase = (TestCase4CUBS) stc;
                            TestCase4CUBS followUpTestCase = (TestCase4CUBS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                    sourceTestCase.getPlanType(),
                                    sourceTestCase.getPlanFee(), sourceTestCase.getTalkTime(),sourceTestCase.getFlow());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                    followUpTestCase.getPlanType(),
                                    followUpTestCase.getPlanFee(), followUpTestCase.getTalkTime(),followUpTestCase.getFlow());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }




                        }else if (objectName.equals("ERS")){
                            double sourceResult = 0;
                            double followUpResult = 0;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, double.class,
                                    double.class, double.class, double.class);
                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4ERS sourceTestCase = (TestCase4ERS) stc;
                            TestCase4ERS followUpTestCase = (TestCase4ERS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (double) mutantMethod.invoke(mutantInstance,sourceTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());

                            followUpResult = (double) mutantMethod.invoke(mutantInstance,followUpTestCase.getStafflevel(),
                                    sourceTestCase.getActualmonthlymileage(), sourceTestCase.getMonthlysalesamount(),
                                    sourceTestCase.getAirfareamount(), sourceTestCase.getOtherexpensesamount());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            if (MR.equals("The output will not change") && sourceResult != followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                            if (MR.equals("The output will increase") && sourceResult >= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult) {
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                        }else {
                            MSR sourceResult = null;
                            MSR followUpResult = null;
                            mutantMethod = mutantClazz.getMethod(methodName, String.class, String.class, int.class,
                                    String.class,int.class,int.class,int.class);

                            long startGenerateTestCase = System.nanoTime();
                            Object stc = getTestCase(objectName,sourceTestFrame);
                            Object ftc = getTestCase(objectName,followTestFrame);
                            TestCase4MOS sourceTestCase = (TestCase4MOS) stc;
                            TestCase4MOS followUpTestCase = (TestCase4MOS) ftc;
                            long endGenerateTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                            }
                            allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);


                            //execute source test case and follow-up test case on all mutants
                            long startExecuteTestCase = System.nanoTime();
                            sourceResult = (MSR) mutantMethod.invoke(mutantInstance,sourceTestCase.getAircraftmodel(),
                                    sourceTestCase.getChangeinthenumberofcrewmembers(),sourceTestCase.getNewnumberofcrewmembers(),
                                    sourceTestCase.getChangeinthenumberofpilots(),sourceTestCase.getNewnumberofpilots(),
                                    sourceTestCase.getNumberofchildpassengers(),sourceTestCase.getNumberofrequestedbundlesofflowers());

                            followUpResult = (MSR) mutantMethod.invoke(mutantInstance,followUpTestCase.getAircraftmodel(),
                                    followUpTestCase.getChangeinthenumberofcrewmembers(),followUpTestCase.getNewnumberofcrewmembers(),
                                    followUpTestCase.getChangeinthenumberofpilots(),followUpTestCase.getNewnumberofpilots(),
                                    followUpTestCase.getNumberofchildpassengers(),followUpTestCase.getNumberofrequestedbundlesofflowers());
                            long endExecuteTestCase = System.nanoTime();
                            if (killedMutants.size() == 0){
                                firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                            }
                            allExecutingTime += (endExecuteTestCase - startExecuteTestCase);


                            String resultRelation = "";


                            if (sourceResult.numberOfMealsForCrewMembers == followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForCrewMembers > followUpResult.numberOfMealsForCrewMembers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfMealsForPilots == followUpResult.numberOfMealsForPilots){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfMealsForPilots > followUpResult.numberOfMealsForPilots){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfChildMeals == followUpResult.numberOfChildMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfChildMeals > followUpResult.numberOfChildMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBundlesOfFlowers == followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBundlesOfFlowers > followUpResult.numberOfBundlesOfFlowers){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfFirstClassMeals == followUpResult.numberOfFirstClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfFirstClassMeals > followUpResult.numberOfFirstClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfBusinessClassMeals == followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "do not change;";
                            }else if (sourceResult.numberOfBusinessClassMeals > followUpResult.numberOfBusinessClassMeals){
                                resultRelation += "decrease;";
                            }else {
                                resultRelation += "increase;";
                            }

                            if (sourceResult.numberOfEconomicClassMeals == followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "do not change";
                            }else if (sourceResult.numberOfEconomicClassMeals > followUpResult.numberOfEconomicClassMeals){
                                resultRelation += "decrease";
                            }else {
                                resultRelation += "increase";
                            }

                            if (!MR.equals(resultRelation)){
                                isKilledMutants = true;
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;
                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }//mutants

                if (isKilledMutants){
                    if (partitionIndex == partitionIndexOffollowTestCase){
                        rew[partitionIndex]++;
                        pun[partitionIndex] = 0;
                    }else {
                        rew[partitionIndex]++;
                        rew[partitionIndexOffollowTestCase]++;
                        pun[partitionIndex] = 0;
                        pun[partitionIndexOffollowTestCase] = 0;
                    }
                }else {
                    adjustRAPT(partitionIndex,partitionIndexOffollowTestCase,isKilledMutants);
                    if (partitionIndex == partitionIndexOffollowTestCase){
                        rew[partitionIndex] = 0;
                        pun[partitionIndex]++;
                    }else {
                        rew[partitionIndex] =0;
                        rew[partitionIndexOffollowTestCase] = 0;
                        pun[partitionIndex]++;
                        pun[partitionIndexOffollowTestCase]++;
                    }
                }
//                adjustRAPT(partitionIndex,partitionIndexOffollowTestCase,isKilledMutants);
//                adjustMAPT(partitionIndex,partitionIndexOffollowTestCase, isKilledMutants);
                if (killedMutants.size() == Constant.getMutantsNumber(objectName)){
                    break;
                }
//                printMAPT();
            }
            FmeasureArray.add(Fmeasure);
            TmeasureArray.add(Tmeasure);


            //add each time to array
            firstSelectTestCaseArray.add(firstSelectingTime);
            firstgenerateTestCaseArray.add(firstGeneratingTime);
            firstExecuteTestCaseArray.add(firstExecutingTime);
            allSelectTestCaseArray.add(allSelectingTime);
            allgenerateTestCaseArray.add(allGeneratingTime);
            allExecuteTestCaseArray.add(allExecutingTime);
        }

        RecordResult.recordResult("DMTwithRAPT_PSMR4" + objectName, FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));
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



    private void printMAPT(){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < MAPT.length; i++) {
            String temp = "";
            for (int j = 0; j < MAPT.length; j++) {
                temp += String.valueOf(MAPT[i][j]) + ", ";
            }
            stringBuffer.append(temp + "\n");
        }
        System.out.println(stringBuffer.toString());
    }



    private void setParameters4RAPT(String objectName){
        if (objectName.equals("ACMS")){
            setRAPT_delta(0.01718312324929972);
            setBou_RAPT(objectName,pun4ACMS);
        }else if (objectName.equals("CUBS")){
            setRAPT_delta(0.026004801920768313);
            setBou_RAPT(objectName,pun4CUBS);
        }else if (objectName.equals("ERS")){
            setRAPT_delta(0.019839685749552596);
            setBou_RAPT(objectName,pun4ERS);
        }else {
            setRAPT_delta(0.009424821887743086);
            setBou_RAPT(objectName,pun4MOS);
        }
    }





    public static void main(String[] args) {
        DMT dmt = new DMT();
//        dmt.MAPTwithRandomlySelectMR("ACMS");
//        dmt.MAPTwithPSMRSelectMR("ACMS");
        dmt.RAPTwithRandomlySelectMR("ACMS");
    }


}
