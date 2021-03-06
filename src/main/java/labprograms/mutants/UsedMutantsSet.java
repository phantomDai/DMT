package labprograms.mutants;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.separator;

/**
 * describe:
 *
 * @author phantom
 * @date 2019/05/06
 */
public class UsedMutantsSet {

    private static final String PARENT_DIR = "labprograms.";

    private String mutants_dir;

    private String dir;

    private Map<String, String> map = new HashMap<String, String>();

    @Setter
    private String objectName;

    @Getter
    @Setter
    private Map<String, Mutant> mutants;

    public UsedMutantsSet(String objectName) {
        setObjectName(objectName);
        mutants = new HashMap<>();
        mutants_dir = PARENT_DIR + this.objectName +  ".usedmutants.";
        dir = System.getProperty("user.dir") + separator + "src" + separator + "main" +
                separator + "java" + separator + "labprograms" + separator + objectName
                + separator + "usedmutants";

        putMap();
    }


    public Map<String, Mutant> getMutants(){
        File dir = new File(this.dir);
        String[] mutantNames = dir.list();
        for (String mutantName : mutantNames){
            String fullName = mutants_dir + mutantName + "."+  map.get(objectName).toString();
            Mutant mutant = new Mutant(fullName);
            mutants.put(mutantName, mutant);
        }
        return mutants;
    }

    private void putMap(){
        this.map.put("ACMS", "AirlinesBaggageBillingService");
        this.map.put("CUBS", "BillCalculation");
        this.map.put("ERS", "ExpenseReimbursementSystem");
        this.map.put("MOS", "MealOrderingSystem");
    }

}
