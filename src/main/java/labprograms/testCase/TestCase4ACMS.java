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
public class TestCase4ACMS {
    private int airClass;
    private int area;
    private boolean isStudent;
    private double luggage;
    private double economicfee;

    public TestCase4ACMS(int airClass, int area, boolean isStudent, double luggage, double economicfee){
        setAirClass(airClass);
        setArea(area);
        setLuggage(luggage);
        setStudent(isStudent);
        setEconomicfee(economicfee);
    }
}
