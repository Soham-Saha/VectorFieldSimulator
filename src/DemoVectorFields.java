
/**
 * @author Soham Saha
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

enum DemoVectorFields {

	ANNULUS, SINES, PIPES, TILING, INTERACTIVE, OPEN_SIMPLEX_NOISE_1, OPEN_SIMPLEX_NOISE_2, OPEN_SIMPLEX_NOISE_3, OPEN_SIMPLEX_NOISE_4, LEADERSHIP, REFLECTING_POOL, SAURONS_EYE, LEADERSHIP_MODIFIED,
	GEMINI, OPEN_SIMPLEX_NOISE_3_REIMAGINED, UNDULATING, FUZZY_DIPOLE, SMOKE, SMOKE_2;

	@SuppressWarnings("serial")
	protected static AnimatedVectorField getVectorField(DemoVectorFields defvf) {

		switch (defvf) {
		case ANNULUS: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					double r = 120;
					double T = 2;
					return new Vec2(-p.x + p.x * Math.cos(T) - p.y * Math.sin(T), -p.y + p.y * Math.cos(T) + p.x * Math.sin(T)).apply(k -> k * (1 - r / p.length()));
				}
			};
			return vf;
		}
		case SINES: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					return p.apply(k -> 200 * Math.sin(k * 3.14 / 130));
				}
			};
			return vf;
		}
		case PIPES: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					return p.flip().apply(k -> -200. * (Math.floor(k / 50) % 2));
				}
			};
			vf.velocityBasedColoring = false;
			vf.velMultiplier = 0.4;
			return vf;
		}
		case TILING: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					return p.flip().apply(k -> -200 * (Math.floor(Math.abs(k) / 75) % 2) + 100);
				}
			};
			vf.velocityBasedColoring = false;
			return vf;
		}
		case INTERACTIVE: {
			// VFSim is not actually meant for interactive behavior. Need to
			// use the legacy non-threading approach for proper behavior
			// during interaction
			AnimatedVectorField vf = new AnimatedVectorField(true) {
				Point ptNew = new Point(width / 2, height / 2 + 20);

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					Vec2 mouse = new Vec2(ptNew.x - 8, ptNew.y - 32);
					mouse.x = -mouse.x + width / 2;
					mouse.y = -height / 2 + mouse.y;
					Vec2 vel = new Vec2(-p.x - mouse.x, -p.y - mouse.y);
					return vel;
				}

				@Override
				public void frameInit() {
					anim.addMouseMotionListener(new MouseMotionAdapter() {

						@Override
						public void mouseMoved(MouseEvent e) {
							ptNew = e.getPoint();
						}

						@Override
						public void mouseDragged(MouseEvent e) {

						}
					});

				}
			};
			vf.drawLines = true;
			vf.numPoints *= 0.05;
			vf.memSize = 20;
			return vf;
		}
		case OPEN_SIMPLEX_NOISE_1: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double ang = OpenSimplex2S.noise2(seed, p.x / 200, p.y / 200) * Math.PI;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 200 * k);
				}
			};
			vf.velocityBasedColoring = false;
			return vf;
		}
		case OPEN_SIMPLEX_NOISE_2: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seedAng = 947497885;
					long seedVel = 531397972;
					double ang = OpenSimplex2S.noise3_Fallback(seedAng, p.x / 200, p.y / 200, t / 2) * Math.PI;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> OpenSimplex2S.noise2(seedVel, p.x / 200, p.y / 200) * 200 * k);
				}
			};
			return vf;
		}
		case OPEN_SIMPLEX_NOISE_3_REIMAGINED: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					// Some clever angle manipulation here (remember, noise -> [-1,1]):
					// [Gives horizontally inwards near left and right edges, but upwards towards
					// the center]
					double ang = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 200, p.y / 200, t % Double.MAX_VALUE) * Math.PI / 4
							+ (p.x < 0 ? Math.PI / 2 * Math.exp(p.x / 100) : Math.PI - Math.PI / 2 * Math.exp(-p.x / 100));
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 300 * k);
				}
			};
			vf.velocityBasedColoring = false;
			vf.memSize = 5;
			Color dc = new Color(116, 247, 204);
			vf.defaultColour = dc;
			vf.bgColor = dc.darker().darker().darker().darker();
			vf.rotateVectorField = -3 * Math.PI / 8;
			vf.drawLines = true;
			vf.shiftProb *= 1.5;
			vf.speedScale = 0.25;
			vf.width = 720;
			vf.height = 550;
			vf.forceSpeed = true;
			return vf;
		}
		case OPEN_SIMPLEX_NOISE_3: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double ang = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 200, p.y / 200, t % Double.MAX_VALUE) * Math.PI;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 100 * k);
				}
			};
			vf.velocityBasedColoring = false;
			vf.numPoints *= 1.5;
			vf.memSize = 10;
			Color defc = new Color(116, 247, 204);
			vf.defaultColour = defc;
			vf.bgColor = defc.darker().darker().darker();
			return vf;
		}
		case OPEN_SIMPLEX_NOISE_4: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seedAng = 947497885;
					long seedVel = 531397972;
					double ang = OpenSimplex2S.noise3_Fallback(seedAng, p.x / 200, p.y / 200, t % Double.MAX_VALUE) * Math.PI;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> (OpenSimplex2S.noise3_Fallback(seedVel, p.x / 200, p.y / 200, t % Double.MAX_VALUE) * 50 + 150) * k);
				}
			};
			vf.numPoints *= 0.75;
			vf.memSize = 10;
			return vf;
		}
		case UNDULATING: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double noise = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 200, p.y / 200, t % Double.MAX_VALUE);
					double noise2 = OpenSimplex2S.noise2(seed + 1L, 0, (t / 3) % Double.MAX_VALUE) * 50 + 160;
					double d = 1 / (1 + Math.exp(-(p.lengthSq() - (noise2 * noise2)) / 3000));
					double ang = (1 - d) * (noise * Math.PI / 4 + Math.atan2(p.y, p.x) + Math.PI / 4) + d * (noise * Math.PI / 4 + Math.atan2(p.y, p.x) + 3 * Math.PI / 4);
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 250 * k);
				}
			};
			vf.velocityBasedColoring = false;
			vf.memSize = 15;// Can also try mem=5 and drawLines=true
			vf.defaultColour = new Color(92, 1, 1);
			vf.bgColor = new Color(252, 252, 229);
			vf.shiftProb *= 1.5;
			vf.speedScale = 0.25;
			vf.width = 700;
			vf.height = 500;
			vf.forceSpeed = true;
			return vf;
		}
		case FUZZY_DIPOLE: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double noise = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 100, p.y / 100, t % Double.MAX_VALUE);
					double d = 1 / (1 + Math.exp(-(p.x) / 300));
					double ang1 = Math.atan2(p.y, p.x - 150) + noise * Math.PI / 6;
					double ang2 = Math.atan2(p.y, p.x + 150) + Math.PI + noise * Math.PI / 6;
					return new Vec2(Math.cos(ang1), Math.sin(ang1)).multiply(d).add(new Vec2(Math.cos(ang2), Math.sin(ang2)).multiply(1 - d)).apply(k -> 250 * k);
				}

				@Override
				protected Vec2 shiftToRandomPosition() {
					return new Vec2(Math.random() * width / 2 * (Math.random() > 0.7 ? -1 : 1), Math.random() * height - height / 2).multiply(randomnessBorderScale);
				};
			};
			vf.velocityBasedColoring = false;
			vf.memSize = 15;// Can also try mem=5 and drawLines=true
			vf.defaultColour = new Color(92, 1, 1);
			vf.bgColor = new Color(252, 252, 229);
			vf.shiftProb *= 1.5;
			vf.speedScale = 0.25;
			vf.width = 700;
			vf.height = 500;
			vf.forceSpeed = true;
			return vf;
		}
		case SMOKE: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double noise = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 100, p.y / 100, t % Double.MAX_VALUE);
					double ang = (noise + 1) * Math.PI / 2;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 250 * k);
				}

				@Override
				protected Vec2 shiftToRandomPosition() {
					// double ang = Math.random() * 2 * Math.PI;
					// return new Vec2(Math.cos(ang), Math.sin(ang)).multiply(20 *
					// Math.random()).minus(new Vec2(0, 0));
					return new Vec2((Math.random() * width - width / 2) * 0.75, -height / 2 + Math.random());
				};

				@Override
				public void initializePoints() {
					for (int i = 0; i < numPoints; i++) {
						pts[i] = shiftToRandomPosition();
					}
				};
			};
			vf.velocityBasedColoring = false;
			vf.memSize = 15;// Can also try mem=5 and drawLines=true
			vf.defaultColour = new Color(92, 1, 1);
			vf.bgColor = new Color(252, 252, 229);
			vf.shiftProb *= 1.5;
			vf.speedScale = 0.25;
			vf.width = 700;
			vf.height = 500;
			vf.forceSpeed = true;
			vf.numPoints /= 2;
			return vf;
		}
		case SMOKE_2: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					long seed = 9474978858L;
					double noise = OpenSimplex2S.noise3_ImproveXY(seed, p.x / 100, p.y / 100, t % Double.MAX_VALUE);
					double ang = (noise + 1) * Math.PI / 4;
					return new Vec2(Math.cos(ang), Math.sin(ang)).apply(k -> 250 * k);
				}

				@Override
				protected Vec2 shiftToRandomPosition() {
					double ang = Math.random() * 2 * Math.PI;
					return new Vec2(Math.cos(ang), Math.sin(ang)).multiply(5 * Math.random()).minus(new Vec2(width / 2 - 50, height / 2 - 50));
				};
			};
			vf.velocityBasedColoring = false;
			vf.numPoints /= 5;
			vf.memSize = 15;// Can also try mem=5 and drawLines=true
			vf.defaultColour = new Color(92, 1, 1);
			vf.bgColor = new Color(252, 252, 229);
			vf.shiftProb *= 1.5;
			vf.speedScale = 0.25;
			vf.width = 700;
			vf.height = 500;
			vf.forceSpeed = true;
			return vf;
		}
		case LEADERSHIP: {
			// VFSim is not actually meant for interactive behavior. Need to
			// use the legacy non-threading approach for proper behavior
			// during interaction
			AnimatedVectorField vf = new AnimatedVectorField(true) {
				Vec2 ptNow = new Vec2(width / 2, height / 2 + 20);

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					Vec2 v = new Vec2(0, 0);
					for (Vec2 x : getPoints()) {
						if (x.equals(p)) {
							continue;
						}
						Vec2 tmp = x.minus(p);
						v = v.add(tmp.getUnitVector().multiply(50 - 100 * Math.exp(-tmp.length() / 250)));
					}
					Vec2 vel = new Vec2(-p.x - ptNow.x, -p.y - ptNow.y);
					v = v.add(vel.getUnitVector().multiply(1 - 2 * Math.exp(-vel.length() / 250)).multiply(250));
					return v;
				}

				@Override
				public void frameInit() {
					anim.addMouseMotionListener(new MouseMotionAdapter() {

						@Override
						public void mouseMoved(MouseEvent e) {
							Vec2 mouse = new Vec2(e.getPoint());
							mouse = new Vec2(mouse.x - 8, mouse.y - 32);
							mouse.x = -mouse.x + width / 2;
							mouse.y = -height / 2 + mouse.y;
							ptNow = mouse;
						}

						@Override
						public void mouseDragged(MouseEvent e) {

						}
					});
				}
			};
			vf.numPoints = 100;
			vf.memSize = 20;
			vf.velocityBasedColoring = false;
			vf.shiftProb = 0;
			vf.velMultiplier = 2.5;
			vf.stroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			vf.drawLines = true;
			return vf;
		}
		case LEADERSHIP_MODIFIED: {
			// VFSim is not actually meant for interactive behavior. Need to
			// use the legacy non-threading approach for proper behavior
			// during interaction
			AnimatedVectorField vf = new AnimatedVectorField(true) {
				Vec2 ptNow = new Vec2(0, 0);

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					Vec2 v = new Vec2(0, 0);
					for (Vec2 x : getPoints()) {
						if (x.equals(p)) {
							continue;
						}
						Vec2 tmp = x.minus(p);
						v = v.add(tmp.getUnitVector().multiply(50 - 100 * Math.exp(-tmp.length() / 250)));
					}
					Vec2 vel = new Vec2(-p.x - ptNow.x, -p.y - ptNow.y);
					v = v.add(vel.getUnitVector().multiply(1 - 2 * Math.exp(-vel.length() / 250)).multiply(250));
					return v;
				}
			};
			vf.numPoints = 100;
			vf.memSize = 20;
			vf.velocityBasedColoring = false;
			vf.shiftProb = 0;
			vf.velMultiplier = 2.5;
			vf.stroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			vf.drawLines = true;
			return vf;
		}
		case GEMINI: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					return p.multiply(1 / 50.).flip().apply(k -> Math.log(Math.abs(k))).multiply(100);
				}
			};
			vf.velocityBasedColoring = true;
			vf.numPoints /= 2;
			return vf;
		}
		// the following are from
		// https://github.com/anvaka/fieldplay/blob/main/Awesome%20Fields.md
		case REFLECTING_POOL: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 p, double t) {
					Vec2 vel = new Vec2();
					Vec2 tmp = p.apply(x -> x / 200);
					vel.x = Math.sin(5 * tmp.y + tmp.x);
					vel.y = Math.sin(5 * tmp.x - tmp.y);
					return vel.apply(k -> k * 200);
				}
			};
			return vf;
		}
		case SAURONS_EYE: {
			AnimatedVectorField vf = new AnimatedVectorField() {

				@Override
				protected Vec2 getVelocityVector(Vec2 pIn, double t) {
					Vec2 p = pIn.apply(x -> x / 30);
					Vec2 v = new Vec2(0., 0.);

					// center parts
					double pupilrange = new Vec2(p.y, p.x + 6. * Math.signum(p.x)).length();
					Vec2 pupilborder = new Vec2(-p.y, (p.x + 6. * Math.signum(p.x))).apply(k -> k * 2.6);
					v = v.add(pupilborder.multiply(smoothstep(6.6, 6.8, pupilrange) * smoothstep(7.1, 6.9, pupilrange)));

					double range = p.length();
					Vec2 iris = p.apply(k -> k * 7 / Math.sqrt(range));
					v = v.add(iris.multiply(smoothstep(7.0, 7.5, pupilrange) * smoothstep(4.0, 3.8, range)));

					Vec2 pupil = new Vec2(p.x + 1. * Math.signum(p.x), p.y);
					v = v.add(pupil.multiply(smoothstep(6.8, 6.6, pupilrange)));

					// absolute parts
					Vec2 psign = p.sign();
					Vec2 a = p.abs();
					Vec2 vabs = new Vec2(0.0, 0.0);

					double borderrange = new Vec2(p.x, p.y + 7. * Math.signum(p.y)).length();
					Vec2 border = new Vec2(a.y + 7. * Math.signum(a.y) * (a.y * a.y / (a.y * a.y + 0.01)) / Math.sqrt(3. / (a.x + 1.)), -a.x + 3. / Math.sqrt(a.x + 1.)).multiply(-1.5);
					vabs = vabs.add(border.multiply(smoothstep(10.8, 11.25, borderrange) * smoothstep(11.7, 11.25, borderrange) * smoothstep(3.8, 4.1, range)));

					Vec2 irisborder = new Vec2(a.y, -a.x).multiply(5 * (a.y / (a.y + 3.))).add(a.multiply(0.2));
					vabs = vabs.add(irisborder.multiply(smoothstep(3.8, 4.25, range) * smoothstep(4.7, 4.25, range)));

					Vec2 white = new Vec2(1.0, -0.2 * (a.y)).multiply(12);
					vabs = vabs.add(white.multiply(smoothstep(4.3, 4.5, range) * smoothstep(11.2, 11., borderrange)));

					v = v.add(new Vec2(vabs.x * psign.x, vabs.y * psign.y));

					// outside part
					Vec2 outside = p.multiply(1 / ((borderrange - 10.) * (borderrange - 10.)));
					v = v.minus(outside.multiply(smoothstep(11.3, 11.5, borderrange)));
					return v.apply(k -> k * 30);
				}

				private double smoothstep(double edge0, double edge1, double x) {
					x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
					return x * x * (3 - 2 * x);
				}

				private double clamp(double x, double lowerLimit, double upperLimit) {
					if (x < lowerLimit) {
						x = lowerLimit;
					}
					if (x > upperLimit) {
						x = upperLimit;
					}
					return x;
				}

			};
			vf.numPoints = 5000;
			vf.drawLines = true;
			vf.width = 600;
			vf.height = 600;
			return vf;
		}
		default:
			return null;
		}

	}

}
