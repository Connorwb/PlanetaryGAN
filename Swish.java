

public class Swish extends Behavior {
	double c;
	
	public Swish(double set) {
		c = set;
	}
	
	@Override
	public double activation(double in) {
		return 1.0;
	}
	
	@Override
	public double derivative(double in) {
		return 1.0;
	}
}