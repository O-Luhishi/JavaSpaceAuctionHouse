import net.jini.core.lease.Lease;
import net.jini.space.*;

import java.awt.*;
import javax.swing.*;


public class PurchasingUI extends JFrame {

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private JavaSpace space;
    private JTextField lotIDIn, displayHighestLotBidAmount, displayLotBuyNowValue, displayUserAmountValue;
    private JButton buyNowButton, addBidButton;

    private String username;


    public PurchasingUI(Integer lotID, String userName) {
        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        this.username = userName;
        initComponents();
        outputLotDetails(lotID);
        pack();
    }

    private void initComponents() {
        setTitle("Purchase An ITem");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dispose();
            }
        });

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout());
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout());
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout());

        JLabel lotIDLabel = new JLabel();
        lotIDLabel.setText("ID Of Lot: ");
        jPanel1.add(lotIDLabel);

        lotIDIn = new JTextField(12);
        lotIDIn.setEditable(false);
        lotIDIn.setText("");
        jPanel1.add(lotIDIn);

        JLabel displayHighestLotBidLabel = new JLabel();
        displayHighestLotBidLabel.setText("Current Highest Bid: ");
        jPanel2.add(displayHighestLotBidLabel);

        displayHighestLotBidAmount = new JTextField(12);
        displayHighestLotBidAmount.setText("");
        displayHighestLotBidAmount.setEditable(false);
        jPanel2.add(displayHighestLotBidAmount);

        JLabel displayLotBuyNowValueLabel = new JLabel();
        displayLotBuyNowValueLabel.setText("Buy Now Value: ");
        jPanel2.add(displayLotBuyNowValueLabel);

        displayLotBuyNowValue = new JTextField(12);
        displayLotBuyNowValue.setText("");
        displayLotBuyNowValue.setEditable(false);
        jPanel2.add(displayLotBuyNowValue);


        JLabel userAmountLabel = new JLabel();
        userAmountLabel.setText("Enter Amount: ");
        jPanel3.add(userAmountLabel);

        displayUserAmountValue = new JTextField(12);
        displayUserAmountValue.setText("");
        displayUserAmountValue.setEnabled(false);
        jPanel3.add(displayUserAmountValue);

        buyNowButton = new JButton();
        buyNowButton.setText("Buy Item Now");
        buyNowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyItemNow();
            }
        });
        buyNowButton.setEnabled(false);

        addBidButton = new JButton();
        addBidButton.setText("Bid On Item");
        addBidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBid();
    }
});
        addBidButton.setEnabled(false);

        jPanel3.add(addBidButton);
        jPanel3.add(buyNowButton);

        cp.add(jPanel1, "North");
        cp.add(jPanel2, "Center");
        cp.add(jPanel3, "South");
    }

    private void outputNoLotFound(Integer lotid){
        JOptionPane.showMessageDialog(null, "No Lot Found For ID Number: " + lotid, "No Lot Found", JOptionPane.ERROR_MESSAGE);
    }

    private void outputBidErrorMessage(){
        JOptionPane.showMessageDialog(null, "Error Bid Value Must Be Higher Than Current Value", "Bidding Error", JOptionPane.ERROR_MESSAGE);
    }

    private void successfulBidMessage(Integer bid_Value, String item_Name){
        JOptionPane.showMessageDialog(null, "Congratulation you are the highest bidder! \n" +
                "Item Name: " + item_Name + "\n" + "Bid Value: £"+ bid_Value, "Bid Complete", JOptionPane.INFORMATION_MESSAGE);

    }

    private void successfulBuyMessage(Integer buyNowValue, String itemName){
        JOptionPane.showMessageDialog(null, "Congratulations! you purchased the following Item: \n" +
                "Item Name: " + itemName + "\n" + "Price: £"+ buyNowValue, "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);

    }

    private void outputLotDetails(Integer lotID) {
        try {
            LotItem lotItemTemplate = new LotItem();
            lotItemTemplate.lotNumber = lotID;
            LotItem lotItemObject = (LotItem) space.readIfExists(lotItemTemplate, null, TWO_MINUTES);

            if (lotItemObject == null) {
                System.out.print("No Items Found In The Space");
                outputNoLotFound(lotID);
            }else {
                String strLotId = String.valueOf(lotID);
                lotIDIn.setText(strLotId);
                // Convert Integer To String For Current Highest Bid & Buy Now Value
                String strHighestLotBid = String.valueOf(lotItemObject.returnHighestBidValue());
                String strBuyNowValue = String.valueOf(lotItemObject.returnBuyNowValue());
                displayHighestLotBidAmount.setText(strHighestLotBid);
                displayLotBuyNowValue.setText(strBuyNowValue);

                displayUserAmountValue.setEnabled(true);
                buyNowButton.setEnabled(true);
                addBidButton.setEnabled(true);
            }
        }catch(Exception e){
                e.printStackTrace();
        }
    }

    private void buyItemNow(){
        try{
            String strLotID = lotIDIn.getText();
            // Convert From String To Integer
            Integer lotID = Integer.valueOf(strLotID);
            LotItem lotItemTemplate = new LotItem();
            lotItemTemplate.lotNumber = lotID;
            LotItem lotItemObject = (LotItem) space.takeIfExists(lotItemTemplate, null, TWO_MINUTES);
            if (lotItemObject == null) {
                System.out.print("No Items Found In The Space");
                outputNoLotFound(lotID);
            }else{
                lotItemObject.sold = true;
                lotItemObject.lotBuyer = this.username;
                lotItemObject.lotFinalPrice = Integer.valueOf(displayLotBuyNowValue.getText());
                space.write(lotItemObject, null , Lease.FOREVER);
                successfulBuyMessage(lotItemObject.returnBuyNowValue(), lotItemObject.returnLotName());
                dispose();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addBid(){
        try {
            // Get Values From UI
            String strLotID = lotIDIn.getText();
            String strBidAmount = displayUserAmountValue.getText();

            // Convert From String To Integer
            Integer lotID = Integer.valueOf(strLotID);
            Integer bidValue = Integer.valueOf(strBidAmount);

            LotItem lotItemTemplate = new LotItem();
            lotItemTemplate.lotNumber = lotID;
            lotItemTemplate.sold = false;
            LotItem lotItemObject = (LotItem) space.takeIfExists(lotItemTemplate, null, TWO_MINUTES);

            if (lotItemObject == null){
                outputNoLotFound(lotID);
                System.out.print("No Items Found In The Space");
            }

            if (bidValue <= lotItemObject.returnHighestBidValue()){
                outputBidErrorMessage();
            }else {
                String itemName = lotItemObject.returnLotName();
                lotItemObject.addBid(bidValue);
                lotItemObject.lotFinalPrice = bidValue;
                lotItemObject.lotBuyer = this.username;
                successfulBidMessage(bidValue, itemName);
            }
            space.write(lotItemObject, null, Lease.FOREVER);
            dispose();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
