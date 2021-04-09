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
		//FIXME: all training stuff changes the DADZ and activation so that it is only accurate for the final layer.
		
		// Generate examples from GAN
		int numNoise = 4; //TODO make this not hardcoded 
		int discLayers = 1; 
		Neuron [] last = DiscOutputLayer;
		while (last[0].hasPrevLayer()) {
			last = last[0].getPrevLayer();
			discLayers++;
		}
		int toGen = GenOutputLayer[0].getBatch();
		double [][] genExamples = new double [toGen][GenOutputLayer.length];
		for (int i = 0; i < toGen; i++) {
			double [] noise = new double[numNoise];
			for (int ii = 0; ii < noise.length; ii++) {
				noise[ii] = Math.random() * 2 - 1;
			}
			genExamples[i] = getExample(noise, GenOutputLayer);
		}
		// Bring in an equal number of training examples
		// For now this is all hardcoded - TODO
		double [][] realExamples = {{0, 0, 0}, {0, 1, 1}, {0, 1, 1}, {0, 1, 0}};
		
		// Feed these examples to the discriminator, 
		double [] realguesses = new double[toGen];
		double [] genguesses = new double[toGen];
		for (int i = 0; i < toGen; i++) {
			genguesses[i] = getExample(genExamples[i], DiscOutputLayer)[0];
		}
		for (int i = 0; i < toGen; i++) {
			realguesses[i] = getExample(realExamples[i], GenOutputLayer)[0];
		}
		
		// Compare these to the expected values 
		double [] realAcc = new double[toGen];
		double [] genAcc = new double[toGen];
		for (int i = 0; i < toGen; i++) {
			realAcc[i] = Math.abs(realguesses[i]);
		}
		for (int i = 0; i < toGen; i++) {
			genAcc[i] = Math.abs(1 - genguesses[i]);
		}
		
		//get the associated cost derivative
		double [] genDCDA = new double[toGen];
		double [] discDCDA = new double[toGen*2];
		for (int i = 0; i < toGen; i++) {
			genDCDA[i] = 1.0 / (Math.log(10)*(genAcc[i]-1));
			discDCDA[i] = -1.0 / (Math.log(10)*genAcc[i]);
			discDCDA[i+toGen] = -1.0 / (Math.log(10)*realAcc[i]);
		}
		
		// Start the backpropogation for the disc
		Neuron [] thru = DiscOutputLayer;
		double [] lastActive = new double[GenOutputLayer.length];
		for (int i = 0; i < toGen; i++) { //for each example
			double [] carryover = {discDCDA[i]};
			for (int ii = 0; ii < discLayers - 1; ii++) {//for each layer but the last
				for (int iii = 0; iii < thru.length; iii++) {//for each node
					//calculating the change in gradient for each node 
					for (int iiii = 0; iiii < thru[iii].getPrevLayer().length; iiii++) {//for each connection
						double toAdd = carryover[iii];
						toAdd = toAdd * thru[iii].getDADZ();
						thru[iii].getNextLayer()[iiii].addToDCDA(toAdd * thru[iii].getWeight(iiii));
						toAdd = toAdd * thru[iii].getPrevLayer()[iiii].getActivation();
						thru[iii].addToDCDW(toAdd, iiii);
					}
					//for the bias
					double toAdd = carryover[iii];
					toAdd = toAdd * thru[iii].getDADZ();
					thru[iii].addToDCDB(toAdd);
				}
				//get dCdA for nextlayer
				thru = thru[0].getPrevLayer();
				carryover = new double[thru.length];
				for (int c = 0; c < thru.length; c++) {
					carryover[c] = thru[c].getDCDA();
				}
			}
			//for the last layer
			for (int l = 0; l < thru.length; l++) {
				for (int ll = 0; ll < realExamples[i].length; ll++) {
					double toAdd = carryover[l];
					toAdd = toAdd*thru[l].getDADZ();
					toAdd = toAdd*realExamples[i][ll];
					thru[l].addToDCDW(toAdd, ll);
				}
				//for the bias
				double toAdd = carryover[l];
				toAdd = toAdd * thru[l].getDADZ();
				thru[l].addToDCDB(toAdd);
			}
		}
		for (int i = 0; i < toGen; i++) {//for the generated samples TODO
			for (int ii = 0; ii < discLayers; ii++) {
				
			}
		}
		
		// Start the backpropogation for the gen TODO
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
	
	public double[] getExample(double [] noise, Neuron [] outLayer) {
		Neuron[] marker = outLayer;
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