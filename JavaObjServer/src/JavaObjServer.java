//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class JavaObjServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;
	private int roomid;
	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	
	private Vector UserVec = new Vector(); 	// 연결된 사용자를 저장할 벡터
	private Vector Users= new Vector();			//유저 정보 모음 벡터
	private Vector Clientlist = new Vector();		//세션용 벡터 testing필요
	private Vector Room_Log = new Vector();
	
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjServer frame = new JavaObjServer();
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
	public JavaObjServer() {
		roomid=1000;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getUser().getUserName() + "\n");
		textArea.append("data = " + msg.getData() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName="";
		private ImageIcon userimg;
		private UserInform userinform;
		
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}
		
		public void Load(String UserName) {
			boolean u = false;
			for(int i=0;i<Users.size();i++) {
				UserInform user = (UserInform)Users.elementAt(i);
				if(user.getUserName().equals(UserName))
					u=true;
			}
			if(u)
				ReLogin();
			else
				Login();
		}
		
		public void Login() {
			AppendText("새로운 참가자 " + UserName + " 입장.");
			userinform = new UserInform(UserName, userimg);		//현재 쓰레드의 유저 이름을 정보 객체에 저장
			Users.add(userinform);
			AppendText("참자가 1 이름 : "+ userinform.getUserName());
			ChatMsg msg = new ChatMsg("SERVER", "100", "Welcom", userinform.getImg());
			WriteAllObject(msg);
			
			updateUsers();
			
			String num1=" ";
			for(int i=0;i<Users.size();i++) {
				UserInform user = (UserInform) Users.elementAt(i);
				num1+=" "+user.getUserName();
			}
			AppendText("유저 리스트 : "+ num1+"/n");
		}
		
		public void ReLogin() {
			//유저 정보들 모두 보내기
			for(int i=0;i<Users.size();i++) {
				UserInform user = (UserInform)Users.elementAt(i);
				WriteOneObject(user);	//모든 유저정보 다 전달.
			}
			AppendText("방로그 수 : "+Room_Log.size());
			//방 로그 정보 모두 전달.
			for(int i=0;i<Room_Log.size();i++) {
				ChatMsg msg = (ChatMsg)Room_Log.elementAt(i);
				AppendText("방 정보 전달중"+i);
				WriteOneObject(msg);
			}
			AppendText("기존 유저로그인");
		}
		
		public void Logout() {
			AppendText("사용자 " + "[" + UserName + "] 로그아웃");
			UserVec.removeElement(this);
		}
		
		public void updateUsers() {
			for(int i=0;i<Users.size();i++) {
				UserInform user_i = (UserInform)Users.elementAt(i);
				ChatMsg msg = new ChatMsg(user_i.getUserName(),"108", " ",user_i.getImg());
				WriteOneObject(msg);		//기존 유저들에게 새로운 유저 정보 전달.
			}
			WriteAllObject(userinform);
		}
		
		//이모티콘 보내기
		public void SendEmoji(Object ob) {
			ChatMsg msg=(ChatMsg) ob;
			ImageIcon emoji =null;
			if(msg.getData().equals("angry")) {
				emoji= new ImageIcon("src/Emoji/angry.png");
			}else if(msg.getData().equals("good")) {
				emoji= new ImageIcon("src/Emoji/good.png");
			}else if(msg.getData().equals("hello")) {
				emoji= new ImageIcon("src/Emoji/hello.png");
			}else if(msg.getData().equals("love")) {
				emoji= new ImageIcon("src/Emoji/love.png");
			}else if(msg.getData().equals("smile")) {
				emoji= new ImageIcon("src/Emoji/smile.jpg");
			}else if(msg.getData().equals("sorry")) {
				emoji= new ImageIcon("src/Emoji/sorry.png");
			}else {
			}
			Image temp = emoji.getImage();
			Image temp2 = temp.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoticon = new ImageIcon(temp2);
			
			ChatMsg smsg = new ChatMsg(msg.getUser().getUserName(),"600",msg.getData(), msg.getUser().getImg());
			smsg.setImg(emoticon);
			smsg.setRoomid(msg.getRoomid());
			smsg.setTime(msg.getTime());
			System.out.println(msg.getRoomid());
			AppendText("이모티콘 : " +msg.getData()+" 시간 : "+msg.getTime());
			WriteAllObject(smsg);
			Room_Log.add(smsg);
		}
		
		public void InviteUser(String invite_user) {
			ChatMsg msg=null;
			String users = invite_user.trim();
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(users.contains(user.UserName)) {
					users = UserName + invite_user;
					msg = new ChatMsg("SERVER","500",users, null);
					msg.setRoomid(roomid);
					user.WriteOneObject(msg);
				}
			}
			Room_Log.add(msg);
		}
		
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}
		public void WriteOneObject(Object ob) {
			try {
				oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					}else
						continue;
					if (cm.getCode().matches("100")) {		//로그인
						UserName = cm.getUser().getUserName();
						userimg = cm.getUser().getImg();
						Load(UserName);
					}
					else if(cm.getCode().matches("105")){
						for(int i=0;i<Users.size();i++) {
							UserInform user = (UserInform)Users.elementAt(i);
							if(cm.getUser().getUserName().equals(user.getUserName())) {
								user.setImg(cm.getImg());
							}
						}
						AppendText("발신자 : "+cm.getUser().getUserName()+"profile 변경 프로토콜");
						WriteAllObject(cm);
					}
					else if (cm.getCode().matches("200")) {
						msg = String.format("[%s] %s %s", cm.getUser().getUserName(), cm.getRoomid(),cm.getData());
						AppendText(msg + "방번호 " +cm.getRoomid()); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						if (cm.getData().equals("")) { 
							//무시
						}
						else { // 일반 채팅 메시지
							WriteAllObject(cm);
							Room_Log.add(cm);
						}
					}
					else if (cm.getCode().matches("400")) { // logout message 처리
						Logout();
						break;
					}
					else if (cm.getCode().matches("300")) {
						AppendText("이미지 : " +cm.getData()+" 시간 : "+cm.getTime());
						WriteAllObject(cm);
						Room_Log.add(cm);
					}
					else if(cm.getCode().matches("500")) {	//방 만들기
						roomid++;
						AppendText("방 id : "+roomid+" 생성, 참여자 : "+cm.getData());
						InviteUser(cm.getData());
						
						String smsg= cm.getUser().getUserName()+"님이 "+cm.getData()+"님을 초대했습니다.";
						ChatMsg servermsg= new ChatMsg("SERVER", "200", smsg, new ImageIcon());
						servermsg.setRoomid(roomid);
						WriteAllObject(servermsg);
						Room_Log.add(servermsg);
					}	
					else if(cm.getCode().matches("502")) {
						AppendText("방 id : "+cm.getRoomid()+" 유저 리스트 출력");
						ChatMsg smsg = new ChatMsg("SERVER", cm.getCode(),"list", null);
						smsg.setRoomid(cm.getRoomid());
						WriteOneObject(smsg);
					}
					else if(cm.getCode().matches("505")) {	//방 나가기
						AppendText("사용자 : "+cm.getUser().getUserName()+"가 "+cm.getRoomid()+"번 방을 나갔습니다.");
						WriteAllObject(cm);
						String temp = cm.getUser().getUserName()+"님이 방을 나갔습니다.";
						
						//채팅 메시지 보내기
						ChatMsg cmsg = new ChatMsg("SERVER", "200", temp, new ImageIcon());
						cmsg.setRoomid(cm.getRoomid());
						WriteAllObject(cmsg);
						Room_Log.add(cmsg);
						Room_Log.add(cm);
					}
					
					else if(cm.getCode().matches("600")) {	//이모티콘보내기
						SendEmoji(cm);
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}
}
