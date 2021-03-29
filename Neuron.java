

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
}