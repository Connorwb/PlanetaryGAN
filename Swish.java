/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 4/23/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The ReLU object holds the activation function for a node, along with relevant information.
 * 
 * Attributes :
 * c: The constant used in the Swish function.
 * 
 * Methods : 
 * Activation: The Swish function. Returns the result of the function applied to the parameter.
 * Derivative: The derivative of the Swish function. Used only in determining some partial derivatives used for backpropagation.
 *
 */

public class Swish extends Behavior {
	double c;
	
	public Swish(double set) {
		super();
		c = set;
		this.mode = "Swish" + c;
	}
	
	@Override
	public double activation(double in) {
		return in*s(c*in);
	}
	
	@Override
	public double derivative(double in) {
		return (c*activation(in))+(s(c*in)*(1-(c*activation(in))));
	}
	
	private double s(double in) {
		return 1/(1+Math.exp(-in));
	}
}