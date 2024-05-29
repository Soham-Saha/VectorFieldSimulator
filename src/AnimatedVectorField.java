
//Inspired from https://anvaka.github.io/fieldplay/
/**
 * @author Soham Saha
**/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public abstract class AnimatedVectorField implements Serializable {
	public Vec2[] pts;
	private double velSqMax;
	private double velSqMin;
	private ArrayList<Vec2[]> memory;// Each Vec2[] is the past history of 1 particle <---- THIS MAY NOT BE CORRECT.
										// I
										// think each vec2 is one memory snap of all particles
	private boolean useThreadingMethod; // Used to change the value of the anim.useBetaThreadingMethod
	private boolean needToPause = false;
	private Random rand = new Random();// Changed all the Math.random()s

	double dt;// in seconds
	int numPoints;
	int width;
	int height;
	double shiftProb;
	double h;// h value used in RungeKutta
	int memSize;
	double speedScale;
	Color bgColor;
	Color defaultColour;// Used when there is no velocityBasedColoring
	boolean velocityBasedColoring;
	boolean drawLines;
	boolean drawPoints;
	Animation anim;
	float saturation;
	float brightness;
	double randomnessBorderScale;
	boolean useSeparateThread;// TODO: if you really want to use threads, remove links between t for Animation
								// class extensions,etc. Threads also don't support simultaneous streaming. When
								// multiple instances run in parallel, some fields of Animation seem to be
								// shared. HEY NEW UPDATE: It appears that the variable t was made static in
								// Animation. Changed to non-static now, all problems should be solved. HEY
								// ANOTHER NEW UPDATE: With useBetaThreadingMethod on, most probably a new
								// thread is created, so setting this to true is overkill, both in terms of
								// performance and style
	Stroke stroke;// You've forgotten to set drawLines to true AGAIN... Aaaargh
	boolean forceSpeed; // forces rudimentary velocity prediction approach
	double velMultiplier;
	boolean rememberVelocity;
	boolean drawNet; // currently in beta
	double rotateVectorField; // problems with velBasedColoring. UPDATE: suddenly appears that all problems
								// have vanished. Don't know how or why

	public AnimatedVectorField() {
		initDefaults();
	}

	public AnimatedVectorField(boolean useLegacyNonThreading) {
		initDefaults();
		useThreadingMethod = !useLegacyNonThreading;
	}

	public Vec2[] getPoints() {
		return pts;
	}

	public ArrayList<Vec2[]> getMemory() {
		return memory;
	}

	public void initDefaults() {
		dt = 0.01;
		numPoints = 10000;
		width = 500;
		height = 500;
		shiftProb = 0.02;
		h = 0.001;
		memory = new ArrayList<>();
		memSize = 30;
		speedScale = 1;
		bgColor = new Color(19, 41, 79);
		defaultColour = Color.cyan;
		velocityBasedColoring = true;
		drawLines = false;
		drawPoints = true;
		saturation = 1f;
		brightness = 1f;
		randomnessBorderScale = 1.2;
		useSeparateThread = false;
		stroke = null;
		velMultiplier = 1;
		rememberVelocity = false;
		drawNet = false;
		forceSpeed = false;
		rotateVectorField = 0;
		useThreadingMethod = true;
	}

	public void frameInit() {
		// Override if needed
	}

	// TODO: streaming stops when frame is minimised
	public void streamFramesToFolder(File file, int tillWhat) throws IOException {
		if (file.isDirectory()) {
			System.out.println("Streaming to " + file + " ...");
			pts = new Vec2[numPoints];
			initializePoints();
			updateVelocitySqMaxMin();
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for (int t = 0; t < tillWhat; t++) {
				updateVF(t);
				Graphics2D gImg = (Graphics2D) img.getGraphics();
				gImg.setColor(bgColor);
				gImg.fillRect(0, 0, width, height);
				drawOver(gImg, t);
				ImageIO.write(img, "png",
						new File(file.getAbsolutePath() + "/" + String.format("%7s", t).replace(' ', '0') + ".png"));

				System.out.println((double) t * 100 / tillWhat + "% : " + t);

			}
		} else {
			System.err.println(file + " is not a directory.");
		}
	}

	/**
	 * return the new thread created if useSeparateThread is active. Else returns
	 * null
	 */
	public Thread displayVectorField() {
		Thread x = null;
		if (useSeparateThread) {
			x = new Thread() {
				@Override
				public void run() {
					try {
						threadRunMethod();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			};
			x.start();
		} else {
			try {
				threadRunMethod();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return x;

	}

	protected void threadRunMethod() throws InterruptedException {
		pts = new Vec2[numPoints];
		initializePoints();
		anim = createAnimation();
		frameInit();
		updateVelocitySqMaxMin(); // TODO: Also call whenever viewport is changed.
		anim.display();
	}

	public void restart() {
		pts = new Vec2[numPoints];
		initializePoints();
		frameInit();
		updateVelocitySqMaxMin(); // TODO: Also call whenever viewport is changed.
	}

	private Animation createAnimation() {
		return new Animation("VectorFieldSimulator: Soham Saha", new Dimension(width, height), bgColor, speedScale / dt,
				this.useThreadingMethod, this) {

			@Override
			public void updateData(int t) {
				if (!needToPause) {
					updateVF(t);
				}
			}

			@Override
			public void draw(Graphics2D g, int t) {
				if (!needToPause) {
					drawOver(g, t);
				}
			}
		};
	}

	private void updateVF(int t) {
		IntStream.range(0, numPoints).parallel().unordered().forEach(i -> {
			if (rand.nextDouble() <= shiftProb) {
				pts[i] = shiftToRandomPosition();
				if (memory.size() >= 1)
					memory.get(memory.size() - 1)[i] = new Vec2(Double.NaN, Double.NaN);

			} else {
				if (rememberVelocity) {
					// TODO: Precompute the velocities for efficiency
					pts[i].velMem = getVelocity(pts[i], t);
				}
				pts[i] = predict(t * dt, pts[i], (t + 1) * dt);
			}
		});
		if (memory.size() > memSize) {
			memory.remove(0);
		}
		memory.add(pts.clone());

	}

	public void initializePoints() {
		for (int i = 0; i < numPoints; i++) {
			pts[i] = new Vec2(rand.nextDouble() * width - width / 2, rand.nextDouble() * height - height / 2)
					.multiply(randomnessBorderScale);
		}
	}

	// TODO: shift to viewport
	protected Vec2 shiftToRandomPosition() {
		return new Vec2(randomnessBorderScale * (rand.nextDouble() * width - width / 2),
				randomnessBorderScale * (rand.nextDouble() * height - height / 2));// Why scale? It scales the
		// randomness port, so that the low density
		// border is not seen
	}

	@SuppressWarnings("unchecked")
	protected void drawOver(Graphics2D g, int t) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Text antialias not needed here till yet, but who knows?
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (stroke != null) {
			g.setStroke(stroke);
		}
		ArrayList<Vec2[]> tmpMem = (ArrayList<Vec2[]>) memory.clone();
		int mem = tmpMem.size();
		DrawingCrumb[][] data = new DrawingCrumb[mem - 1][numPoints];
		// TODO: new idea = give sequence of colors to each pixel that will actually get
		// colored (but how to implement for line?). Then color that pixel using the
		// sequence ordering by alpha. Run the
		// previous sentence in parallel over all pixels that will get colored.
		// Colouring order=(a) Memory order (ascending) (b) particle coloring order has
		// to remain constant
		IntStream.range(0, mem - 1).parallel().unordered()
				.forEach(j -> IntStream.range(0, numPoints).parallel().unordered().forEach(i -> {
					Vec2 a = tmpMem.get(j)[i];
					Vec2 b = drawLines ? tmpMem.get(j + 1)[i] : new Vec2();
					if (!(Double.isNaN(a.x) || Double.isNaN(a.y) || Double.isNaN(b.x) || Double.isNaN(b.y))) {
						DrawingCrumb crumb = new DrawingCrumb();
						if (velocityBasedColoring) {
							crumb.col = getColor(getNormalizedAbsoluteVelocitySq(a, t * dt),
									(float) (((double) j / (double) mem)));
						} else {
							crumb.col = new Color(defaultColour.getRed(), defaultColour.getGreen(),
									defaultColour.getBlue(), (int) (180 * ((double) j / (double) mem)));
						}
						if (drawPoints) {
							crumb.rectX = width / 2 + (int) a.x;
							crumb.rectY = height / 2 - (int) a.y;
						}
						if (drawLines) {
							crumb.lineAX = width / 2 + (int) a.x;
							crumb.lineAY = height / 2 - (int) a.y;
							crumb.lineBX = width / 2 + (int) b.x;
							crumb.lineBY = height / 2 - (int) b.y;
						}
						data[j][i] = crumb;
					}
				}));
		for (int j = 0; j < tmpMem.size() - 1; j++) {
			for (int i = 0; i < numPoints; i++) {
				DrawingCrumb crumb = data[j][i];
				if (crumb != null) {
					g.setColor(crumb.col);
					if (drawPoints) {
						g.fillRect(crumb.rectX, crumb.rectY, 1, 1);
					}
					if (drawLines) {
						g.drawLine(crumb.lineAX, crumb.lineAY, crumb.lineBX, crumb.lineBY);
					}
				}
			}
		}
		if (drawNet) {
			Vec2[] ptsNow = tmpMem.get(tmpMem.size() - 1);

			IntStream.range(0, ptsNow.length - 1).parallel().unordered().forEach(i -> {
				Vec2 x = ptsNow[i];
				g.setColor(defaultColour);
				Vec2 closest = getClosest(x, ptsNow);
				g.drawLine((int) x.x + width / 2, width / 2 - (int) x.y, (int) closest.x + height / 2,
						height / 2 - (int) closest.y);
			});
		}

	}

	public Vec2 getClosest(Vec2 x, Vec2[] arr) {
		// Using cheap trick to make closest and dist effectively final by using 1
		// element array;
		Vec2[] closest = { null };
		double dist[] = { Double.POSITIVE_INFINITY };
		IntStream.range(0, arr.length - 1).parallel().unordered().forEach(i -> {
			Vec2 pt = arr[i];
			if (!pt.equals(x)) {
				double v = Vec2.distanceSq(x, pt);
				if (v < dist[0]) {
					dist[0] = v;
					closest[0] = pt;
				}
			}
		});
		return closest != null ? closest[0] : new Vec2(0, 0);
	}

	// TODO: call whenever viewport is changed or initialized
	private void updateVelocitySqMaxMin() {
		// TODO: Should be viewport dependent
		// TODO: should be time dependent, so call at each frame update, not only
		// viewport update
		// TODO: add time dependency, defaulted to 0 as of now
		velSqMin = Double.POSITIVE_INFINITY;
		velSqMax = Double.NEGATIVE_INFINITY;
		for (int i = -width / 2; i < width / 2; i++) {
			for (int j = -height / 2; j < height / 2; j++) {
				double v = getAbsoluteVelocitySq(new Vec2(i, j), 0);
				if (!Double.isNaN(v) && Double.isFinite(v)) {
					if (v < velSqMin) {
						velSqMin = v;
					}
					if (v > velSqMax) {
						velSqMax = v;
					}
				}
			}
		}
	}

	// It takes in velocity squared to reduce previous calculations.
	private Color getColor(double velocitySq, float alpha) {
		if (!Double.isNaN(velocitySq)) {
			Color c = Color.getHSBColor((float) (1 - Math.sqrt(velocitySq)) * 200f / 360 + 10f / 360, saturation,
					brightness);
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (alpha * 255));
		} else {
			return bgColor;
		}
	}

	// Using serialization-deserialization
	public AnimatedVectorField deepCopy() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		oos.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object deserializedObject = ois.readObject();
		ois.close();
		return (AnimatedVectorField) deserializedObject;
	}

	// Modified from
	// https://www.geeksforgeeks.org/runge-kutta-4th-order-method-solve-differential-equation/
	// with some good changes by Google Bard
	// TODO: Both methods appear to be problematic for non-static vector fields
	// (rule based velocity selection). While RK4 was never designed for that, the
	// other method should work in theory. Well, theory doesn't always work...
	Vec2 predict(double t0, Vec2 p0, double t) {
		int n = (int) ((t - t0) / h);
		Vec2 k1, k2, k3, k4;
		Vec2 p = (Vec2) p0.clone();
		if (!forceSpeed) {
			// RK4 implementation
			for (int i = 1; i <= n; i++) {
				k1 = f(p, t0 + (i - 1) * h);
				k2 = f(new Vec2(p.x + 0.5 * k1.x, p.y + 0.5 * k1.y), t0 + 0.5 * h + (i - 1) * h);
				k3 = f(new Vec2(p.x + 0.5 * k2.x, p.y + 0.5 * k2.y), t0 + 0.5 * h + (i - 1) * h);
				k4 = f(new Vec2(p.x + k3.x, p.y + k3.y), t0 + h + (i - 1) * h);
				p.x = p.x + (k1.x + 2 * k2.x + 2 * k3.x + k4.x) / 6;
				p.y = p.y + (k1.y + 2 * k2.y + 2 * k3.y + k4.y) / 6;
				t0 = t0 + h;
			}
		} else {
			for (int i = 1; i <= n; i++) {
				p = p.add(getVelocity(p, t0).multiply(h));
				t0 += h;
			}
		}
		return p;
	}

	// Helper function for RK4
	private Vec2 f(Vec2 p, double t) {
		Vec2 v = getVelocity(p, t);
		return new Vec2(h * v.x, h * v.y);
	}

	protected abstract Vec2 getVelocityVector(Vec2 p, double t);

	private Vec2 getVelocity(Vec2 p, double t) {
		Vec2 vel = null;
		if (rotateVectorField == 0)
			vel = getVelocityVector(p, t).multiply(velMultiplier);
		else
			vel = getVelocityVector(p.rotate(-rotateVectorField), t).multiply(velMultiplier).rotate(rotateVectorField);
		if (Double.isInfinite(vel.lengthSq())) {
			vel = new Vec2(0, 0);
		}
		return vel;
	}

	private double getAbsoluteVelocitySq(Vec2 p, double t) {
		return getVelocity(p, t).lengthSq();
	}

	private double getNormalizedAbsoluteVelocitySq(Vec2 p, double t) {
		if (velSqMax == velSqMin) {
			return 0;
		}
		double k = (getAbsoluteVelocitySq(p, t) - velSqMin) / (velSqMax - velSqMin);
		return Math.max(0, Math.min(k, 1));
	}

	public static AnimatedVectorField getVectorField(DemoVectorFields vf) {
		return DemoVectorFields.getVectorField(vf);
	}

	public void waitTillPaused() {
		needToPause = true;

		needToPause = false;
	}

}

class DrawingCrumb {
	Color col;
	int rectX;
	int rectY;
	int lineAX;
	int lineAY;
	int lineBX;
	int lineBY;
}