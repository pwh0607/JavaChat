import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class AddUser1 extends JFrame{
	private static final long serialVersionUID = 1L;
	private Vector users= new Vector();		//방의유저
	private Vector addUser = new Vector();	//멤버 추가용 유저
	private JPanel contentPane;
	private JPanel panel;
	private JButton send;
	private JButton create;
	private String userlist="";
	private JTextPane u_list;
	
	private ChatUI mainView;
	
	
	public AddUser1(Vector users, ChatUI mainView) {
		this.users=users;
		this.mainView=mainView;
		
		setBounds(300,100,220,350);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setVisible(true);
		JScrollPane sc = new JScrollPane();
		sc.setBounds(0,30,203,240);
		contentPane.add(sc);
		panel=new JPanel();
		panel.setLayout(null);
		sc.setViewportView(panel);
		
		u_list = new JTextPane();
		u_list.setBounds(10,5,180,20);
		u_list.setText("초대 : " + userlist);
		contentPane.add(u_list);
		
		create = new JButton("방 생성하기");
		create.setBounds(27,276,150,28);
		Action1 a1 = new Action1();
		create.addActionListener(a1);
		contentPane.add(create);
		
		for(int i=0;i<users.size();i++) {
			UserInform user= (UserInform)users.elementAt(i);
			friendEntry f = new friendEntry(user.getUserName(),user.getImg(),mainView);
			
			//버튼 이미지
			ImageIcon img = new ImageIcon("src/resource/checkbox.png");
			Image i1 = img.getImage();
			Image i2 = i1.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
			
			send=new JButton(new ImageIcon(i2));
			send.setBorderPainted(true);
			send.setContentAreaFilled(false);
			
			
			SendAction action= new SendAction(user.getUserName());
			send.addActionListener(action);
			f.setLocation(-1,50*i);
			send.setBounds(168,5,35,35);
			f.add(send);
			panel.add(f);
		}
	}
	
	public class SendAction implements ActionListener{
		private String username;
		private boolean t = false;
		public SendAction(String username) {
			this.username=username;
		}
		public void actionPerformed(ActionEvent e) {
			userlist="";
			t=!t;
			if(t) {
				addUser.add(username);	
			}
			else {
				addUser.remove(username);
			}
			
			for(int i=0; i<addUser.size();i++) {
				String user = (String)addUser.elementAt(i);
				userlist=userlist+user+" ";
			}
			u_list.setText("초대 : "+userlist.trim());
		}
	}
	
	//최종 채팅방 만들기 버튼 이벤트
	public class Action1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(userlist.equals(""))
				return;				//비어있으면 무시
			ChatMsg make = new ChatMsg(mainView.UserName, "500", userlist.trim(), new ImageIcon());
			mainView.SendObject(make);
			setVisible(false);
		}
	}
}