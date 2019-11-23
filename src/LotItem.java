import net.jini.core.entry.*;
import java.util.ArrayList;
import java.util.Collections;

public class LotItem implements Entry{
    // Variables
    public Integer lotNumber;
    public Integer lotBuyNowValue;
    public String lotName;
    public String lotDescription;
    public String lotSeller;

    public ArrayList <Integer> lotBid;


    // No Arg Constructor
    public LotItem(){
    }

    // Arg Constructor
    public LotItem(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid){
        this.lotNumber = lot_no;
        this.lotName = lot_name;
        this.lotDescription = lot_description;
        this.lotSeller = lot_seller;
        this.lotBid = lot_bid;
        this.lotBuyNowValue = buyNowValue;
    }

    // Returns all values for LotItemClass
    public String printItem(){
        String item;
        item = "Lot Number: " + this.lotNumber + "\n" +
                " Name: " + this.lotName + "\n"+
                " Seller's Name: " + this.lotSeller + "\n" +
                " Description: " + this.lotDescription + "\n" +
                " Buy Now Value: £" + this.lotBuyNowValue + "\n" +
                " Current Bids: " + this.lotBid + "\n";
        return item;
    }

    // Adds Bid To Array List
    public void addBid(Integer value){
        this.lotBid.add(value);
    }

    // Return Bids Placed On Specific Item
    public ArrayList<Integer> returnBids(){
        return this.lotBid;
    }

    // Returns Highest Current Bid Value
    public Integer returnHighestBidValue(){
        return Collections.max(this.lotBid);
    }

    // Return Lot ID
    public Integer returnLotNo(){
        return this.lotNumber;
    }

    // Return Lot Name
    public String returnLotName(){
        return this.lotName;
    }

    // Return Buy Now Value
    public Integer returnBuyNowValue(){
        return this.lotBuyNowValue;
    }

}
