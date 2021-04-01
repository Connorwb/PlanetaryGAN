/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 3/31/2021
 * 
 * Purpose: This object manages the utilization of the Neural Network, as well as handling exporting images that
 * the network generates.
 * 
 * Attributes :
 * TODO
 * 
 * Methods : 
 * TODO
 *
 */

public class Trainer {
	Neuron [] GenOutputLayer;
	Neuron [] DiscOutputLayer;
	double learningRate;
	
	public Trainer(double lr) {
		learningRate = lr;
	}
	
	public void train() {
		
	}
	
	public void setOutputLayer(Neuron[] toSet, boolean isDisc) {
		if (isDisc) {
			DiscOutputLayer = toSet;
		} else {
			GenOutputLayer = toSet;
		}
		
	}
	
	public Neuron[] getGenOutputLayer() {
		return GenOutputLayer;
	}
	
	public double[] getExample(double [] noise) {
		Neuron[] marker = GenOutputLayer;
		while (marker[0].hasPrevLayer()) {
			marker = marker[0].getPrevLayer();
		}
		double [] thru;
		thru = noise;
		while (marker[0].hasNextLayer()) {
			double [] temp = new double[marker.length];
			for (int i = 0; i < marker.length; i++) {
				marker[i].feedforward(thru);
				temp[i] = marker[i].getActivation();
			}
			thru = temp;
			marker = marker[0].getNextLayer();
		}
		double [] temp = new double[marker.length];
		for (int i = 0; i < marker.length; i++) {
			marker[i].feedforward(thru);
			temp[i] = marker[i].getActivation();
		}
		thru = temp;
		marker = marker[0].getNextLayer();
		return thru;
	}
	
	public void printExample(double [] in) {
		
	}
}