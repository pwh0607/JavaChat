//JavaObjServer.java ObjectStream ��� ä�� Server

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
	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	
	private Vector UserVec = new Vector(); 	// ����� ����ڸ� ������ ����
	private Vector Users= new Vector();			//���� ���� ���� ����
	private Vector Clientlist = new Vector();		//���ǿ� ���� testing�ʿ�
	private Vector Room_Log = new Vector();
	
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
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
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getUser().getUserName() + "\n");
		textArea.append("data = " + msg.getData() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
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
			// �Ű������� �Ѿ�� �ڷ� ����
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
			AppendText("���ο� ������ " + UserName + " ����.");
			userinform = new UserInform(UserName, userimg);		//���� �������� ���� �̸��� ���� ��ü�� ����
			Users.add(userinform);
			AppendText("���ڰ� 1 �̸� : "+ userinform.getUserName());
			ChatMsg msg = new ChatMsg("SERVER", "100", "Welcom", userinform.getImg());
			WriteAllObject(msg);
			
			updateUsers();
			
			String num1=" ";
			for(int i=0;i<Users.size();i++) {
				UserInform user = (UserInform) Users.elementAt(i);
				num1+=" "+user.getUserName();
			}
			AppendText("���� ����Ʈ : "+ num1+"/n");
		}
		
		public void ReLogin() {
			//���� ������ ��� ������
			for(int i=0;i<Users.size();i++) {
				UserInform user = (UserInform)Users.elementAt(i);
				WriteOneObject(user);	//��� �������� �� ����.
			}
			AppendText("��α� �� : "+Room_Log.size());
			//�� �α� ���� ��� ����.
			for(int i=0;i<Room_Log.size();i++) {
				ChatMsg msg = (ChatMsg)Room_Log.elementAt(i);
				AppendText("�� ���� ������"+i);
				WriteOneObject(msg);
			}
			AppendText("���� �����α���");
		}
		
		public void Logout() {
			AppendText("����� " + "[" + UserName + "] �α׾ƿ�");
			UserVec.removeElement(this);
		}
		
		public void updateUsers() {
			for(int i=0;i<Users.size();i++) {
				UserInform user_i = (UserInform)Users.elementAt(i);
				ChatMsg msg = new ChatMsg(user_i.getUserName(),"108", " ",user_i.getImg());
				WriteOneObject(msg);		//���� �����鿡�� ���ο� ���� ���� ����.
			}
			WriteAllObject(userinform);
		}
		
		//�̸�Ƽ�� ������
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
			AppendText("�̸�Ƽ�� : " +msg.getData()+" �ð� : "+msg.getTime());
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
		
		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
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
		
		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
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
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
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
					if (cm.getCode().matches("100")) {		//�α���
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
						AppendText("�߽��� : "+cm.getUser().getUserName()+"profile ���� ��������");
						WriteAllObject(cm);
					}
					else if (cm.getCode().matches("200")) {
						msg = String.format("[%s] %s %s", cm.getUser().getUserName(), cm.getRoomid(),cm.getData());
						AppendText(msg + "���ȣ " +cm.getRoomid()); // server ȭ�鿡 ���
						String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
						if (cm.getData().equals("")) { 
							//����
						}
						else { // �Ϲ� ä�� �޽���
							WriteAllObject(cm);
							Room_Log.add(cm);
						}
					}
					else if (cm.getCode().matches("400")) { // logout message ó��
						Logout();
						break;
					}
					else if (cm.getCode().matches("300")) {
						AppendText("�̹��� : " +cm.getData()+" �ð� : "+cm.getTime());
						WriteAllObject(cm);
						Room_Log.add(cm);
					}
					else if(cm.getCode().matches("500")) {	//�� �����
						roomid++;
						AppendText("�� id : "+roomid+" ����, ������ : "+cm.getData());
						InviteUser(cm.getData());
						
						String smsg= cm.getUser().getUserName()+"���� "+cm.getData()+"���� �ʴ��߽��ϴ�.";
						ChatMsg servermsg= new ChatMsg("SERVER", "200", smsg, new ImageIcon());
						servermsg.setRoomid(roomid);
						WriteAllObject(servermsg);
						Room_Log.add(servermsg);
					}	
					else if(cm.getCode().matches("502")) {
						AppendText("�� id : "+cm.getRoomid()+" ���� ����Ʈ ���");
						ChatMsg smsg = new ChatMsg("SERVER", cm.getCode(),"list", null);
						smsg.setRoomid(cm.getRoomid());
						WriteOneObject(smsg);
					}
					else if(cm.getCode().matches("505")) {	//�� ������
						AppendText("����� : "+cm.getUser().getUserName()+"�� "+cm.getRoomid()+"�� ���� �������ϴ�.");
						WriteAllObject(cm);
						String temp = cm.getUser().getUserName()+"���� ���� �������ϴ�.";
						
						//ä�� �޽��� ������
						ChatMsg cmsg = new ChatMsg("SERVER", "200", temp, new ImageIcon());
						cmsg.setRoomid(cm.getRoomid());
						WriteAllObject(cmsg);
						Room_Log.add(cmsg);
						Room_Log.add(cm);
					}
					
					else if(cm.getCode().matches("600")) {	//�̸�Ƽ�ܺ�����
						SendEmoji(cm);
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}
}
