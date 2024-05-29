import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class Animation extends JFrame {
	private Timer timer = new Timer();
	private Thread updateThread;
	JPanel pnl = new JPanel();
	String thisName;
	Dimension dim = new Dimension();
	Color bg;
	double del = 150;
	int t = 0;
	int tLastUpdate = 0;
	public boolean useThreadingMethod = true;
	Vector<BufferedImage> imgMem = new Vector<>();
	AnimatedVectorField vf;

	public Animation(String thisName, Dimension dim, Color bg, double fps, boolean useThreadingMethod,
			AnimatedVectorField vf) {
		this.vf = vf;
		this.thisName = thisName;
		this.useThreadingMethod = useThreadingMethod;
		this.dim = dim;
		this.bg = bg;
		this.del = 1000 / fps;
		this.setTitle(thisName);
		this.setResizable(false);
		this.setSize(dim.width + 17, dim.height + 40);
		centerFrame();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
	}

	public void centerFrame() {
		this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - this.getSize().width / 2,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - this.getSize().height / 2);

	}

	public void display() throws InterruptedException {
		this.setVisible(true);
		startThreads();
	}

	private void startThreads() throws InterruptedException {
		if (useThreadingMethod) {
			updateThread = new Thread() {
				@Override
				public void run() {
					while (true) {
						if (imgMem.size() > 50) {
							continue;
						}
						updateData(t);
						BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
						Graphics g = img.getGraphics();
						if (bg != null) {
							g.setColor(bg);
							g.fillRect(0, 0, dim.width, dim.height);
						}
						draw((Graphics2D) g, t);
						imgMem.add(img);
					}
				};
			};
			updateThread.start();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (imgMem.size() > 0) {
						t++;
						remove(pnl);
						// Don't send the following line inside the Override for JPanel. The Override
						// function may be called later than when the JPanel object is created, at that
						// time imgMem might've changed
						final BufferedImage img = imgMem.remove(0);
						pnl = new JPanel() {
							@Override
							protected void paintComponent(Graphics g) {
								g.drawImage(img, 0, 0, null);
							}
						};
						pnl.setLocation(0, 0);
						pnl.setSize(dim);
						add(pnl);
						invalidate();
						validate();
						repaint();
					}
				}
			}, 0, (long) del);
		} else {
			while (true) {
				t++;
				updateData(t);
				this.remove(pnl);
				pnl = new JPanel() {

					@Override
					protected void paintComponent(Graphics g) {
						if (bg != null) {
							g.setColor(bg);
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
						}
						draw((Graphics2D) g, t);
					}

				};
				pnl.setLocation(0, 0);
				pnl.setSize(dim);
				this.add(pnl);
				this.invalidate();
				this.validate();
				this.repaint();
				Thread.sleep((int) del);
			}
		}
	}

	public abstract void draw(Graphics2D g, int t);

	public abstract void updateData(int t);

	public void unpause() {
		try {
			startThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}