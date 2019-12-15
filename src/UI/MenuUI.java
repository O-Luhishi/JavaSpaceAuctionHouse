package UI;

import JavaSpaceConfig.SpaceUtils;
import UI.Admin.AddLotUI;
import UI.Admin.MyLotsUI;
import UI.Buyer.AuctionLotsUI;
import UI.Buyer.MyPurchasesUI;
import net.jini.space.JavaSpace;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuUI extends JFrame{
	private JavaSpace space;

	private JPanel firstPanel;

	private String userName;


	/**
	 * Create the frame.
	 */
	public MenuUI(String uname) {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		this.userName = uname;
		setTitle("Menu - Logged In As: " + uname);
		initComponents();
		setVisible(true);
	}
	
	private void initComponents() {
		setSize(350,450);
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
		lblNewLabel.setBounds(121, 6, 129, 58);
		firstPanel.add(lblNewLabel);
		
		JButton btnSellLot = new JButton("Sell A Lot");
		btnSellLot.setBounds(91, 138, 159, 64);
		btnSellLot.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openSellLotsUI();
			}
		});
		firstPanel.add(btnSellLot);
		
		JButton btnViewLots = new JButton("View Lots");
		btnViewLots.setBounds(92, 62, 159, 64);
		btnViewLots.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openViewLotsUI();
			}
		});
		firstPanel.add(btnViewLots);
		
		JButton btnAdminView = new JButton("My Lots");
		btnAdminView.setBounds(91, 214, 159, 64);
		btnAdminView.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openMyLotsUI();
			}
		});
		firstPanel.add(btnAdminView);

		JButton btnMyPurchasesView = new JButton("My Purchases");
		btnMyPurchasesView.setBounds(91, 290, 159, 64);
		btnMyPurchasesView.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openMyPurchasesUI();
			}
		});
		firstPanel.add(btnMyPurchasesView);

		JButton btnLogout = new JButton("Logout");
		btnLogout.setBounds(91, 358, 159, 64);
		btnLogout.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		firstPanel.add(btnLogout);
	}

	private void openSellLotsUI(){
		dispose();
		new AddLotUI(this.userName).setVisible(true);
	}

	private void openViewLotsUI(){
		dispose();
		new AuctionLotsUI(this.userName).setVisible(true);
	}

	private void openMyLotsUI(){
		dispose();
		new MyLotsUI(this.userName).setVisible(true);
	}

	private void openMyPurchasesUI(){
		dispose();
		new MyPurchasesUI(this.userName).setVisible(true);
	}

	private void logout(){
		dispose();
		LoginRegisterUI mainFrame = new LoginRegisterUI();
		mainFrame.loginUI();
		mainFrame.setVisible(true);
	}
}
