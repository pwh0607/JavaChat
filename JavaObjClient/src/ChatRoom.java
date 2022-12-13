// JavaObjClientView.java ObjecStram 기반 Client
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JToggleButton;
import javax.swing.JList;

public class ChatRoom extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private JLabel textmsg;
	public JTextPane textArea;
	
	private SimpleAttributeSet attr = new SimpleAttributeSet();
	
	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn1;
	private JButton imgBtn2;
	private JButton exit;
	private JButton list;
	public Vector Userlist = new Vector();		//채팅방 사용자 벡터
	public Vector pl = new Vector();			//채팅방 사용자 프로필 레이블 저장용 벡터
	private ChatUI mainView;
	private int roomid;
	private UserList Memberlist;
	private EmojiPane EmojiList;
	private boolean t=true;

	/**
	 * Create the frame.
	 */
	ChatRoom() {
		
	}
	
	public ChatRoom(ChatUI mainView, int roomid, Vector Userlist) {
		//좌우 정렬
		this.mainView=mainView;		//채팅방 사용자 리스트 저장
		this.roomid=roomid;
		this.Userlist=Userlist;
		
		setBounds(100, 100, 394, 630);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(7, 35, 362, 445);
		contentPane.add(scrollPane);
		
		//메시지 출력 부분
		textArea = new JTextPane();
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		textArea.setBackground(new Color(186,206,224));
		scrollPane.setViewportView(textArea);
		
		//메시지 입력 부분
		txtInput = new JTextField();
		txtInput.setBounds(7, 489, 300, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		TextSendAction action = new TextSendAction();
		
		btnSend = new JButton("전송");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 15));
		btnSend.setBounds(305, 488, 67, 42);
		btnSend.setOpaque(false);
		btnSend.addActionListener(action);
		contentPane.add(btnSend);
		setVisible(false);
		
		//이미지들 수정
		ImageIcon ic1 = new ImageIcon("src/resource/face.png");
		ImageIcon ic2 = new ImageIcon("src/resource/clip.png");
		
		Image img1 = ic1.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Image img2 = ic2.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		
		//이미지, 사진 보내기 구현
		imgBtn1 = new JButton(new ImageIcon(img1));				//이모티콘
		imgBtn2 = new JButton(new ImageIcon(img2));				//사진 보내기
		imgBtn1.setBounds(12, 540, 40, 40);
		imgBtn2.setBounds(60, 540, 40, 40);
		imgBtn1.setBorderPainted(false);
		imgBtn1.setContentAreaFilled(false);
		imgBtn2.setBorderPainted(false);
		imgBtn2.setContentAreaFilled(false);
		
		imgBtn2.addActionListener(new ImageSendAction());
		imgBtn1.addActionListener(new EmojiSendAction());
		
		//나가기, 추가하기
		ImageIcon mp1 = new ImageIcon("src/resource/memplus.png");
		ImageIcon ex1 = new ImageIcon("src/resource/exit.png");
		ImageIcon list1 = new ImageIcon("src/resource/group.png");
		
		Image ex2 = ex1.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		Image mp2 = mp1.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		Image list2 = list1.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		
		exit = new JButton(new ImageIcon(ex2));
		exit.setBounds(339, 4, 25, 25);
		
		list = new JButton(new ImageIcon(list2));
		list.setBounds(300, 4, 25, 25);
		
		//이모티콘 모음
		EmojiList = new EmojiPane(mainView, roomid);
		EmojiList.setLocation(12, 592);
		
		ExitAction exitaction = new ExitAction();
		ShowListAction listaction = new ShowListAction();
		
		exit.addActionListener(exitaction);
		list.addActionListener(listaction);
		contentPane.add(exit);
		contentPane.add(EmojiList);
		contentPane.add(imgBtn1);
		contentPane.add(imgBtn2);
		contentPane.add(list);
		
		repaint();
	}
	
	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String data = txtInput.getText();
				ChatMsg msg =null;
				msg = new ChatMsg(mainView.UserName, "200" , data, mainView.myimg);
				msg.setRoomid(roomid);
				msg.setTime(getTime());
				
				txtInput.setText("");			// 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus();		// 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				mainView.SendObject(msg);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn2 || e.getSource() == txtInput) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				fd.setVisible(true);
				
				//수정필요
				ImageIcon temp = new ImageIcon();
				ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
				ChatMsg obcm = new ChatMsg(mainView.UserName, "300", "IMG", mainView.myimg);
				obcm.setTime(getTime());
				obcm.setImg(img);
				obcm.setRoomid(roomid);
				mainView.SendObject(obcm);
			}
		}
	}
	
	class EmojiSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if(t==true)
				setSize(394, 930);
			else
				setSize(394, 630);
			t=!t;
		}
	}
	
	class ExitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ChatMsg obcm = new ChatMsg(mainView.UserName, "505", "Exit", mainView.myimg);
			obcm.setRoomid(roomid);
			mainView.SendObject(obcm);
			dispose();
			}
	}
	
	class ShowListAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ChatMsg obcm = new ChatMsg(mainView.UserName, "502", "List",null);
			obcm.setRoomid(roomid);
			mainView.SendObject(obcm);
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

	public void AppendMyImage(ChatMsg cmsg) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		Image ori_img = cmsg.getImg().getImage();
		String time = cmsg.getTime();
		int width, height;
		double ratio;
		width = cmsg.getImg().getIconWidth();
		height = cmsg.getImg().getIconHeight();
		
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 300 || height > 300) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 250;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon new_icon = new ImageIcon(new_img);
			
			textArea.replaceSelection("\n");
			textArea.insertIcon(new_icon);
			} else {
			textArea.insertIcon(cmsg.getImg());
		}
		StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
		textArea.setParagraphAttributes(attr, true);
		textArea.replaceSelection(cmsg.getTime());
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		AppendNull();
	}
	
	public void AppendOtherImage(ChatMsg cmsg) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		Image ori_img = cmsg.getImg().getImage();
		String time = cmsg.getTime();
		int width, height;
		double ratio;
		width = cmsg.getImg().getIconWidth();
		height = cmsg.getImg().getIconHeight();
		
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 300 || height > 300) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 250;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon new_icon = new ImageIcon(new_img);

			textArea.replaceSelection("\n");

			textArea.replaceSelection(time);
			textArea.insertIcon(new_icon);
		} else {
			textArea.replaceSelection("\n");

			textArea.replaceSelection(time);
			textArea.insertIcon(cmsg.getImg());
		}
		//수정		
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		}
	
	public void AppendIcon(ImageIcon icon, String user) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		Image i1 = icon.getImage();
		Image i2 = i1.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon profile = new ImageIcon(i2);
		
		profileLabel pro = new profileLabel(icon, user);
		pl.add(pro);
		
		textArea.setCaretPosition(len);
		textArea.insertComponent(pro);
		StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
		textArea.setParagraphAttributes(attr, true);
		textArea.replaceSelection("\n");
	}
	
	public void AppendMyText(ChatMsg cmsg) {
		int len = textArea.getDocument().getLength();
		
		String send_user = cmsg.getUser().getUserName();
		String msg= cmsg.getData().trim(); 		// 앞뒤 blank와 \n을 제거한다.
		String time = cmsg.getTime();
		JLabel text= new JLabel(msg);
		text.setBackground(Color.yellow);
		text.setOpaque(true);
		
		Border border = text.getBorder();
		Border margin = new EmptyBorder(1,1,1,1);
		text.setBorder(new CompoundBorder(border, margin));
		text.setFont(new Font("Serif",Font.PLAIN,16));

		textArea.setCaretPosition(len);
		textArea.insertComponent(text);
		textArea.replaceSelection(cmsg.getTime());
		StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
		textArea.setParagraphAttributes(attr, true);
		textArea.replaceSelection("\n");
	}
	
	public void AppendOtherText(ChatMsg cmsg) {
		AppendIcon(cmsg.getUser().getImg(), cmsg.getUser().getUserName());

		String send_user = cmsg.getUser().getUserName();
		String msg= cmsg.getData().trim(); 		// 앞뒤 blank와 \n을 제거한다.
		String time = cmsg.getTime();
		
		JLabel text= new JLabel(msg);
		text.setBackground(new Color(184,184,184));
		text.setForeground(Color.white);
		text.setOpaque(true);
		int len = textArea.getDocument().getLength();

		Border border = text.getBorder();
		Border margin = new EmptyBorder(1,1,1,1);
		text.setBorder(new CompoundBorder(border, margin));
		text.setFont(new Font("Serif",Font.PLAIN,16));
		
		textArea.setCaretPosition(len);
		textArea.replaceSelection(cmsg.getTime());
		textArea.insertComponent(text);
		textArea.replaceSelection("\n");
	}
	
	public void AppendServerText(ChatMsg cmsg) {
		int len = textArea.getDocument().getLength();
		
		String send_user = cmsg.getUser().getUserName();
		String msg= cmsg.getData().trim(); 		// 앞뒤 blank와 \n을 제거한다.
		
		JLabel text= new JLabel(msg);
		text.setBackground(Color.WHITE);
		text.setOpaque(true);

		Border border = text.getBorder();
		Border margin = new EmptyBorder(1,1,1,1);
		text.setBorder(new CompoundBorder(border, margin));
		text.setFont(new Font("Serif",Font.PLAIN,16));

		textArea.setCaretPosition(len);
		textArea.insertComponent(text);
		StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
		textArea.setParagraphAttributes(attr, true);
		textArea.replaceSelection("\n");
	}
	
	public void showUserList() {
		Memberlist = new UserList(Userlist);
		Memberlist.setVisible(true);
	}
	
	public void setUserList(String name) {
		for(int i=0;i<Userlist.size();i++) {
			UserInform user = (UserInform)Userlist.elementAt(i);
			if(user.getUserName().equals(name)) {
				Userlist.remove(user);
				System.out.println("유저 "+user.getUserName()+"제거");
			}
		}
	}
	
	public void AppendNull() {
		String msg =" ";
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
	}
		
	public int getRoomid() {
		return roomid;
	}
	
	public void changeProfile(String name, ImageIcon img) {
		for(int i=0;i<pl.size();i++) {
			profileLabel p = (profileLabel)pl.elementAt(i);
			if(p.getName().trim().equals(name.trim())) {
				p.setImg(img);
			}
		}
		textArea.repaint();
	}
	
	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date t = new Date();
		String time = " "+format.format(t)+" ";
		
		return time;
	}
}