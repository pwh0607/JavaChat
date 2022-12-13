import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class chatEntry extends JLabel{
	private String users;
	private JButton Room_img;
	private JLabel Room_users;
	private JButton enter;
	public int Roomid;
	public chatEntry(String user, int Roomid) {
		this.users=user.trim();
		this.Roomid=Roomid;
		//방 이미지
		ImageIcon roomicon = new ImageIcon("src/resource/kakaoprofile.jpg");
		Image temp1 = roomicon.getImage();
		Image temp2 =temp1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		Room_img=new JButton(new ImageIcon(temp2));
		Room_img.setBounds(0,0,49,49);
		add(Room_img);

		this.setLayout(null);
		this.setSize(210,50);

		//사용자 이름
		Room_users = new JLabel(users);
		Room_users.setBounds(72,10,80,30);

		enter = new JButton();
		
		add(Room_users);
	}
}