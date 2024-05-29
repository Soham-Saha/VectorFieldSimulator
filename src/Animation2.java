/*-import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import processing.core.PApplet;

public abstract class Animation2 extends PApplet {
	boolean running = true;
	String thisName;
	Dimension dim = new Dimension();
	Color bg;
	double del = 150;
	static int t = 0;

	public Animation2(String thisName, Dimension dim, Color bg, double fps) {
		this.thisName = thisName;
		this.dim = dim;
		this.bg = bg;
		this.del = 1000 / fps;
		/*-
		this.setSize(dim.width + 17, dim.height + 40);
		centerFrame();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);*
}

public void display(){

}

public abstract void draw(Graphics2D g,int t);

public abstract void updateData(int t);

}

class MySketch extends PApplet {

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void draw() {
		background(64);
		ellipse(mouseX, mouseY, 20, 20);
	}

	public static void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "MySketch" };
		PApplet.main(appletArgs);
	}
}*/