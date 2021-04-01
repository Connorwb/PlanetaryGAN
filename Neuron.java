/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 3/31/2021
 * 
 * Purpose: The Neuron object represents one convergence of weights and biases in the network. 
 * 
 * Attributes :
 * activationfunct: the Behavior object that this Neuron uses to parse its input.
 * prevLayer: handles to the neurons on the previous layer.
 * nextLayer: handles to the neurons on the next layer.
 * TODO explain the calc ones
 * batchSize: the number of examples that go into the cost determination for each round of backpropagation.
 * activationVal: the current value held by the node, determined by the values of the previous node times the relevant weight, plus the bias,
 * then put through the activation function.
 * bias: this Neuron's bias value, added to the sum of the fed-forward values.
 * weights: the weights used to multiply each previous Neuron's activation value, in the same order as prevLayer.
 * 
 * Methods : 
 * TODO The top 3
 * feedforward: determines this Neuron's activation value from the previous layer's values, along with this Neuron's weights, bias and 
 * activation function.
 * FIXME I don't need to explain getters/setters do I? 
 * hasPrevLayer: returns a boolean value for whether this Neuron has a layer before it.
 * hasNextLayer: returns a boolean value for whether this Neuron has a layer after it.
 */

public class Neuron {
	private Behavior activationfunct;
	private Neuron[] prevLayer;
	private Neuron[] nextLayer;
	private double dCdASum;
	private double dCdWsum;
	private double batchSize;
	private double activationVal;
	private double bias;
	private double[] weights;
	
	public Neuron(Behavior af, double batch) {
		activationfunct = af;
		batchSize = batch;
	}
	
	public void backprop(double dCdaL) {
		
	}
	
	public void backpropFrozen(double dCdaL) {
		
	}
	
	public void update(double learningRate) {
		
	}
	
	public void feedforward(double[] in) {
		double z = 0;
		for (int i = 0; i < in.length; i++) {
			z += in[i]*weights[i];
		}
		z += bias;
		double a = activationfunct.activation(z);
		activationVal = a;
	}
	
	public Neuron[] getPrevLayer() {
		return prevLayer;
	}
	
	public void setPrevLayer(Neuron[] in) {
		prevLayer = in;
	}
	
	public Neuron[] getNextLayer() {
		return nextLayer;
	}
	
	public void setNextLayer(Neuron[] in) {
		nextLayer = in;
	}
	
	public void setBias(double in) {
		bias = in;
	}
	
	public void setWeights(double[] in) {
		weights = in;
	}
	
	public double getActivation() {
		return activationVal;
	}
	
	public boolean hasPrevLayer() {
		try {Neuron tester = prevLayer[0];}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean hasNextLayer() {
		try {Neuron tester = nextLayer[0];}
		catch (Exception e) {
			return false;
		}
		return true;
	}
}