import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class profile extends JFrame{
	private ImageIcon img;
	
	private JPanel contentPane;
	public profile(String name, ImageIcon img) {
		setBounds(100, 100, 300, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(Color.WHITE);
		
		//프로필 사진
		Image img1 = img.getImage();
		Image img2 = img1.getScaledInstance(170, 170, Image.SCALE_SMOOTH);
		ImageIcon profileimg = new ImageIcon(img2);
		
		JLabel UserName = new JLabel("[" +name+"]");
		UserName.setBackground(Color.white);
		UserName.setOpaque(true);
		UserName.setBounds(63, 230, 160, 33);
		UserName.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(UserName);
		
		JLabel profile = new JLabel(profileimg);
		profile.setBounds(58,40,170,170);
		contentPane.add(profile);
	}
	
	public void setImg(ImageIcon img) {
		this.img =img;
	}
}
