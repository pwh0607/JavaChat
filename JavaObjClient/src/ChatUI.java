// JavaObjClient.java
// ObjecStream 사용하는 채팅 Client

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.ScrollPane;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.awt.event.ActionEvent;

public class ChatUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel fpage=new JPanel();
	private JPanel cpage=new JPanel();

	private JButton friends;
	private JButton chat;
	private JButton plus;
	public ImageIcon myimg;
	
	public String UserName;
	
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private JScrollPane sp1;
	private JScrollPane sp2;
	
	private static JPanel list1;
	private static JPanel list2; 
	
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Frame frame;
	
	private Vector roomlist = new Vector();
	private Vector r_entrylist = new Vector();
	public Vector Users = new Vector();		//친구정보
	private int k=0;
	private ChatUI mainView=this;
	/**
	 * Create the frame.
	 */
	public ChatUI(String username, String ip_addr, String port_no, ImageIcon myimg) {
		this.myimg=myimg;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(245,245,245));
		contentPane.setLayout(null);
		
		//fpage
		fpage.setBounds(74,0,210,461);
		fpage.setBackground(new Color(255,255,255));
		fpage.setLayout(null);
		
		JLabel title1 = new JLabel("친구");
		title1.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		title1.setBounds(10,7,50,40);
		
		sp1=new JScrollPane();	
		sp1.setBounds(0,101,213,360);
		
		list1 = new JPanel();
		list1.setLayout(null);
		list1.setBackground(new Color(255,255,255));
		sp1.setViewportView(list1);
		
		MyEntry me = new MyEntry(username,myimg,mainView);
		me.setLocation(0,53);
		fpage.add(me);
		
		fpage.add(sp1);
		fpage.add(title1);
		contentPane.add(fpage);
		
		cpage.setBounds(74,0,210,461);
		cpage.setBackground(new Color(255,255,255));
		cpage.setLayout(null);
		
		JLabel title2 = new JLabel("채팅");
		title2.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		title2.setBounds(10,7,50,40);
		
		sp2=new JScrollPane();
		sp2.setBounds(0,51,210,410);
		
		list2 = new JPanel();
		list2.setLayout(null);
		list2.setBackground(new Color(255,255,255));
		sp2.setViewportView(list2);
		
		//채팅 추가 버튼.
		ImageIcon i1 = new ImageIcon("src/resource/makeRoom.png");
		Image i2 = i1.getImage();
		Image i3 = i2.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		
		plus = new JButton(new ImageIcon(i3));
		plus.setBounds(153,8,40,40);
		plus.setBorderPainted(false);
		plus.setContentAreaFilled(false);
		
		createChatRoom create = new createChatRoom();
		plus.addActionListener(create);
		
		cpage.add(plus);
		cpage.add(sp2);
		cpage.add(title2);
		
		contentPane.add(cpage);
	
		fpage.setVisible(true);
		cpage.setVisible(false);
		
		//버튼
		//버튼 이미지
		ImageIcon freIcon = new ImageIcon("src/resource/friends.png");
		Image fimg1 = freIcon.getImage();
		Image fimg2 = fimg1.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		
		ImageIcon chatIcon = new ImageIcon("src/resource/chatRoom.png");
		Image cimg1 = chatIcon.getImage();
		Image cimg2 = cimg1.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		
		friends = new JButton(new ImageIcon(fimg2));
		chat = new JButton(new ImageIcon(cimg2));
		
		friends.setBorderPainted(false);
		friends.setContentAreaFilled(false);
		chat.setBorderPainted(false);
		chat.setContentAreaFilled(false);
		
		friends.setBounds(10,10,50,50);
		chat.setBounds(10,65,50,50);

		setFriends setf = new setFriends();
		setChatRoom setc = new setChatRoom();
		
		friends.addActionListener(setf);
		chat.addActionListener(setc);
		
		contentPane.add(friends);
		contentPane.add(chat);
		
		setVisible(true);
		
		UserName=username;
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
			//로그인
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello", myimg);
			SendObject(obcm);
			
			ListenNetwork net = new ListenNetwork();
			net.start();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm=null;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = cm.getData();
					}
					else if(obcm instanceof UserInform) {
						updateNewFriend(obcm);
					}
					else
						continue;
					if(cm == null)
						continue;			
					switch (cm.getCode()) {
					case "105":		//친구 프로필 사진 변경.
						changeProfile(cm);
						break;
					case "108":
						updateFriends(obcm);
						break;
					case "200": // chat message
						//이 유저가 채팅방에 속해있다면 텍스트 삽입
						for(int i=0;i<roomlist.size();i++) {
							ChatRoom cr = (ChatRoom)roomlist.elementAt(i);
							if(cr.getRoomid()==cm.getRoomid()){
								if(!cm.getUser().getUserName().trim().matches(UserName) && !cm.getUser().getUserName().equals("SERVER"))
									{
										cr.AppendOtherText(cm);
										cr.AppendNull();
									}
								else if (cm.getUser().getUserName().equals("SERVER"))	{//서버가 보낸 메시지라면.
									cr.AppendServerText(cm);
									cr.AppendNull();
							}
								else {
									cr.AppendMyText(cm);
									cr.AppendNull();
								}
							}
						}
						break;
					case "300": // Image 첨부
						for(int i=0;i<roomlist.size();i++) {
							ChatRoom cr = (ChatRoom)roomlist.elementAt(i);
							if(cr.getRoomid()==cm.getRoomid()){
								if(!cm.getUser().getUserName().trim().matches(UserName)) {
									cr.AppendIcon(myimg, cm.getUser().getUserName());
									cr.AppendOtherImage(cm);
									cr.AppendNull();
								}
								else {
									cr.AppendMyImage(cm);
									cr.AppendNull();
								}
							}
						} 
						break;
					case "500":
						int temp_room=cm.getRoomid();
						String roomUser= cm.getData();
						makeChatRoom(temp_room, roomUser);
						break;
					case "501":
						int addtemp_room=cm.getRoomid();
						String addroomUser= cm.getData();
						makeChatRoom(addtemp_room, addroomUser);
						break;
					case "502":			//리스트 출력
						for(int i=0;i<roomlist.size();i++) {
							ChatRoom cr = (ChatRoom)roomlist.elementAt(i);
							if(cr.getRoomid()==cm.getRoomid()){
								cr.showUserList();
							}
						}
						break;
					case "505":
						exitRoom(cm);
						break;
					case "600":		//이모티콘
						for(int i=0;i<roomlist.size();i++) {
							ChatRoom cr = (ChatRoom)roomlist.elementAt(i);
							if(cr.getRoomid()==cm.getRoomid()){
								if(!cm.getUser().getUserName().trim().matches(UserName)) {
									cr.AppendIcon(myimg, cm.getUser().getUserName());
									cr.AppendOtherImage(cm);
									cr.AppendNull();
								}
								else {
									cr.AppendMyImage(cm);
									cr.AppendNull();
								}
							}
						}
						break;
					}
				} catch (IOException e) {
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			}
		}
	}
	
	public void changeProfile(Object ob) {
		ChatMsg msg = (ChatMsg) ob;
		System.out.println(msg.getUser()+"상대 프로필 변경완료");
		for(int i=0;i<Users.size();i++) {
			UserInform user = (UserInform)Users.elementAt(i);
			if(user.getUserName().equals(msg.getUser().getUserName())) {
				user.setImg(msg.getImg());
			}
		}
		//모든 방에 프로필 변경할 것 있는 지 확인후 실시
		for(int i=0;i<roomlist.size();i++) {
			ChatRoom c = (ChatRoom)roomlist.elementAt(i);
			c.changeProfile(msg.getUser().getUserName(), msg.getImg());
		}
		updatelist1();
	}
	
	private void updateFriends(Object ob) {
		ChatMsg msg = (ChatMsg)ob;
		UserInform user_i = new UserInform(msg.getUser().getUserName(), msg.getUser().getImg());
		if(!user_i.getUserName().equals(UserName))
			Users.add(user_i);
		updatelist1();
	}
	
	private void updateNewFriend(Object ob) {
		UserInform user = (UserInform)ob;
		if(!user.getUserName().equals(UserName))
			Users.add(user);
		updatelist1();
	}
	
	private void updatelist1() {
		list1.removeAll();
		for(int i=0;i<Users.size();i++) {
			UserInform user = (UserInform) Users.elementAt(i);
			friendEntry f = new friendEntry(user.getUserName(), user.getImg(), mainView);
			f.setLocation(-1,50*i);
			list1.add(f);
			list1.repaint();
		}
	}
	
	public void exitRoom(Object ob) {
		ChatMsg msg = (ChatMsg)ob;
		if(msg.getUser().getUserName().equals(UserName)) {
			for(int i=0;i<roomlist.size();i++) {
				ChatRoom room = (ChatRoom)roomlist.elementAt(i);
				if(msg.getRoomid()==room.getRoomid()) {
					roomlist.remove(room);
				}
			}
			for(int i=0;i<r_entrylist.size();i++) {
				chatEntry e = (chatEntry)r_entrylist.elementAt(i);
				if(msg.getRoomid()==e.Roomid) {
					r_entrylist.remove(e);
				}
			}
			//둘다 삭제후 
		updateChatRoomList();
		}
		else		//다른 사람이 나간 경우라면.
		{
			for(int i=0;i<roomlist.size();i++) {
				ChatRoom room = (ChatRoom)roomlist.elementAt(i);
				if(msg.getRoomid()==room.getRoomid()) {
					room.setUserList(msg.getUser().getUserName());
					}
				}
		}
	}
	
	public void makeChatRoom(int roomid, String roomUser) {
		System.out.println("이 방의 유저는 : "+roomUser);
		Vector temp_userlist = new Vector();
		//임시용 친구 벡터
		for(int i=0; i<Users.size();i++) {
			UserInform user = (UserInform)Users.elementAt(i);
			if(roomUser.contains(user.getUserName())) {
				temp_userlist.add(user);
			}
		}
		temp_userlist.add(new UserInform(UserName, myimg));
		ChatRoom room= new ChatRoom(mainView, roomid, temp_userlist);		
		chatEntry e = new chatEntry(roomUser.trim(), roomid);		
		
		//엔터버튼 이미지
		ImageIcon i1 = new ImageIcon("src/resource/enterRoom.png");
		Image i2 = i1.getImage();
		Image i3 = i2.getScaledInstance(20, 50, Image.SCALE_SMOOTH);
		
		JButton enter = new JButton(new ImageIcon(i3));
		enter.setBorderPainted(false);
		enter.setContentAreaFilled(false);
		
		enter.setBounds(190, 0, 20,50);
		setRoomAction action1 = new setRoomAction(room);
		enter.addActionListener(action1);
		e.add(enter);
		roomlist.add(room);
		r_entrylist.add(e);
		updateChatRoomList();
	}
	
	public void updateChatRoomList() {
		list2.removeAll();
		for(int i=0;i<r_entrylist.size();i++) {
			chatEntry e = (chatEntry)r_entrylist.elementAt(i);
			e.setLocation(-1,50*i);
			list2.add(e);
		}
		list2.repaint();
	}
	
	public void viewChatroom(ChatRoom room, int roomid) {
		for(int i=0;i<roomlist.size();i++) {
			ChatRoom temp= (ChatRoom)roomlist.elementAt(i);
			if(temp.getRoomid()==roomid) {
				ChatRoom instance = new ChatRoom();
				instance=temp;
				instance.repaint();
			}
		}
	}
	
	class setFriends implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			cpage.setVisible(false);
			fpage.setVisible(true);
		}
	}
	
	class setChatRoom implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			fpage.setVisible(false);
			cpage.setVisible(true);
		}
	}
	
	class createChatRoom implements ActionListener{
		public void actionPerformed(ActionEvent e){
			AddUser1 create = new AddUser1(Users, mainView);
		}
	}
	
	class setRoomAction implements ActionListener{
		private ChatRoom room;
		public setRoomAction(ChatRoom room) {
			this.room=room;
		}
		
		public void actionPerformed(ActionEvent e) {
			ChatRoom instance = room;
			instance.setVisible(true);
		}
	}
	
	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg, myimg);
			obcm.setTime(getTime());
			oos.writeObject(obcm);
		} catch (IOException e) {
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			//AppendText(" Error");
		}
	}
	
	public Vector getUsers() {
		return Users;
	}
	
	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date t = new Date();
		String time = " "+format.format(t)+" ";
		
		return time;
	}
}