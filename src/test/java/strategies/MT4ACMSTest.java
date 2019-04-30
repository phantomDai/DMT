package strategies;

import labprograms.constant.Constant;
import labprograms.log.RecordLog;
import labprograms.method.Methods4Testing;
import labprograms.mutants.Mutant;
import labprograms.mutants.MutantsSet;
import labprograms.result.RecordResult;
import labprograms.testCase.TestCase4ACMS;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/04/29
 */
public class MT4ACMSTest {

    private Random random;

    private Random random4SelectTestCase;

    @Before
    public void initialize(){
        random = new Random(0);
    }


    @Test
    public void MTtestACMS(){
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


        //the name of method
        String methodName = new Methods4Testing("ACMS").getMethodName();

        // the number of repeating testing are 50
        for (int i = 0; i < Constant.repeatNumber; i++) {
            System.out.println("第" + String.valueOf(i + 1) + "次重复实验");

            random4SelectTestCase = new Random(i);
            MutantsSet mutantsSet = new MutantsSet("ACMS");
            Map<String, Mutant> mutants = mutantsSet.getMutants();

            Map<String, Mutant> mutantMap = new ConcurrentHashMap<String, Mutant>();

            for (Map.Entry<String, Mutant> entry : mutants.entrySet()){
                mutantMap.put(entry.getKey(),entry.getValue());
            }

            List<Integer> indexArray = new ArrayList<>();

            for (int j = 0; j < Constant.number; j++) {
                indexArray.add(random4SelectTestCase.nextInt(735) + 1);
            }


            // the F-measure
            int Fmeasure = 0;
            // the T-measure
            int Tmeasure = 0;

            //the counter
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

            for (int j = 0; j < Constant.number; j++) { //测试用例的上限为10000
                System.out.println("第" + String.valueOf(j + 1) + "个测试用例");

                //select a test case
                long startSelectTestCase = System.currentTimeMillis();
                //对应与ACMS文件的行号（行号是从1开始）
                int testframeAndMrIndex = indexArray.get(j);
                // the counter add 1
                counter++;

                String testframeAndMr = getspecifiedLineContent(testframeAndMrIndex);
                long endSelectTestCase = System.currentTimeMillis();
                if (mutantMap.size() == 2){
                    firstSelectingTime += (endSelectTestCase - startSelectTestCase);
                }
                allSelectingTime += (endSelectTestCase - startSelectTestCase);

                //get source test frame, follow-up test frame, and corresponding MR
                String sourceTestFrame = testframeAndMr.split(";")[0];
                String followUpTestFrame = testframeAndMr.split(";")[1];
                String MR = testframeAndMr.split(";")[2];

                //generate source test case and follow-up test case
                long startGenerateTestCase = System.currentTimeMillis();
                TestCase4ACMS sourceTestCase = generateTestCase(sourceTestFrame);

                System.out.println("原始测试用例：" + sourceTestCase.toString());
                TestCase4ACMS followUpTestCase = generateTestCase(followUpTestFrame);
                System.out.println("衍生测试用例：" + followUpTestCase.toString());
                long endGenerateTestCase = System.currentTimeMillis();
                if (mutantMap.size() == 2){
                    firstGeneratingTime += (endGenerateTestCase - startGenerateTestCase);
                }
                allGeneratingTime += (endGenerateTestCase - startGenerateTestCase);

                //execute source test case and follow-up test case on all mutants
                long startExecuteTestCase = System.currentTimeMillis();
                for (Map.Entry<String, Mutant> entry : mutantMap.entrySet()){
                    Mutant mutant = null;
                    mutant = entry.getValue();
                    //initial mutant
                    Object mutantInstance = null;
                    Method mutantMethod = null;
                    double sourceResult = 0;
                    double followUpResult = 0;
                    try {
                        Class mutantClazz = Class.forName(mutant.getFullName());
                        Constructor mutantConstructor = mutantClazz.getConstructor();
                        mutantInstance = mutantConstructor.newInstance();
                        mutantMethod = mutantClazz.getMethod(methodName, int.class, int.class,
                                boolean.class, double.class, double.class);

                        //get the results of source test case and follow-up test case
                        sourceResult = (double) mutantMethod.invoke(mutantInstance,
                                sourceTestCase.getAirClass(),sourceTestCase.getArea(),
                                sourceTestCase.isStudent(),sourceTestCase.getLuggage(),
                                sourceTestCase.getEconomicfee());

                        followUpResult = (double) mutantMethod.invoke(mutantInstance,
                                followUpTestCase.getAirClass(),followUpTestCase.getArea(),
                                followUpTestCase.isStudent(),followUpTestCase.getLuggage(),
                                followUpTestCase.getEconomicfee());
                        long endExecuteTestCase = System.currentTimeMillis();
                        if (mutantMap.size() == 2){
                            firstExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                        }
                        allExecutingTime += (endExecuteTestCase - startExecuteTestCase);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    if (MR.equals("The output will not change") && sourceResult != followUpResult){
                        if (mutantMap.size() == 2){
                            Fmeasure = counter;
                        }

                        if (mutantMap.size() == 1){
                            Tmeasure = counter;
                        }
                        RecordLog.recordLog("ACMS", i,j, sourceResult,followUpResult,MR,
                                sourceTestCase,followUpTestCase,entry.getKey());
                        mutantMap.remove(entry.getKey());
                    }
                    if (MR.equals("The output will increase") && sourceResult <= followUpResult){
                        if (mutantMap.size() == 2){
                            Fmeasure = counter;
                        }

                        if (mutantMap.size() == 1){
                            Tmeasure = counter;
                        }

                        RecordLog.recordLog("ACMS", i, j, sourceResult, followUpResult,MR,
                                sourceTestCase,followUpTestCase,entry.getKey());
                        mutantMap.remove(entry.getKey());
                    }
                }
                if (mutantMap.size() == 0){
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

        RecordResult.recordResult("ACMS", FmeasureArray, TmeasureArray,
                firstSelectTestCaseArray, firstgenerateTestCaseArray,firstExecuteTestCaseArray,
                allSelectTestCaseArray,allgenerateTestCaseArray,allExecuteTestCaseArray,
                getAveragemeasure(FmeasureArray), getAveragemeasure(TmeasureArray),
                getAverageTime(firstSelectTestCaseArray), getAverageTime(firstgenerateTestCaseArray),
                getAverageTime(firstExecuteTestCaseArray),getAverageTime(allSelectTestCaseArray),
                getAverageTime(allgenerateTestCaseArray), getAverageTime(allExecuteTestCaseArray));

    }

    /**
     * get the content of specified line
     * @param index the line number
     * @return the content of the line
     */
    private String getspecifiedLineContent(int index){
        File file = new File(Constant.mrPath4ACMS);
        String result = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int count = 0;
            while((result = bufferedReader.readLine()) != null){
                count++;
                if (count == index){
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * generate concrete test cases based on the test frame
     * @param testFrame that is used to generate test cases
     * @return a test case
     */
    private TestCase4ACMS generateTestCase(String testFrame){
        int airClass = 0;
        int area = 0;
        boolean isStudent = true;
        double luggage = 0;
        double economicfee = 0;
        //delate the braces
        testFrame = testFrame.replace("{","").replace("}","");
        String[] choices = testFrame.split(",");
        // instantiate the choices
        switch (choices[0]){
            case "I-1a":
                airClass = 0;
                break;
            case "I-1b":
                airClass = 1;
                break;
            case "I-1c":
                airClass = 2;
                break;
            case "I-1d":
                airClass = 3;
                break;
        }

        switch (choices[1]){
            case "I-2a":
                area = 0;
                break;
            case "I-2b":
                area = 1;
                break;
        }

        switch (choices[2]){
            case "I-3a":
                isStudent = false;
                break;
            case "I-3b":
                isStudent = true;
                airClass = 2;
                break;
        }
        int benchmark = 0;
        if (airClass == 0)
            benchmark = 40;
        else if (airClass == 1)
            benchmark = 30;
        else if (airClass ==2 && isStudent == true)
            benchmark = 30;
        else if (airClass ==2 && isStudent == false)
            benchmark = 20;
        else
            benchmark = 0;

        switch (choices[3]){
            case "I-4a":
                int temp = new Random().nextInt(80);
                while(temp > benchmark){
                    temp = new Random().nextInt(80);
                }
                luggage = temp;
                break;
            case "I-4b":
                int tempb = new Random().nextInt(80);
                while(tempb <= benchmark){
                    tempb = new Random().nextInt(80);
                }
                luggage = tempb;
                break;
        }

        switch (choices[4]){
            case "I-5a":
                economicfee = 0;
                break;
            case "I-5b":
                economicfee = new Random().nextDouble() * 3000;
                break;
        }
        TestCase4ACMS tc = new TestCase4ACMS(airClass,area,isStudent,luggage,economicfee);
        return tc;
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
