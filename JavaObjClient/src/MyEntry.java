import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyEntry extends JLabel{
	private Frame frame;
	private FileDialog fd;
	
	private String username;
	private JButton profile_img;
	private ChatUI mainView= null;
	
	public MyEntry(String username, ImageIcon img, ChatUI mainView) {
		this.mainView=mainView;
		this.username=username;
		
		//������ �̹���
		Image temp1 = img.getImage();
		Image temp2 =temp1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		profile_img=new JButton(new ImageIcon(temp2));
		profile_img.setBounds(0,0,49,49);
		
		ChangeProfile cp = new ChangeProfile();
		profile_img.addActionListener(cp);
		
		add(profile_img);
		
		//����� �̸�
		this.setLayout(null);
		this.setSize(210,50);
		JLabel user = new JLabel(this.username);
		user.setBounds(72,10,80,30);
		add(user);
	}
	
	public MyEntry(String username, ImageIcon img) {
		this.username=username;
		
		//������ �̹���
		Image temp1 = img.getImage();
		Image temp2 =temp1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		profile_img=new JButton(new ImageIcon(temp2));
		profile_img.setBounds(0,0,49,49);
		
		ChangeProfile cp = new ChangeProfile();
		profile_img.addActionListener(cp);
		
		add(profile_img);
		
		//����� �̸�
		this.setLayout(null);
		this.setSize(210,50);
		JLabel user = new JLabel(this.username);
		user.setBounds(72,10,80,30);
		add(user);
	}
	
	public void setprofile_img(ImageIcon img) {
		Image temp1 = img.getImage();
		Image temp2 =temp1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		profile_img.setIcon(new ImageIcon(temp2));
	}
	
	class ChangeProfile implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			frame = new Frame("�̹���÷��");
			fd = new FileDialog(frame, "�̹��� ����", FileDialog.LOAD);
			fd.setVisible(true);
			
			//�����ʿ�
			ImageIcon temp = new ImageIcon();
			ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
			setprofile_img(img);
			mainView.myimg = img;
			ChatMsg profilechanger = new ChatMsg(mainView.UserName, "105", "change profile", mainView.myimg);
			profilechanger.setImg(img);
			mainView.SendObject(profilechanger);
		}
	}
}