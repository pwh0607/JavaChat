// JavaObjClient.java
// ObjecStream 사용하는 채팅 Client

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionEvent;

public class JavaObjClientMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUserName;
	private ImageIcon imgIcon;
	private Image img;
	private ImageIcon profileimg;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjClientMain frame = new JavaObjClientMain();
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
	public JavaObjClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(Color.YELLOW);
		
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(63, 230, 160, 33);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		
		//프로필 사진
		imgIcon = new ImageIcon("src/resource/kakaoProfile.jpg");
		img = imgIcon.getImage();
		img.getScaledInstance(170, 170, Image.SCALE_SMOOTH);
		profileimg = new ImageIcon(img);
		
		JLabel profile = new JLabel(profileimg);
		profile.setBounds(58,40,170,170);
		contentPane.add(profile);
		
		//로그인 버튼
		JButton btnlogin = new JButton(new ImageIcon("src/resource/kakaoLogin.png"));
		Myaction action = new Myaction();
		btnlogin.setBorderPainted(false);
		btnlogin.setContentAreaFilled(false);
		btnlogin.setBounds(63, 280, 160, 38);
		btnlogin.addActionListener(action);
		contentPane.add(btnlogin);
	}

	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = "127.0.0.1";
			String port_no = "30000";
			ChatUI view = new ChatUI(username, ip_addr, port_no, profileimg);
			setVisible(false);
		}
	}	
}