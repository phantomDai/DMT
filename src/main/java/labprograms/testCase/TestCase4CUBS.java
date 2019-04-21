package labprograms.testCase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * describe:
 * test case
 * @author phantom
 * @date 2019/04/17
 */
@Setter
@Getter
@ToString
public class TestCase4CUBS {
    private String planType;
    private int planFee;
    private int talkTime;
    private int flow;

    public TestCase4CUBS(String planType, int planFee, int talkTime, int flow){
        setPlanType(planType);
        setPlanFee(planFee);
        setTalkTime(talkTime);
        setFlow(flow);
    }
}
