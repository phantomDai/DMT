//package labprograms.gethardkilledmutants;
//
//import com.alibaba.fastjson.JSONObject;
//import labprograms.testCase.TestCase4ERS;
//import labprograms.testCase.TestSuite4ACMS;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//import static java.io.File.separator;
//
///**
// * describe:
// * randomly generate test cases to execute mutants, which can get the mutants
// * that at least need 20 test case to be killed
// * @author phantom
// * @date 2019/04/17
// */
//public class RT4ERS {
//    private static final String[] STAFFLEVEL = {"seniormanager", "manager", "supervisor"};
//
//    public Map<String, Object> createTestCases(int numOfTestCases){
//        TestSuite4ACMS testSuite4ACMS = new TestSuite4ACMS();
//        Random random = new Random(0);
//        for (int i = 0; i < numOfTestCases; i++) {
//            String staffLevel = STAFFLEVEL[random.nextInt(3)];
//            double actualmonthlymileage = random.nextDouble() * 30000;
//            double monthlysalesamount = random.nextDouble() * 200000;
//            double airfareamount = random.nextDouble() * 20000;
//            double otherexpensesamount = random.nextDouble() * 50000;
//            TestCase4ERS tc = new TestCase4ERS(staffLevel,actualmonthlymileage,monthlysalesamount,
//                    airfareamount,otherexpensesamount);
//        }
//        return testSuite4ACMS.getTestSuite();
//    }
//
//}
