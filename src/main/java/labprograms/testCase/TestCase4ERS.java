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
@Getter
@Setter
@ToString
public class TestCase4ERS {
    private String stafflevel;
    private double actualmonthlymileage;
    private double monthlysalesamount;
    private double airfareamount;
    private double otherexpensesamount;

    public TestCase4ERS(String stafflevel, double actualmonthlymileage, double monthlysalesamount,
                        double airfareamount, double otherexpensesamount){
        setStafflevel(stafflevel);
        setActualmonthlymileage(actualmonthlymileage);
        setMonthlysalesamount(monthlysalesamount);
        setAirfareamount(airfareamount);
        setOtherexpensesamount(otherexpensesamount);
    }
}
