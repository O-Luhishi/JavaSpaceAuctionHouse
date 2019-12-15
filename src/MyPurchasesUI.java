import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MyPurchasesUI extends JFrame implements RemoteEventListener {
    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds

    private String userName;
    private Integer lotNumber;

    private JPanel firstPanel;

    private RemoteEventListener theStub;
    private Exporter myDefaultExporter;

    List<LotItem> item = new ArrayList<>(25);
    BuyerTableModel model = new BuyerTableModel(item);
    private JTable table;
    private JScrollPane scrollPane;

    private JButton btnViewItemDetails;

    private Boolean x = true;



    public MyPurchasesUI(String username){
        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        this.userName = username;
        setTitle("Purchase History - Logged In As: " + username);
        initComponents();
        setVisible(true);
        myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        getLotItems();
        readLotFromSpace();
    }

    private void initComponents(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 645, 392);

        firstPanel = new JPanel();
        firstPanel.setVisible(true);
        firstPanel.setSize(540, 504);
        firstPanel.setBackground(Color.GRAY);
        getContentPane().add(firstPanel);
        firstPanel.setLayout(null);

        JLabel lblTitle = new JLabel("My Purchases");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        lblTitle.setBounds(212, 6, 222, 58);
        firstPanel.add(lblTitle);

        scrollPane = new JScrollPane();
        firstPanel.add(scrollPane);
        scrollPane.setBounds(6, 76, 633, 222);

        table = new JTable();
        scrollPane.setViewportView(table);
        table.setModel(model);
        table.setBounds(6, 114, 235, 184);
        model.alignTable(table);

        JButton btnHome = new JButton("Home");
        btnHome.setBounds(61, 324, 117, 29);
        btnHome.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                returnHome(e);
            }
        });
        firstPanel.add(btnHome);

        btnViewItemDetails = new JButton("Details");
        btnViewItemDetails.setBounds(275, 324, 117, 29);
        btnViewItemDetails.setEnabled(false);
        btnViewItemDetails.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                viewItemDetails();
            }
        });
        firstPanel.add(btnViewItemDetails);

        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                getValuesFromJTable(e);
            }
        });

        this.setVisible(true);
    }

    private void returnHome(ActionEvent evt){
        dispose();
        MenuUI mainFrame = new MenuUI(this.userName);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(350,450);
    }


    private void displayItemDetailsWindow(String itemDetails){
            JOptionPane.showMessageDialog(null, itemDetails, "Item Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewItemDetails(){
        try{
            LotItem lotItemTemplate = new LotItem();
            lotItemTemplate.lotNumber = lotNumber;
            LotItem lotItemObject = (LotItem) space.readIfExists(lotItemTemplate, null, 100);
            if (lotItemObject == null) {
            }else {
                displayItemDetailsWindow(lotItemObject.printBuyerItemDetails());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
        try {
            LotIdIncrementor lotIDObject = readLotIDFromSpace();
            // Checks To See Whether There Is An Initial ID Object In The Space
            if (lotIDObject == null) {
                System.out.println("No Lot ID In The JavaSpace. Please Run StartAuctionSpace");
            }else {
                for (int i = 0; i < lotIDObject.lotID; i++) {
                    LotItem lotItemTemplate = new LotItem();
                    lotItemTemplate.lotNumber = i;
                    lotItemTemplate.lotBuyer = this.userName;
                    lotItemTemplate.sold = true;
                    LotItem lotItemObject = (LotItem) space.read(lotItemTemplate, null, 100);
                    if (lotItemObject == null) {
                        System.out.println("Nothing In Space");
                    } else {
                        int lotNumber = lotItemObject.lotNumber;
                        int lotBuyNowValue = lotItemObject.lotBuyNowValue;
                        String lotName = lotItemObject.lotName;
                        boolean sold = lotItemObject.sold;
                        String lotDescription = lotItemObject.lotDescription;
                        String lotSeller = lotItemObject.lotSeller;
                        String lotBuyer = lotItemObject.lotBuyer;
                        Integer lotFinalPrice = lotItemObject.lotFinalPrice;
                        ArrayList<Integer> starting_bid_price = new ArrayList<>();
                        starting_bid_price.add(lotItemObject.returnHighestBidValue());
                        model.addNewLot(lotNumber, lotBuyNowValue, lotName, lotDescription, lotSeller, starting_bid_price, sold, lotBuyer, lotFinalPrice);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void notify(RemoteEvent remoteEvent){
        item = new ArrayList<>(25);
        model = new BuyerTableModel(item);
        table = new JTable();
        scrollPane.setViewportView(table);
        table.setModel(model);
        table.setBounds(6, 114, 235, 184);
        model.alignTable(table);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                getValuesFromJTable(e);
            }
        });
        System.out.println("Running notify");
        readLotFromSpace();
    }

    private void getValuesFromJTable(MouseEvent e){
        if (e.getClickCount() == 1){
            JTable selected = (JTable)e.getSource();
            int index = selected.getSelectedRow();
            lotNumber = (Integer) model.getValueAt(index, 0);
            btnViewItemDetails.setEnabled(true);
        }
    }

    private void getLotItems(){
        try {
            // register this as a remote object and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);
            // add the listener
            LotItem template = new LotItem();
            space.notify(template, null, this.theStub, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
