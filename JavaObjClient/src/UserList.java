import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UserList extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Vector Users;
	public UserList(Vector v){
		System.out.println("유저 수 : "+v.size());
		setBounds(300,100,220,350);
	
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setUserList(v);
		add(contentPane);
	}
	
	public void setUserList(Vector v) {
		this.Users = v;
		
		System.out.println("유저 수 : "+Users.size());
		for(int i=0;i<Users.size();i++) {
			UserInform user= (UserInform)Users.elementAt(i);
			friendEntry f = new friendEntry(user.getUserName(), user.getImg());
			contentPane.add(f);
			f.setLocation(-1,50*i);
			contentPane.add(f);
		}
	}
}
