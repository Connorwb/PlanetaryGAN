

public class ReLU extends Behavior {
	public ReLU() {
		mode = "ReLU";
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