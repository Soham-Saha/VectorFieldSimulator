import java.awt.Point;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Vec2 implements Cloneable, Serializable {
	double x;
	double y;
	Vec2 velMem;

	public Vec2() {
		this.x = 0;
		this.y = 0;
	}

	public Vec2 flip() {
		return new Vec2(this.y, this.x);
	}

	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(Point pt) {
		this.x = pt.x;
		this.y = pt.y;
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSq() {
		return x * x + y * y;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	public Vec2 apply(DoubleFunction func) {
		return new Vec2(func.run(x), func.run(y));
	}

	public Vec2 add(Vec2 add) {
		return new Vec2(this.x + add.x, this.y + add.y);
	}

	public Vec2 minus(Vec2 minus) {
		return new Vec2(this.x - minus.x, this.y - minus.y);
	}

	public Vec2 sign() {
		return this.apply(k -> Math.signum(k));
	}

	public Vec2 abs() {
		return this.apply(k -> Math.abs(k));
	}

	public Vec2 multiply(double x) {
		return this.apply(k -> k * x);
	}

	public Vec2 getUnitVector() {
		return this.multiply(1 / length());
	}

	public Vec2 rotate(double ang) {
		return new Vec2(x * Math.cos(ang) - y * Math.sin(ang), x * Math.sin(ang) + y * Math.cos(ang));
	}

	public static Vec2 add(Vec2... k) {
		Vec2 a = new Vec2(0, 0);
		for (Vec2 x : k) {
			a = a.add(x);
		}
		return a;
	}

	@Override
	public boolean equals(Object objInp) {
		Vec2 obj = (Vec2) objInp;
		return x == obj.x && y == obj.y;
	}

	public static double distance(Vec2 a, Vec2 b) {
		return a.minus(b).length();
	}

	public static double distanceSq(Vec2 a, Vec2 b) {
		return a.minus(b).lengthSq();
	}

	public static Vec2 getRandomDirection() {
		double angle = 2 * Math.PI * Math.random();
		return new Vec2(Math.cos(angle), Math.sin(angle));
	}
}