package Main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.SystemColor;
import java.awt.Window.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.Color;
import javax.swing.border.CompoundBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JPanel login;
	private JTextField username;
	private JTextField password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
	    setBackground(new Color(248, 248, 255));
		setTitle("NSync File Sharing");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				ClientGUI.class.getResource("/Images/fb.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 385, 337);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new CompoundBorder());
		setContentPane(contentPane);
		BufferedImage myPicture = null;
		final String dir = System.getProperty("user.dir");
		System.out.println("current dir = " + dir);
		try {
			myPicture = ImageIO.read(new File(dir + "\\src\\Images\\logo.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contentPane.setLayout(new BorderLayout(0, 0));
		JLabel logo = new JLabel(new ImageIcon(myPicture));

		JPanel top = new JPanel();
		top.setBackground(new Color(248, 248, 255));
		contentPane.add(top, BorderLayout.NORTH);
		top.setLayout(new MigLayout("",
				"[grow,fill][fill][][][][][][][][][][][grow,fill]",
				"[][96.00][]"));
		top.add(logo, "cell 1 1 11 1,alignx center,aligny center");

		login = new JPanel();
		login.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.setBorder(new CompoundBorder());
		login.setBackground(new Color(248, 248, 255));
		contentPane.add(login, BorderLayout.CENTER);
		login.setLayout(new MigLayout("", "[][][][][][grow]",
				"[][33.00][][33][33][]"));

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(lblUsername, "cell 3 1");

		username = new JTextField();
		username.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(username, "cell 5 1,grow");
		username.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(lblPassword, "cell 3 3");

		password = new JTextField();
		password.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(password, "cell 5 3,grow");
		password.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(btnLogin, "flowx,cell 5 4,growy");

		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ClientSignUpGUI cs = new ClientSignUpGUI();
				cs.setLocationRelativeTo(null);
				cs.setVisible(true);
			}
		});
		btnSignUp.setFont(new Font("SansSerif", Font.PLAIN, 14));
		login.add(btnSignUp, "cell 5 4,growy");
	}

	public JPanel getMaincontent() {
		return login;
	}
}
