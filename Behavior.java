

public class Behavior {
	protected String mode;
	
	public Behavior() {
		mode = "Sig";
	}
	
	public double activation(double in) {
		return (1.0/ (1+Math.pow(Math.E, 0-in)));
	}
	
	public double derivative(double in) {
		return activation(in)*(1-activation(in));
	}
	
	public String getMode() {
		return mode;
	}
}