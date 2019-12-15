package LotSpace;

import net.jini.core.entry.*;

public class LotIdIncrementor implements Entry{
    // Variables
    public Integer lotID;

    // No arg contructor
    public LotIdIncrementor(){
    }

    public LotIdIncrementor(int n){
        // set count to n
        lotID = n;
    }

    public void incrementLotID(){
        lotID++;
    }
}
