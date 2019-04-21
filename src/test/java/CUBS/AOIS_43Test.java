package CUBS;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import labprograms.util.WriteTestingResult;
import labprograms.testCase.TestCase4ACMS;
import labprograms.testCase.TestCase4CUBS;
import labprograms.testCase.TestCase4ERS;
import labprograms.testCase.TestCase4MOS;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import labprograms.constant.Constant;
public class AOIS_43Test extends TestCase{
	private List<TestCase4CUBS> testcases;
	labprograms.CUBS.sourceCode.BillCalculation source = new labprograms.CUBS.sourceCode.BillCalculation();
	WriteTestingResult writeTestingResult = new WriteTestingResult();
	@Test
	public void testAOIS_43(){
		String mutantName = "AOIS_43";
		testcases = new ArrayList<>();
		createTestCases();
		int count = 0;
		for (TestCase4CUBS tc : testcases) {
			double sourceResult = source.phoneBillCalculation(tc.getPlanType(), tc.getPlanFee(), tc.getTalkTime(), tc.getFlow());
			labprograms.CUBS.mutants.AOIS_43.BillCalculation mutant = new labprograms.CUBS.mutants.AOIS_43.BillCalculation();
			double mutantResult = mutant.phoneBillCalculation(tc.getPlanType(), tc.getPlanFee(), tc.getTalkTime(), tc.getFlow());
			if (sourceResult == mutantResult){
				continue;
			}else {
				count++;
			}
		}
		writeTestingResult.write("CUBS", mutantName," ",String.valueOf(count));
	}
	private void createTestCases(){
		Constant constant = new Constant();
		Random random = new Random(0);
		String[] types = {"A", "B", "a", "b"};
		int[] Achoices = {46, 96, 286, 886};
		int[] Bchoices = {46, 96, 126, 186};
		for (int i = 0; i < constant.number; i++) {
			String planType = types[random.nextInt(4)];
			int planFee = 0;
			if (planType == "A" || planType == "a"){
				planFee = Achoices[random.nextInt(4)];
			}else {
				planFee = Bchoices[random.nextInt(4)];
			}
			int talkTime = random.nextInt(4000);
			int flow = random.nextInt(4000);
			TestCase4CUBS tc = new TestCase4CUBS(planType,planFee,talkTime,flow);
			testcases.add(tc);
		}
	}
}