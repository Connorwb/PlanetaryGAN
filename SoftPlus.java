

public class SoftPlus extends Behavior {
	public SoftPlus() {
		mode = "Soft";
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