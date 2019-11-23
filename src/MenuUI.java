import net.jini.space.JavaSpace;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuUI extends JFrame{
	private JavaSpace space;

	private JPanel firstPanel;


	/**
	 * Create the frame.
	 */
	public MenuUI() {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		setTitle("Menu");
		initComponents();
		setVisible(true);
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
		
		JLabel lblNewLabel = new JLabel("Main Menu");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setBounds(212, 6, 129, 58);
		firstPanel.add(lblNewLabel);
		
		JButton btnSellLot = new JButton("Sell A Lot");
		btnSellLot.setBounds(331, 91, 159, 64);
		btnSellLot.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openSellLotsUI();
			}
		});
		firstPanel.add(btnSellLot);
		
		JButton btnViewLots = new JButton("View Lots");
		btnViewLots.setBounds(31, 91, 159, 64);
		btnViewLots.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openViewLotsUI();
			}
		});
		firstPanel.add(btnViewLots);
		
		JButton btnAdminView = new JButton("Admin View");
		btnAdminView.setBounds(31, 264, 159, 64);
		firstPanel.add(btnAdminView);
		
		JButton btnLogout = new JButton("Logout");
		btnLogout.setBounds(331, 264, 159, 64);
		firstPanel.add(btnLogout);
		
	}

	private void openSellLotsUI(){
		dispose();
		new AuctionLotsUI().setVisible(true);
	}

	private void openViewLotsUI(){
		dispose();
		new AddLotUI().setVisible(true);
	}
	public static void main(String[] args) {
		MenuUI mainFrame = new MenuUI();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(550,450);
	}
}
