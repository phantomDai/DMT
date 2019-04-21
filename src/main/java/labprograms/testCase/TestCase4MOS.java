package labprograms.testCase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * describe:
 * test case
 * @author phantom
 * @date 2019/04/18
 */
@Getter
@Setter
@ToString
public class TestCase4MOS {
    private String aircraftmodel;
    private String changeinthenumberofcrewmembers;
    private int newnumberofcrewmembers;
    private String changeinthenumberofpilots;
    private int newnumberofpilots;
    private int numberofchildpassengers;
    private int numberofrequestedbundlesofflowers;

    public TestCase4MOS(String aircraftmodel, String changeinthenumberofcrewmembers,
                        int newnumberofcrewmembers, String changeinthenumberofpilots,
                        int newnumberofpilots, int numberofchildpassengers,
                        int numberofrequestedbundlesofflowers){
        setAircraftmodel(aircraftmodel);
        setChangeinthenumberofcrewmembers(changeinthenumberofcrewmembers);
        setNewnumberofcrewmembers(newnumberofcrewmembers);
        setChangeinthenumberofpilots(changeinthenumberofpilots);
        setNewnumberofpilots(newnumberofpilots);
        setNumberofchildpassengers(numberofchildpassengers);
        setNumberofrequestedbundlesofflowers(numberofrequestedbundlesofflowers);
    }
}
