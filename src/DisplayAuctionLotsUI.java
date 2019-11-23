import net.jini.space.JavaSpace;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class DisplayAuctionLotsUI extends JFrame {

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds

    private JavaSpace space;
    private JTextArea auctionLotList;
    private JButton refresh;

    public DisplayAuctionLotsUI() {
        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }

        initComponents ();
        pack ();
        setVisible(true);
    }

    private void initComponents () {
        setTitle ("Auction Lot Interface");
        addWindowListener (new java.awt.event.WindowAdapter () {
            public void windowClosing (java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        }   );

        Container cp = getContentPane();
        cp.setLayout (new BorderLayout ());

        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout());

        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout());

        auctionLotList = new JTextArea(30,50);
        jPanel1.add(auctionLotList);

        JButton refresh = new JButton();
        refresh.setText("Refresh Lots");
        refresh.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                auctionLotList.setText("");
                refreshLot(e);
            }
        });
        jPanel2.add(refresh);
        cp.add(jPanel1,"Center");
        cp.add(jPanel2, "North");
    }

    private void refreshLot(ActionEvent evt){
        readLotFromSpace();
    }

    private LotIdIncrementor readLotIDFromSpace(){
        try{
            LotIdIncrementor lotIDTemplate = new LotIdIncrementor();
            LotIdIncrementor lotIDObject = (LotIdIncrementor) space.readIfExists(lotIDTemplate, null, TWO_SECONDS);
            // Checks To See Whether There Is An Initial ID Object In The Space
            if (lotIDObject == null) {
                System.out.println("No Lot ID In The JavaSpace. Please Run StartAuctionSpace");
            }
            return lotIDObject;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void readLotFromSpace(){
        LotIdIncrementor lotIDObject = readLotIDFromSpace();
        try {
            for (int i = 0; i < lotIDObject.lotID; i++) {
                LotItem lotItemTemplate = new LotItem();
                lotItemTemplate.lotNumber = i;
                LotItem lotItemObject = (LotItem) space.read(lotItemTemplate, null, 100);
                if (lotItemObject == null) {
                    System.out.println("Nothing In Space");
                }else {
                    int lotNumber = lotItemObject.lotNumber;
                    int lotBuyNowValue = lotItemObject.lotBuyNowValue;
                    String lotName = lotItemObject.lotName;
                    String lotDescription = lotItemObject.lotDescription;
                    String lotSellerName = lotItemObject.lotSeller;
                    ArrayList currentBids = lotItemObject.returnBids();
                    auctionLotList.append("-------------------------------------- \n" +
                            lotItemObject.printItem() +
                            "-------------------");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DisplayAuctionLotsUI();
    }
}
