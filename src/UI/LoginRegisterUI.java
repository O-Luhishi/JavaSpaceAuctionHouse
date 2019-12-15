package UI;

import Encryption.AES;
import JavaSpaceConfig.SpaceUtils;
import LotSpace.LotUser;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginRegisterUI extends JFrame{
    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds

    private JPanel firstPanel;
    private JTextField registerUsernameTextField, loginUsernameTextField;
    private JPasswordField loginPasswordField, registerPasswordField;

    private TransactionManager transactionManager;


    /**
     * Create the frame.
     */
    public LoginRegisterUI() {

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        // Find the transaction manager on the network
        transactionManager = SpaceUtils.getManager();
        if (transactionManager == null) {
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }

        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
    }

    public void loginUI(){
    	setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 350, 370);

        firstPanel = new JPanel();
        firstPanel.setVisible(true);
        firstPanel.setSize(540, 370);
        firstPanel.setBackground(Color.GRAY);
        getContentPane().add(firstPanel);
        firstPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Login To Auction Lots");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        lblTitle.setBounds(32, 6, 270, 58);
        firstPanel.add(lblTitle);

        JLabel lblLoginUsername = new JLabel("Username:");
        lblLoginUsername.setBounds(32, 89, 128, 16);
        firstPanel.add(lblLoginUsername);

        loginUsernameTextField = new JTextField();
        loginUsernameTextField.setBounds(30, 126, 130, 26);
        firstPanel.add(loginUsernameTextField);
        loginUsernameTextField.setColumns(10);
        
        JLabel lblLoginPassword = new JLabel("Password:");
        lblLoginPassword.setBounds(32, 179, 128, 16);
        firstPanel.add(lblLoginPassword);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(32, 293, 117, 29);
		btnLogin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
        firstPanel.add(btnLogin);
        
        JButton btnLoginRegister = new JButton("Register");
        btnLoginRegister.setBounds(213, 293, 117, 29);
		btnLoginRegister.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				LoginRegisterUI mainFrame = new LoginRegisterUI();
		        mainFrame.registerUI();			}
		});
        firstPanel.add(btnLoginRegister);
        
        loginPasswordField = new JPasswordField();
        loginPasswordField.setBounds(32, 218, 128, 26);
        firstPanel.add(loginPasswordField);
    }

    private void registerUI(){
    	setTitle("Register");
    	setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 350, 315);

        firstPanel = new JPanel();
        firstPanel.setVisible(true);
        firstPanel.setSize(540, 315);
        firstPanel.setBackground(Color.GRAY);
        getContentPane().add(firstPanel);
        firstPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Register An Account");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        lblTitle.setBounds(32, 6, 270, 58);
        firstPanel.add(lblTitle);
        
        JLabel lblRgisterUsername = new JLabel("Username:");
        lblRgisterUsername.setBounds(32, 89, 128, 16);
        firstPanel.add(lblRgisterUsername);
        
        registerUsernameTextField = new JTextField();
        registerUsernameTextField.setBounds(30, 126, 130, 26);
        firstPanel.add(registerUsernameTextField);
        registerUsernameTextField.setColumns(10);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(32, 179, 128, 16);
        firstPanel.add(lblPassword);
        
        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(116, 256, 117, 29);
		btnRegister.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close Register Window & Open Login
				registerNewUser();
                dispose();
                LoginRegisterUI mainFrame = new LoginRegisterUI();
                mainFrame.loginUI();
                mainFrame.setVisible(true);
            }
		});
        firstPanel.add(btnRegister);

        registerPasswordField = new JPasswordField();
        registerPasswordField.setBounds(32, 218, 128, 26);
        firstPanel.add(registerPasswordField);
    }

    private void registerNewUser(){
        // Now try to take the object back out of the space, modify it, and write it back again.
        // All of this IS part of one single transaction, so it all happens or it all rolls back and never happens
        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, TWO_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try{
                String uname = registerUsernameTextField.getText();
                String pass = hashPassword(new String(registerPasswordField.getPassword()));
                LotUser newUser  = new LotUser(uname, pass);
                space.write(newUser, txn, Lease.FOREVER);
            }catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }
            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }

    private void login(){
        try{
            String uname = loginUsernameTextField.getText();
            String pass = hashPassword(new String(loginPasswordField.getPassword()));
            LotUser lotUserTemplate = new LotUser();
            lotUserTemplate.userName = uname;
            lotUserTemplate.hashedPassword = pass;
            LotUser lotUserObject = (LotUser) space.readIfExists(lotUserTemplate, null, 100);
            if (lotUserObject == null){
                outputIncorrectCredentials();
            }else{
                dispose();
                MenuUI mainFrame = new MenuUI(uname);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setSize(350,450);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void outputIncorrectCredentials(){
        JOptionPane.showMessageDialog(null, "Please Check Your Login Credentials Again" , "Incorrect Credentials", JOptionPane.ERROR_MESSAGE);
    }

    private String hashPassword(String password){
        final String secretKey = "ssshhhhhhhhhhh!!!!";
        return AES.encrypt(password, secretKey);
    }

    private String decryptPassword(String password){
        final String secretKey = "ssshhhhhhhhhhh!!!!";
        return AES.decrypt(password, password);
    }

    public static void main(String[] args) {
        LoginRegisterUI mainFrame = new LoginRegisterUI();
        mainFrame.loginUI();
        mainFrame.setVisible(true);

    }
}

