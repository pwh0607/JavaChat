import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class friendEntry extends JLabel{
	private String username;
	private JButton profile_img;
	public friendEntry(String username, ImageIcon img) {
		this.username=username;
		//프로필 이미지
		Image temp1 = img.getImage();
		Image temp2 =temp1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		profile_img=new JButton(new ImageIcon(temp2));
		profile_img.setBounds(0,0,49,49);
		add(profile_img);
		
		//사용자 이름
		this.setLayout(null);
		this.setSize(210,50);
		JLabel user = new JLabel(this.username);
		user.setBounds(72,10,80,30);
		add(user);
	}
	
}