/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The Behavior object holds the activation function for a node, along with relevant information.
 * 
 * Attributes :
 * mode: a string tag for this activation function, to be used for file I/O.
 * 
 * Methods : 
 * Activation: The Sigmoid function. Returns the result of the function applied to the parameter.
 * Derivative: The derivative of the Sigmoid function. Used only in determining some partial derivatives used for backpropagation.
 *
 */

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