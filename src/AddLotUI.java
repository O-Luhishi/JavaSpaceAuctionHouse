import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.awt.Color;
import javax.swing.*;
import java.awt.Font;
import java.util.ArrayList;

public class AddLotUI extends JFrame {

	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 2 * 1000 * 60;

	private JavaSpace space;
	private JTextArea auctionLotList;

	private JPanel firstPanel;
	private JTextField lotNameIn;
	private JTextField lotSellerIn;
	private JTextField lotBuyNowValueIn;
	private JTextField lotStartingBidPrice;
	private JTextField lotDescriptionIn;

	/**
	 * Create the frame.
	 */
	public AddLotUI() {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		setTitle("Add Lot");
		initComponents();
		setVisible(true);

	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 350);

		firstPanel = new JPanel();
		firstPanel.setVisible(true);
		firstPanel.setSize(540, 504);
		firstPanel.setBackground(Color.GRAY);
		getContentPane().add(firstPanel);
		firstPanel.setLayout(null);
		
		JLabel lotNameLabel = new JLabel("Lot Name:");
		lotNameLabel.setForeground(Color.WHITE);
		lotNameLabel.setBounds(31, 51, 80, 16);
		firstPanel.add(lotNameLabel);
		
		lotNameIn = new JTextField();
		lotNameIn.setBounds(204, 46, 176, 26);
		firstPanel.add(lotNameIn);
		lotNameIn.setColumns(10);
		
		JLabel lblTitle = new JLabel("Sell Your Item");
		lblTitle.setFont(new Font("Helvetica", Font.ITALIC, 15));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(135, 6, 161, 28);
		firstPanel.add(lblTitle);
		
		JLabel lotSellerNameLabel = new JLabel("Sellers Name:");
		lotSellerNameLabel.setForeground(Color.WHITE);
		lotSellerNameLabel.setBounds(31, 89, 102, 16);
		firstPanel.add(lotSellerNameLabel);
		
		lotSellerIn = new JTextField();
		lotSellerIn.setColumns(10);
		lotSellerIn.setBounds(204, 84, 176, 26);
		firstPanel.add(lotSellerIn);
		
		JLabel lotlBuyNowValueLabel = new JLabel("Buy Now Price:");
		lotlBuyNowValueLabel.setForeground(Color.WHITE);
		lotlBuyNowValueLabel.setBounds(31, 130, 102, 16);
		firstPanel.add(lotlBuyNowValueLabel);
		
		lotBuyNowValueIn = new JTextField();
		lotBuyNowValueIn.setColumns(10);
		lotBuyNowValueIn.setBounds(204, 125, 176, 26);
		firstPanel.add(lotBuyNowValueIn);
		
		JLabel lblStartingBidValue = new JLabel("Starting Bid Value:");
		lblStartingBidValue.setForeground(Color.WHITE);
		lblStartingBidValue.setBounds(31, 163, 120, 16);
		firstPanel.add(lblStartingBidValue);
		
		lotStartingBidPrice = new JTextField();
		lotStartingBidPrice.setColumns(10);
		lotStartingBidPrice.setBounds(204, 158, 176, 26);
		firstPanel.add(lotStartingBidPrice);
		
		JLabel lotDescriptionLabel = new JLabel("Description");
		lotDescriptionLabel.setForeground(Color.WHITE);
		lotDescriptionLabel.setBounds(31, 222, 102, 16);
		firstPanel.add(lotDescriptionLabel);
		
		lotDescriptionIn = new JTextField();
		lotDescriptionIn.setColumns(10);
		lotDescriptionIn.setBounds(204, 196, 176, 76);
		firstPanel.add(lotDescriptionIn);
		
		JButton returnHomeScreenButton = new JButton("Home");
		returnHomeScreenButton.setBounds(34, 282, 117, 29);
		firstPanel.add(returnHomeScreenButton);
		
		JButton addLotButton = new JButton("Add Lot To Auction");
		addLotButton.setBounds(219, 282, 161, 29);
		addLotButton.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				addLotToSpace();
			}
		}  );
		firstPanel.add(addLotButton);
	}

	private void successfulLotItemPlaced(int lotID, String sellerName, String itemName){
		JOptionPane.showMessageDialog(null, "Congratulations " + sellerName+ "! You Placed The Following Item For Sale: \n" +
				"Lot ID: " + lotID + "\n" + "Item Name: "+ itemName, "Selling Complete", JOptionPane.INFORMATION_MESSAGE);
		displayAuctionLotUI();
	}

	private void displayAuctionLotUI(){
		lotNameIn.setText("");
		lotSellerIn.setText("");
		lotBuyNowValueIn.setText("");
		lotStartingBidPrice.setText("");
		lotDescriptionIn.setText("");
	}

	private LotIdIncrementor readLotIDFromSpace(){
		try{
			LotIdIncrementor lotIDTemplate = new LotIdIncrementor();
			LotIdIncrementor lotIDObject = (LotIdIncrementor) space.readIfExists(lotIDTemplate, null, 100);
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

	private void addLotToSpace(){
		try {
			LotIdIncrementor lotIdTemplate = readLotIDFromSpace();
			LotIdIncrementor lotIdObject = (LotIdIncrementor)space.take(lotIdTemplate,null, 100);

			// create the new LotItem, write it to the space, and update the GUI
			int lotNumber = lotIdObject.lotID;
			String lotName = lotNameIn.getText();
			String lotDescription = lotDescriptionIn.getText();
			String lotSeller = lotSellerIn.getText();
			String strLotBuyNowValue = lotBuyNowValueIn.getText();
			String strStartingBidPrice = lotStartingBidPrice.getText();
			Integer lotBuyNowValue = Integer.valueOf(strLotBuyNowValue);
			Integer lotStartingBidPrice = Integer.valueOf(strStartingBidPrice);
			ArrayList<Integer> starting_bid_price = new ArrayList<>();
			starting_bid_price.add(lotStartingBidPrice);
			LotItem newJob = new LotItem(lotNumber, lotBuyNowValue, lotName, lotDescription, lotSeller, starting_bid_price);
			// update the LotIdIncrementor object by incrementing the counter and write it back to the space
			lotIdObject.incrementLotID();
			space.write( lotIdObject, null, Lease.FOREVER);
			space.write( newJob, null, Lease.FOREVER);
			// Output Dialog For Lot ID Number
			successfulLotItemPlaced(lotNumber, lotSeller, lotName);
		}  catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		AddLotUI mainFrame = new AddLotUI();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(450,350);
	}
}
