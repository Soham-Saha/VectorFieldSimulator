import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

@SuppressWarnings("serial")
class Sliders extends JFrame {
	public boolean flagged = false;
	Class<?> classObj;

	public Sliders(Class<?> classObj) {
		this.classObj = classObj;
	}

	private JSlider createSlider(String varName) {
		JSlider slider = new JSlider(0, 1000, 0);
		slider.setBorder(BorderFactory.createTitledBorder(varName));
		slider.addChangeListener(e -> {
			try {
				classObj.getMethod("update_" + varName, new Class[] { double.class }).invoke(null,
						(double) slider.getValue() / 1000);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				e1.printStackTrace();
			}
		});
		slider.addChangeListener(null);
		return slider;
	}

	private void createAndShowSliders(ArrayList<String> varNames) {
		JPanel sliderPanel = new JPanel(new GridLayout(varNames.size(), 1, 5, 5));
		for (String x : varNames) {
			JSlider sliderA = createSlider(x);
			sliderPanel.add(sliderA);
		}
		add(sliderPanel, BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void display() {
		ArrayList<String> list = new ArrayList<String>();
		for (Method x : classObj.getMethods()) {
			if (x.getName().startsWith("update_")) {
				list.add(x.getName().substring(7));
			}
		}
		createAndShowSliders(list);

	}
}

public class Test2 {
	static AnimatedVectorField vf;

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		vf = AnimatedVectorField.getVectorField(DemoVectorFields.GEMINI);
		new Sliders(Test2.class).display();
		vf.displayVectorField();

		// TODO: every major update needs vf.killAndRestart() How to implement this?
	}

	public static void update_saturation(double x) {
		vf.saturation = 1 - (float) x;
	}

	public static void update_velocityBasedColoring(double x) {
		vf.velocityBasedColoring = x < 0.5;
	}

	public static void update_rotate(double x) {
		vf.rotateVectorField = Math.PI * 2 * x;
	}

	public static void update_shift(double x) {
		vf.shiftProb = x;
	}

	public static void update_numPoints(double x) {
		// AnimatedVF has many function that start in prallel. Need to check if they
		// are completed too, before unlocking
		// Every threadable part increases "heyWait" by 1 when it starts and decrements
		// it when it is finished, doesn't start if "needtopause". Doesn't work, heyWait
		// is a shared resource, maybe race conditions are happening
		System.out.println("locking");
		vf.waitTillPaused();
		System.out.println("unlocking");
		System.out.println("updating values");
		vf.numPoints = (int) (x * 10000);
		System.out.println("restarting");
		vf.restart();
		vf.anim.unpause();
	}
}