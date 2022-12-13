import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class UserInform implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private ImageIcon img;
	
	public UserInform(String username, ImageIcon img){
		this.username = username;
		setImg(img);
	}
	public void setUserName(String username) {
		this.username=username;
	}
	public String getUserName() {
		return username;
	}
	public ImageIcon getImg() {
		return img;
	}
	
	public void setImg(ImageIcon img) {
		this.img = img;
	}
}