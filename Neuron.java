/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The Neuron object represents one convergence of weights and biases in the network. 
 * 
 * Attributes :
 * activationfunct: the Behavior object that this Neuron uses to parse its input.
 * prevLayer: handles to the neurons on the previous layer.
 * nextLayer: handles to the neurons on the next layer.
 * batchSize: the number of examples that go into the cost determination for each round of backpropagation.
 * activationVal: the current value held by the node, determined by the values of the previous node times the relevant weight, plus the bias,
 * then put through the activation function.
 * bias: this Neuron's bias value, added to the sum of the fed-forward values.
 * weights: the weights used to multiply each previous Neuron's activation value, in the same order as prevLayer.
 * Other attributes starting with "d" are running totals for backpropogation calculations.
 * 
 * Methods : 
 * cleanupDCDA: used to clear a running total related to the "frozen" backpropogation of the Discriminator.
 * cleanup: used to clear all running totals after backpropogation
 * feedforward: determines this Neuron's activation value from the previous layer's values, along with this Neuron's weights, bias and 
 * activation function.
 * hasPrevLayer: returns a boolean value for whether this Neuron has a layer before it.
 * hasNextLayer: returns a boolean value for whether this Neuron has a layer after it.
 * Other methods starting with "add" add to the running totals used in backpropogation.
 */

public class Neuron {
	private Behavior activationfunct;
	private Neuron[] prevLayer;
	private Neuron[] nextLayer;
	private double dCdASum;
	private double dCdBSum;
	private double [] dCdWSum;
	private int batchSize;
	private double activationVal;
	private double dAdZ; 
	private double bias;
	private double[] weights;
	
	public Neuron(Behavior af, int batch) {
		activationfunct = af;
		batchSize = batch;
		dAdZ = 0;
		dCdBSum = 0;
		dCdASum = 0;
	}
	
	public void cleanupDCDA() {
		dCdASum = 0;
	}
	
	
	public void cleanup() {
		dCdASum = 0;
		dCdBSum = 0;
		for (int i = 0; i < weights.length; i++) {
			dCdWSum[i] = 0;
		}
	}
	
	public void update(double learningRate) {
		bias += (dCdBSum * learningRate / ( (double) batchSize));
		for (int i = 0; i < weights.length; i++) {
			weights[i] += (dCdWSum[i] * learningRate / ( (double) batchSize));
		}
		cleanup();
	}
	
	public void feedforward(double[] in) {
		double z = 0;
		for (int i = 0; i < in.length; i++) {
			//System.out.println(in.length +  " " + weights.length);
			z += in[i]*weights[i];
		}
		z += bias;
		double a = activationfunct.activation(z);
		dAdZ = activationfunct.derivative(z);
		activationVal = a;
	}
	
	public Neuron[] getPrevLayer() {
		return prevLayer;
	}
	
	public void setPrevLayer(Neuron[] in) {
		prevLayer = in;
		try {
			dCdWSum = new double[in.length];
		} catch (Exception e) {
			dCdWSum = new double[weights.length];
		}
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
	
	public int getBatch() {
		return batchSize;
	}
	
	public double getDADZ() {
		return dAdZ;
	}
	
	public double getDCDA() {
		return dCdASum;
	}
	
	public void addToDCDW(double in, int index) {
		if (Double.isNaN(in)) {
			in = 0;
		} 
		dCdWSum[index] += in;
	}
	
	public void addToDCDB(double in) {
		if (Double.isNaN(in)) {
			in = 0;
		} 
		dCdBSum += in;
	}
	
	public void addToDCDA(double in) {
		if (Double.isNaN(in)) {
			in = 0;
		} 
		dCdASum += in;
	}
	
	public double getWeight(int in) {
		return weights[in];
	}
	
	public double [] getWeights() {
		return weights;
	}
	
	public double getBias() {
		return bias;
	}
	
	public String getMode() {
		return activationfunct.getMode();
	}
	
	public void setMode(Behavior in) {
		activationfunct = in;
	}
}