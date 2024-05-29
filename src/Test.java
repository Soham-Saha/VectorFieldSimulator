public class Test {

	public static void main(String[] args) {
		try {
			if (args.length >= 1) {
				AnimatedVectorField.getVectorField((DemoVectorFields) DemoVectorFields.class.getField(args[0].toUpperCase()).get(null)).displayVectorField();
			} else {
				AnimatedVectorField vf = AnimatedVectorField.getVectorField(DemoVectorFields.UNDULATING);
				vf.displayVectorField();
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO: handle exception
		}

		// vf.streamFramesToFolder(new File("C:\\users\\santanu\\desktop\\files"),
		// 10000);

		/*-AnimatedVectorField vf = AnimatedVectorField.getVectorField(DemoVectorFields.GEMINI);
		vf.velocityBasedColoring = true;
		vf.velMultiplier = 0.5;
		vf.memSize *= 1.5;
		vf.forceSpeed = true;
		vf.displayVectorField();*/
		/*-vf.bgColor = Color.black;
		vf.stroke = new BasicStroke(2);
		vf.drawLines = true;*/
		/*-vf.bgColor = Color.getHSBColor(148f / 360, 0.73f, 1f);
		vf.defaultColour = Color.getHSBColor(214f / 360, 0.91f, 0.88f);
		vf.numPoints *= 0.5;
		vf.stroke = new BasicStroke(1.5f);
		vf.drawLines = true;
		vf.memSize *= 1.5;
		vf.velMultiplier = 0.7;
		vf.shiftProb = 0.1;*/
	}

	public static AnimatedVectorField boid() {
		AnimatedVectorField vf = new AnimatedVectorField() {

			@Override
			protected Vec2 getVelocityVector(Vec2 p, double t) {
				// Move towards COM
				// Approximate group velocity
				// Stay at minimum distance
				if (getMemory().size() < 1) {
					return new Vec2(0, 0);
				}
				Vec2 centerOfMass = new Vec2(0, 0);
				Vec2 centerOfMassVelocity = new Vec2(0, 0);
				Vec2 push = new Vec2(0, 0);
				Vec2[] pts = getPoints();
				for (int i = 0; i < numPoints; i++) {
					if (pts[i].equals(p)) {
						continue;
					}
					centerOfMass = centerOfMass.add(pts[i]);
					if (pts[i].velMem != null) {
						centerOfMassVelocity = centerOfMassVelocity.add(pts[i].velMem);
					}
					if (p.minus(pts[i]).lengthSq() < 2500) {
						push = push.add(p.minus(pts[i]).getUnitVector().multiply(50 - p.minus(pts[i]).length()));
					}
				}
				centerOfMass = centerOfMass.multiply(1 / ((double) numPoints - 1));
				centerOfMassVelocity = centerOfMassVelocity.multiply(1 / ((double) numPoints - 1));
				return centerOfMass.minus(p).multiply(1).add(centerOfMassVelocity.multiply(1)).add(p.multiply(-0.5)).add(push.multiply(1));
			}

		};
		vf.numPoints = 100;
		vf.memSize = 20;
		vf.velocityBasedColoring = false;
		vf.shiftProb = 0;
		vf.rememberVelocity = true;
		vf.velMultiplier = 1;
		vf.useSeparateThread = true;
		return vf;

	}

}
