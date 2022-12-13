import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class profileLabel extends JLabel{
	private ImageIcon img;
	private String name;
	profileLabel(ImageIcon img, String name){
		this.img=img;
		this.name=name;
		
		Image temp1 = img.getImage();
		Image temp2 = temp1.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		
		this.setSize(160,40);
		this.setBackground(new Color(186,206,224));
		this.setLayout(null);
		
		this.setIcon(new ImageIcon(temp2));
		this.setText("["+name+"]");
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				profile p = new profile(name, img);
				p.setVisible(true);
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	public void setImg(ImageIcon img)
	{
		Image temp1 = img.getImage();
		Image temp2 = temp1.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		this.setIcon(new ImageIcon(temp2));
	}
}