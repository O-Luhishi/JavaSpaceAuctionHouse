import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AuctionLotsUI extends JFrame implements RemoteEventListener{

	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 2 * 1000 * 60;

	private String userName;

	private Integer lotNumber;

	private JavaSpace space;

	private JPanel firstPanel;

	List<LotItem> item = new ArrayList<>(25);
	TableModel model = new TableModel(item);
	private JTable table;
	private JScrollPane scrollPane;

	private RemoteEventListener theStub;
	private Exporter myDefaultExporter;

	private JTextArea lotItemDisplayList;

	private JButton addBidButton;


	/**
	 * Create the frame.
	 */
	public AuctionLotsUI(String username) {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		this.userName = username;
		setTitle("Listed Lots");
		initComponents();
		setVisible(true);
		myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
		getLotItems();
		readLotFromSpace();
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 450);

		firstPanel = new JPanel();
		firstPanel.setVisible(true);
		firstPanel.setSize(540, 504);
		firstPanel.setBackground(Color.GRAY);
		getContentPane().add(firstPanel);
		firstPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Auction Lots");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setBounds(212, 6, 129, 58);
		firstPanel.add(lblNewLabel);
		
		lotItemDisplayList = new JTextArea();
		lotItemDisplayList.setLineWrap(true);
		lotItemDisplayList.setEditable(false);
		lotItemDisplayList.setForeground(Color.BLACK);
		lotItemDisplayList.setBackground(Color.LIGHT_GRAY);
		lotItemDisplayList.setBounds(269, 75, 275, 347);
		firstPanel.add(lotItemDisplayList);
		
		addBidButton = new JButton("Purchase Item");
		addBidButton.setBounds(135, 342, 117, 29);
		addBidButton.setEnabled(false);
		addBidButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openPurchasingWindow(e, lotNumber);
			}
		});
		firstPanel.add(addBidButton);
		
		JButton returnHome = new JButton("Home");
		returnHome.setBounds(6, 342, 117, 29);
		returnHome.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				returnHome(e);
			}
		});
		firstPanel.add(returnHome);

		scrollPane = new JScrollPane();
		firstPanel.add(scrollPane);
		scrollPane.setBounds(6, 114, 251, 184);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(model);
		table.setBounds(6, 114, 235, 184);
		model.alignTable(table);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1){
					addBidButton.setEnabled(true);
					JTable selected = (JTable)e.getSource();
					int index = selected.getSelectedRow();
					lotNumber = (Integer) model.getValueAt(index, 0);
					displayLotDetails(lotNumber);
				}
			}
		});

		this.setVisible(true);
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
					lotItemTemplate.sold = false;
					LotItem lotItemObject = (LotItem) space.read(lotItemTemplate, null, 100);
					if (lotItemObject == null) {
						System.out.println("Nothing In Space");
					} else {
						int lotNumber = lotItemObject.lotNumber;
						int lotBuyNowValue = lotItemObject.lotBuyNowValue;
						String lotName = lotItemObject.lotName;
						boolean sold = lotItemTemplate.sold;
						String lotDescription = lotItemObject.lotDescription;
						String lotSeller = lotItemObject.lotSeller;
						ArrayList<Integer> starting_bid_price = new ArrayList<>();
						starting_bid_price.add(0);
						model.addNewLot(lotNumber, lotBuyNowValue, lotName, lotDescription, lotSeller, starting_bid_price, sold);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void returnHome(ActionEvent evt){
		dispose();
		MenuUI mainFrame = new MenuUI(this.userName);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(350,450);
	}

	private void openPurchasingWindow(ActionEvent evt, Integer lotID){
		new PurchasingUI(lotID).setVisible(true);
		addBidButton.setEnabled(false);
		lotItemDisplayList.setText(null);
	}

	private void displayLotDetails(Integer lotNumber){
		try{
			LotItem lotItemTemplate = new LotItem();
			lotItemTemplate.lotNumber = lotNumber;
			LotItem lotItemObject = (LotItem) space.readIfExists(lotItemTemplate, null, 100);
			if (lotItemObject == null) {
			}else {
				lotItemDisplayList.setText(null);
				lotItemDisplayList.append("------------------- \n" +
						lotItemObject.printItem());
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

	public void notify(RemoteEvent remoteEvent){
		item = new ArrayList<>(25);
		model = new TableModel(item);
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(model);
		table.setBounds(6, 114, 235, 184);
		model.alignTable(table);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1){
					JTable selected = (JTable)e.getSource();
					int index = selected.getSelectedRow();
					lotNumber = (Integer) model.getValueAt(index, 0);
					displayLotDetails(lotNumber);
					addBidButton.setEnabled(true);
				}
			}
		});
		System.out.println("Running notify");
		readLotFromSpace();
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
