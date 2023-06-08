import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SegmFrame extends JFrame {
	String filename = "segm_2.png";
	String title = "Segmentation (Choose one of the three methods)";
	JPanel cotrolPanel;
	JPanel imagePanelLeft;
	JPanel imagePanelRight;
	JButton btnShow;
	JButton btnSegm;
	JButton btnNext;
	JButton btnPrev;
	int[][] new_data;
	int[][][] data;
	int height;
	int width;
	static BufferedImage img = null;
	int find_count = 1;
	int count = 1;

	SegmFrame() {
		setTitle(title);
		setLayout(null);
		btnShow = new JButton("Show Original Image");
		btnSegm = new JButton("Segment");
		btnNext = new JButton("Next Object");
		btnPrev = new JButton("Prev Object");
		cotrolPanel = new JPanel();
		cotrolPanel.setBounds(0, 0, 1500, 200);
		getContentPane().add(cotrolPanel);
		cotrolPanel.add(btnShow);
		cotrolPanel.add(btnSegm);
		cotrolPanel.add(btnNext);
		cotrolPanel.add(btnPrev);
		imagePanelLeft = new ImagePanel();
		imagePanelLeft.setBounds(0, 120, 700, 700);
		getContentPane().add(imagePanelLeft);
		imagePanelRight = new ImagePanel();
		imagePanelRight.setBounds(750, 120, 700, 700);
		getContentPane().add(imagePanelRight);

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadImg();
				Graphics g = imagePanelLeft.getGraphics();
				g.drawImage(img, 0, 0, null);
			}
		});

		btnSegm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (data[i][j][0] != 255 && new_data[i][j] == 0) {
							findAround(i, j);
							find_count++;
						}
					}
				}
				showElement();
			}
		});

		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				count += 2;
				showElement();
			}
		});

		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				count -= 2;
				showElement();
			}
		});
	}

	void loadImg() {
		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		new_data = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Util.getR(rgb);
				data[y][x][1] = Util.getG(rgb);
				data[y][x][2] = Util.getB(rgb);
				new_data[y][x] = 0;
			}
		}
	}

	public void findAround(int y, int x) {
		if (data[y][x][0] == 255 || new_data[y][x] == find_count) {
			return;
		}
		new_data[y][x] = find_count;
		for (int i = y - 1; i <= y + 1; i += 2) {
			for (int j = x - 1; j <= x + 1; j += 2) {
				int y_tmp = Util.checkImageBounds(i, height);
				int x_tmp = Util.checkImageBounds(j, width);
				findAround(y_tmp, x_tmp);
			}
		}

	}

	public void showElement() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (new_data[i][j] == count || new_data[i][j] == count + 1) {
					img.setRGB(j, i, Util.makeColor(0, 0, 0));
				} else {
					img.setRGB(j, i, Util.makeColor(255, 255, 255));
				}
			}
		}
		Graphics g = imagePanelRight.getGraphics();
		g.drawImage(img, 0, 0, null);
	}

	public static void main(String[] args) {
		SegmFrame frame = new SegmFrame();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
