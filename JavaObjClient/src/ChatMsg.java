// ChatMsg.java ä�� �޽��� ObjectStream ��.
import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code; 	// 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image 500:ģ���߰�
	private String data;
	private int roomid;
	private UserInform user;
	private ImageIcon img;		//÷�� �̹���
	private String time;
	
	public ChatMsg(String id, String code, String msg, ImageIcon profile) {
		setUser(new UserInform(id,profile));
		this.code = code;
		this.data = msg;
	}

	public String getCode() {
		return code;
	}

	public String getData() {
		return data;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	public UserInform getUser() {
		return user;
	}

	public void setUser(UserInform user) {
		this.user = user;
	}

	public int getRoomid() {
		return roomid;
	}

	public void setRoomid(int roomid) {
		this.roomid = roomid;
	}
	
	//�̹��� ÷��
	public void setImg(ImageIcon img) {
		this.img = img;
	}
	
	public ImageIcon getImg() {
		return img;
	}
	
	public void setTime(String time) {
		this.time=time;
	}
	
	public String getTime() {
		return time;
	}
}