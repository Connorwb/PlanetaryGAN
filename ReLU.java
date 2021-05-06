/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 4/23/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The ReLU object holds the activation function for a node, along with relevant information.
 * 
 * Attributes :
 * 
 * Methods : 
 * Activation: The ReLU function. Returns the result of the function applied to the parameter.
 * Derivative: The derivative of the ReLU function. Used only in determining some partial derivatives used for backpropagation.
 *
 */

public class ReLU extends Behavior {
	public ReLU() {
		super();
		this.mode = "ReLU";
	}
	
	@Override
	public double activation(double in) {
		return Math.max(0, in);
	}
	
	@Override
	public double derivative(double in) {
		if (in > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}