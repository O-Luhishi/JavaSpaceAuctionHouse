package UI.Admin;

import JTableModels.SellerTableModel;
import JavaSpaceConfig.SpaceUtils;
import LotSpace.LotIdIncrementor;
import LotSpace.LotItem;
import UI.MenuUI;
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

public class MyLotsUI extends JFrame implements RemoteEventListener {

    private JavaSpace space;

	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    
    private String userName;
    private Integer lotNumber;
    private Integer lotFinalPriceValue;

    private JPanel firstPanel;

	private RemoteEventListener theStub;
	private Exporter myDefaultExporter;
    
	List<LotItem> item = new ArrayList<>(25);
	SellerTableModel model = new SellerTableModel(item);
	private JTable table;
	private JScrollPane scrollPane;

	private JButton btnAcceptBid, btnRemoveLot;

	private Boolean x = true;



    public MyLotsUI(String username){
        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        this.userName = username;
        setTitle("Sellers Page - Logged In As: " + username);
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

        JLabel lblTitle = new JLabel("My Auction Lots");
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
		
		btnAcceptBid = new JButton("Accept Bid");
		btnAcceptBid.setBounds(275, 324, 117, 29);
		btnAcceptBid.setEnabled(false);
		btnAcceptBid.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptHighestBid();
			}
		});
		firstPanel.add(btnAcceptBid);
		
		btnRemoveLot = new JButton("Remove Lot");
		btnRemoveLot.setBounds(462, 324, 117, 29);
		btnRemoveLot.setEnabled(false);
		btnRemoveLot.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteLot();
			}
		});
		firstPanel.add(btnRemoveLot);

		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				getValuesFromJTable(e);
			}
		});

		this.setVisible(true);
    }

    // Returns user to home screen
	private void returnHome(ActionEvent evt){
		dispose();
		MenuUI mainFrame = new MenuUI(this.userName);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(350,450);
	}

	// Presents a popup window to alert the seller that they accepted the highest bid
	private void successfulAcceptBidMessage(String username, Integer bid_Value, String item_Name){
		JOptionPane.showMessageDialog(null, "Congratulations " + username +"! You accepted the highest bid! \n" +
				"Item Name: " + item_Name + "\n" + "Bid Value: £"+ bid_Value, "Accepted Offer", JOptionPane.INFORMATION_MESSAGE);

	}

	// Presents a popup window to alert the seller that they successfully removed the item from the auction lot
	private void successfulRemoveLotMessage(String item_Name){
		JOptionPane.showMessageDialog(null, "You Successfully Removed " + item_Name, "Removed Lot", JOptionPane.INFORMATION_MESSAGE);
	}

	// Accept the highest bid on a item and turns Sold flag to true
	private void acceptHighestBid(){
		try{
			Integer lotID = lotNumber;
			LotItem lotItemTemplate = new LotItem();
			lotItemTemplate.lotNumber = lotID;
			LotItem lotItemObject = (LotItem) space.takeIfExists(lotItemTemplate, null, TWO_SECONDS);
			if (lotItemObject == null) {
				System.out.print("No Items Found In The Space");
			}else{
				lotItemObject.sold = true;
				lotItemObject.lotFinalPrice = lotFinalPriceValue;
				space.write(lotItemObject, null , Lease.FOREVER);
				successfulAcceptBidMessage(userName, lotItemObject.returnHighestBidValue(), lotItemObject.returnLotName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// Removes the item from the auction lot by taking it out of the space permanently
	private void deleteLot(){
		try {
			Integer lotID = lotNumber;
			LotItem lotItemTemplate = new LotItem();
			lotItemTemplate.lotNumber = lotID;
			LotItem lotItemObject = (LotItem) space.takeIfExists(lotItemTemplate, null, TWO_SECONDS);
			if (lotItemObject == null) {
				System.out.print("No Items Found In The Space");
			}else {
				successfulRemoveLotMessage(lotItemObject.lotName);
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
				System.out.println("No Lot ID In The JavaSpace. Please Run JavaSpaceConfig.StartAuctionSpace");
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
				System.out.println("No Lot ID In The JavaSpace. Please Run JavaSpaceConfig.StartAuctionSpace");
			}else {
				for (int i = 0; i < lotIDObject.lotID; i++) {
					LotItem lotItemTemplate = new LotItem();
					lotItemTemplate.lotNumber = i;
					lotItemTemplate.lotSeller = this.userName;
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
						ArrayList<Integer> starting_bid_price = new ArrayList<>();
						starting_bid_price.add(lotItemObject.returnHighestBidValue());
						String lotBuyer = null;
						Integer lotFinalPrice = 0;
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
		model = new SellerTableModel(item);
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

	// Gets the values from the JTable model class
	private void getValuesFromJTable(MouseEvent e){
		if (e.getClickCount() == 1){
			JTable selected = (JTable)e.getSource();
			int index = selected.getSelectedRow();
			Boolean soldStatus = (Boolean) model.getValueAt(index, 4);
			lotNumber = (Integer) model.getValueAt(index, 0);
			lotFinalPriceValue = (Integer) model.getValueAt(index, 3);
			btnRemoveLot.setEnabled(true);
			if (!soldStatus){
				btnAcceptBid.setEnabled(true);
				btnRemoveLot.setEnabled(true);
			}else{
				btnAcceptBid.setEnabled(false);
				btnRemoveLot.setEnabled(false);
			}
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
