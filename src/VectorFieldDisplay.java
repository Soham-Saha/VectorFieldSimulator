//STATUS UPDATE: Read the file "others/DynamicCompilation.txt"

//STATUS: base is ready, revise and restart.
//Keeping inactive as I don't think I'll get time

/*-import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VectorFieldDisplay {
	static boolean validScript = false;
	static String scriptInputMemory = "return p.multiply(-1);";
	static String scriptInput = scriptInputMemory;
	static Method m;
	static RuntimeCompiler r = new RuntimeCompiler();

	@SuppressWarnings("serial")
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		updateUserInputMethod();
		AnimatedVectorField vf = new AnimatedVectorField() {
			@Override
			protected Vec2 getVelocityVector(Vec2 p, double t) {
				try {
					return (Vec2) m.invoke(null, p, t);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			void frameInit() {
				Dimension dim = anim.getSize();
				anim.setSize(dim.width + 300, dim.height);
				anim.centerFrame();

				JTextArea ta = new JTextArea();
				ta.setLocation(dim.width, 0);
				ta.setSize(200, 200);
				ta.setLineWrap(true);
				ta.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						System.out.println(arg0);
					}
				});
				JScrollPane sc = new JScrollPane(ta);
				sc.setSize(ta.getSize());
				sc.setLocation(ta.getLocation());
				anim.add(sc);

			}
		};
		vf.displayVectorField();
	}

	private static void updateUserInputMethod() throws NoSuchMethodException, SecurityException {
		try {
			r.addClass("Runner",
					"public class Runner{public static Vec2 velocity(Vec2 p,double t){" + scriptInput + "}}");
			r.compile();
			Class<?> runner = r.getCompiledClass("Runner");
			m = runner.getMethod("velocity", Vec2.class, double.class);
			scriptInputMemory = scriptInput;
		} catch (Exception e) {
			r.addClass("Runner",
					"public class Runner{public static Vec2 velocity(Vec2 p,double t){" + scriptInputMemory + "}}");
			r.compile();
			Class<?> runner = r.getCompiledClass("Runner");
			m = runner.getMethod("velocity", Vec2.class, double.class);

		}

	}

}*/