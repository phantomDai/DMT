package labprograms.strategies;

import labprograms.MOS.sourceCode.MSR;
import labprograms.constant.Constant;
import labprograms.gethardkilledmutants.MT4ACMS;
import labprograms.gethardkilledmutants.MT4CUBS;
import labprograms.gethardkilledmutants.MT4ERS;
import labprograms.gethardkilledmutants.MT4MOS;
import labprograms.log.WriteDataToExcel;
import labprograms.method.Methods4Testing;
import labprograms.mutants.Mutant;
import labprograms.mutants.UsedMutantsSet;
import labprograms.result.RecordResult;
import labprograms.testCase.TestCase4ACMS;
import labprograms.testCase.TestCase4CUBS;
import labprograms.testCase.TestCase4ERS;
import labprograms.testCase.TestCase4MOS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * describe:
 * the method of selecting source test case: random
 * the method of selecting MR: random
 * @author phantom
 * @date 2019/05/06
 */
public class MT implements Strategy{

    /**
     * generate test case according to the test frame
     * @param objectName SUT
     * @param testframe the test frame
     * @return a test case
     */
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
    public void executeTestCase(String objectName, int in) {


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
            System.out.println("MT4" + objectName + ":" + "执行第"+ String.valueOf(i + 1) + "次测试：" );

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

                //select a test case
                long startSelectTestCase = System.nanoTime();
                int index = new Random().nextInt(numberOfMr) + 1;
                // the number of executing test case add 1
                counter++;
//                int index = new Random().nextInt(1) + 374;
                testframesAndMr = mrInfo.get(String.valueOf(index));

                long endSelectTestCase = System.nanoTime();
                if (killedMutants.size() == 0){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }

                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                String MR = "";
                if (!objectName.equals("MOS")){
                    MR = testframesAndMr.split(";")[2];
                }else {
                    for (int z = 2; z < testframesAndMr.split(";").length; z++) {
                        MR += testframesAndMr.split(";")[z] + ";";
                    }
                    MR = MR.substring(0, MR.length() - 1);
                    // delete the medel change in the MR
                    String[] choice = MR.split(";");
                    MR = choice[0] +";" + choice[1]+";" + choice[2]+";" + choice[3]+";"
                            + choice[5]+";" + choice[6]+";" + choice[7];
                }
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
                            Object stc = getTestCase(objectName,testframesAndMr.split(";")[0]);
                            Object ftc = getTestCase(objectName,testframesAndMr.split(";")[1]);
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
                                //检测出第一个故障，记录此时的数据
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;

                                }

                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;

                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
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
                            Object stc = getTestCase(objectName,testframesAndMr.split(";")[0]);
                            Object ftc = getTestCase(objectName,testframesAndMr.split(";")[1]);
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
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;

                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult){
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;

                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult){
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
                            Object stc = getTestCase(objectName,testframesAndMr.split(";")[0]);
                            Object ftc = getTestCase(objectName,testframesAndMr.split(";")[1]);
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
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;

                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will increase") && sourceResult >= followUpResult) {
                                if (killedMutants.size() == 0){
                                    Fmeasure = counter;

                                }
                                if (killedMutants.size() == Constant.getMutantsNumber(objectName) - 1){
                                    Tmeasure = counter;
                                }
                                killedMutants.add(entry.getKey());
                            }

                            if (MR.equals("The output will decrease") && sourceResult <= followUpResult) {
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
                            Object stc = getTestCase(objectName,testframesAndMr.split(";")[0]);
                            Object ftc = getTestCase(objectName,testframesAndMr.split(";")[1]);
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

                if (killedMutants.size() == Constant.getMutantsNumber(objectName)){
                    break;
                }
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

        RecordResult.recordResult("MT4" + objectName, FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));
        String excelName = "MT4" + objectName + "index_" + String.valueOf(in);
        WriteDataToExcel writeDataToExcel = new WriteDataToExcel();
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


    public static void main(String[] args) {
        MT mt = new MT();
//        String[] names = {"ACMS", "CUBS", "ERS", "MOS"};
        String[] names = {"ACMS"};
        for (int i = 0; i < 2000; i++) {
            for (String name : names){
                mt.executeTestCase(name,i);
            }
        }
    }
}
