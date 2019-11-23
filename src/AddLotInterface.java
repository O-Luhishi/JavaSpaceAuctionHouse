import net.jini.space.*;
import net.jini.core.lease.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class AddLotInterface extends JFrame {

	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 2 * 1000 * 60;

	private JavaSpace space;

	private JTextField lotNameIn, lotNumberIn, lotSellerIn, lotDescriptionIn, lotBuyNowValueIn;


	public AddLotInterface() {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		initComponents ();
//		pack ();
	}

	private void initComponents () {
		setTitle ("Add Lot UI");
		addWindowListener (new java.awt.event.WindowAdapter () {
			public void windowClosing (java.awt.event.WindowEvent evt) {
				System.exit (0);
			}
		}   );

		Container cp = getContentPane();
		cp.setLayout (new BorderLayout ());

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout (new FlowLayout ());

		JLabel lotNameLabel = new JLabel();
		lotNameLabel.setText ("Name of lot: ");
		jPanel1.add (lotNameLabel);

		lotNameIn = new JTextField (12);
		lotNameIn.setText ("");
		jPanel1.add (lotNameIn);

		JLabel lotDescriptionLabel = new JLabel();
		lotDescriptionLabel.setText ("Description: ");
		jPanel1.add (lotDescriptionLabel);

		lotDescriptionIn = new JTextField (12);
		lotDescriptionIn.setText ("");
		jPanel1.add (lotDescriptionIn);

		JLabel lotSellerNameLabel = new JLabel();
		lotSellerNameLabel.setText ("Seller Name: ");
		jPanel1.add (lotSellerNameLabel);

		lotSellerIn = new JTextField (12);
		lotSellerIn.setText ("");
		jPanel1.add (lotSellerIn);

		JLabel lotBuyNowValueLabel = new JLabel();
		lotBuyNowValueLabel.setText ("Buy Now Value: ");
		jPanel1.add (lotBuyNowValueLabel);

		lotBuyNowValueIn = new JTextField (6);
		lotBuyNowValueIn.setText ("");
		jPanel1.add (lotBuyNowValueIn);

		JLabel lotNumberLabel = new JLabel();
		lotNumberLabel.setText ("Auction Number: ");
		jPanel1.add (lotNumberLabel);

		lotNumberIn = new JTextField (6);
		lotNumberIn.setText ("");
		lotNumberIn.setEditable(false);
		jPanel1.add (lotNumberIn);

		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout (new FlowLayout ());

		JButton addLotButton = new JButton();
		addLotButton.setText("Add Lot To Auction");
		addLotButton.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				addLotToSpace();
			}
		}  );

		jPanel2.add(addLotButton);

        cp.add (jPanel1, "North");
        cp.add (jPanel2, "South");
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

	private void addLotToSpace(){
		try {
			LotIdIncrementor lotIdTemplate = readLotIDFromSpace();
			LotIdIncrementor lotIdObject = (LotIdIncrementor)space.take(lotIdTemplate,null, TWO_SECONDS);

			// create the new LotItem, write it to the space, and update the GUI
			int lotNumber = lotIdObject.lotID;
			String lotName = lotNameIn.getText();
			String lotDescription = lotDescriptionIn.getText();
			String lotSeller = lotSellerIn.getText();
			String strLotBuyNowValue = lotBuyNowValueIn.getText();
			Integer lotBuyNowValue = Integer.valueOf(strLotBuyNowValue);
			ArrayList<Integer> empty_array = new ArrayList<>();
			empty_array.add(0);
			LotItem newJob = new LotItem(lotNumber, lotBuyNowValue, lotName, lotDescription, lotSeller, empty_array);
			space.write( newJob, null, TWO_MINUTES);
			lotNumberIn.setText(""+lotNumber);
			// update the LotIdIncrementor object by incrementing the counter and write it back to the space
			lotIdObject.incrementLotID();
			space.write( lotIdObject, null, Lease.FOREVER);
		}  catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AddLotInterface().setVisible(true);
	}
}
