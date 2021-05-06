/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 4/23/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The SoftPlus object holds the activation function for a node, along with relevant information.
 * 
 * Attributes :
 * 
 * Methods : 
 * Activation: The SoftPlus function. Returns the result of the function applied to the parameter.
 * Derivative: The derivative of the SoftPlus function. Used only in determining some partial derivatives used for backpropagation.
 *
 */

public class SoftPlus extends Behavior {
	public SoftPlus() {
		super();
		this.mode = "Soft";
	}
	
	@Override
	public double activation(double in) {
		return Math.log10(1+Math.exp(in));
	}
	
	@Override
	public double derivative(double in) {
		return (Math.exp(in))/(Math.log(10)*(Math.exp(in)+1));
	}
}