import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class EmojiPane extends JPanel{
	private EmojiAction action;
	private ChatUI mainView;
	private int roomid;
	
	public EmojiPane(ChatUI mainView, int roomid){
		this.mainView=mainView;
		this.roomid=roomid;
		
		//이미지들
		ImageIcon i1= new ImageIcon("src/Emoji/angry.png");
		Image temp1_1 = i1.getImage();
		Image temp2_1 = temp1_1.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e1 = new JButton(new ImageIcon(temp2_1));
		e1.setSize(70,70);
		e1.setContentAreaFilled(false);
		
		ImageIcon i2= new ImageIcon("src/Emoji/good.png");
		Image temp1_2 = i2.getImage();
		Image temp2_2 = temp1_2.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e2 = new JButton(new ImageIcon(temp2_2));
		e2.setSize(70,70);
		e2.setContentAreaFilled(false);
		
		ImageIcon i3= new ImageIcon("src/Emoji/hello.png");
		Image temp1_3 = i3.getImage();
		Image temp2_3 = temp1_3.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e3 = new JButton(new ImageIcon(temp2_3));
		e3.setSize(70,70);	
		e3.setContentAreaFilled(false);
		
		ImageIcon i4= new ImageIcon("src/Emoji/love.png");
		Image temp1_4 = i4.getImage();
		Image temp2_4 = temp1_4.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e4 = new JButton(new ImageIcon(temp2_4));
		e4.setSize(70,70);
		e4.setContentAreaFilled(false);
		
		ImageIcon i5= new ImageIcon("src/Emoji/smile.jpg");
		Image temp1_5 = i5.getImage();
		Image temp2_5 = temp1_5.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e5 = new JButton(new ImageIcon(temp2_5));
		e5.setSize(70,70);
		e5.setContentAreaFilled(false);
		
		ImageIcon i6= new ImageIcon("src/Emoji/sorry.png");
		Image temp1_6 = i6.getImage();
		Image temp2_6 = temp1_6.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JButton e6 = new JButton(new ImageIcon(temp2_6));
		e6.setSize(70,70);
		e6.setContentAreaFilled(false);
		
		this.setBackground(Color.white);
		
		EmojiAction action1 = new EmojiAction("angry");
		EmojiAction action2 = new EmojiAction("good");
		EmojiAction action3 = new EmojiAction("hello");
		EmojiAction action4 = new EmojiAction("love");
		EmojiAction action5 = new EmojiAction("smile");
		EmojiAction action6 = new EmojiAction("sorry");
		
		e1.addActionListener(action1);
		e2.addActionListener(action2);
		e3.addActionListener(action3);
		e4.addActionListener(action4);
		e5.addActionListener(action5);
		e6.addActionListener(action6);
		
		add(e1);
		add(e2);
		add(e3);
		add(e4);
		add(e5);
		add(e6);
		
		setSize(352, 289);
	}
	
	class EmojiAction implements ActionListener {
		private String cmd;
		public EmojiAction(String cmd) {
			this.cmd=cmd;
		}
		public void actionPerformed(ActionEvent e) {
			ChatMsg emg = new ChatMsg(mainView.UserName, "600", cmd, mainView.myimg);
			emg.setRoomid(roomid);
			emg.setTime(getTime());
			mainView.SendObject(emg);
		}
	}
	
	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date t = new Date();
		String time = " "+format.format(t)+" ";
		
		return time;
	}
}