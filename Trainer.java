/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: This object manages the utilization of the Neural Network, as well as handling exporting images that
 * the network generates.
 *
 */

public class Trainer {
	private Neuron [] GenOutputLayer;
	private Neuron [] DiscOutputLayer;
	private double learningRate;
	private double discError;
	private double [][] samples;
	
	public Trainer(double lr, double[][] data) {
		learningRate = lr;
		if (data == null) {
			samples = new double[600][5];
			//double [][] realExamples = {{0, 0, 1, 0, 0}, {0, 0, 1, 0, 0}, {0, 0, 1, 0, 0}, {0, 0, 1, 0, 0}};
			//double [] realExample = realExamples[(int)Math.floor(Math.random()*3.99)];
			//double [] gensamp = {.5, 0, (Math.random()*.1)+.8, inverse, 1-inverse};
			for (int i = 0; i < 600; i++) {
				double inverse = Math.random();
				samples[i][0] = 0;//.5;
				samples[i][1] = 0;//0;
				samples[i][2] = 1;//(Math.random()*.1)+.8;
				samples[i][3] = 0;//inverse;
				samples[i][4] = 0;//1-inverse;
			}
		} else {
			samples = data;
		}
	}
	
	public void train() {
		discError = 0;
		for (int i = 0; i < DiscOutputLayer[0].getBatch(); i++) {
			trainIteration();
		}
		//call updates to finish training 
		Neuron[] updater = GenOutputLayer; 
		while (updater[0].hasNextLayer()) {
			for (int i = 0; i < updater.length; i++) {
				updater[i].update(learningRate);
			}
			updater = updater[0].getNextLayer();
		}
		for (int i = 0; i < updater.length; i++) {
			updater[i].update(learningRate);
		}
		updater = DiscOutputLayer;
		while (updater[0].hasNextLayer()) {
			for (int i = 0; i < updater.length; i++) {
				updater[i].update(learningRate);
			}
			updater = updater[0].getNextLayer();
		}
		for (int i = 0; i < updater.length; i++) {
			updater[i].update(learningRate);
		}
		//System.out.println((discError / (DiscOutputLayer[0].getBatch()*2)) + " " + discError + " " + (DiscOutputLayer[0].getBatch()*2));
	}
	
	
	public void trainIteration() {
		// For this whole thing, remember 0 is a "fake" label and 1 is a "real" label for the discriminator output.
		// Generate an example from GAN
		Neuron[] tempPass = GenOutputLayer;
		while (tempPass[0].hasPrevLayer()) {
			tempPass = tempPass[0].getPrevLayer();
		}
		int numNoise = tempPass[0].getWeights().length; //TODO make this not hardcoded 
		int discLayers = 1; 
		Neuron [] last = DiscOutputLayer;
		while (last[0].hasPrevLayer()) {
			last = last[0].getPrevLayer();
			discLayers++;
		}
		double [] genExample = new double [GenOutputLayer.length];
		double [] noise = new double[numNoise];
		for (int ii = 0; ii < noise.length; ii++) {
			noise[ii] = Math.random() * 2 - 1;
		}
		//noise [0] = .5; //FIXME delete
		//noise [1] = .5; //FIXME delete
		genExample = getExample(noise, GenOutputLayer);
		
		// Feed the false example to the discriminator, 
		double genguess = getExample(genExample, DiscOutputLayer)[0];
		
		// Compare this to the expected value
		double genAcc = Math.abs(1 - genguess);
		
		//get the associated cost derivatives
		//double [] genDCDA = {-1.0 / (Math.log(10)*(genAcc-1))};
		double [] genDCDA = {(1-genguess)};

		// Start the frozen backpropogation for the disc
		Neuron [] thru = DiscOutputLayer;
		double [] carryover = genDCDA;
		for (int i = 0; i < discLayers - 1; i++) {//for each layer but the last
			for (int ii = 0; ii < thru.length; ii++) {//for each node
				//calculating the change in gradient for each node 
				for (int iii = 0; iii < thru[ii].getPrevLayer().length; iii++) {//for each connection
					double toAdd = carryover[ii];
					toAdd = toAdd * thru[ii].getDADZ();
					thru[ii].getPrevLayer()[iii].addToDCDA(toAdd * thru[ii].getWeight(iii)); //note: was prev
					toAdd = toAdd * thru[ii].getPrevLayer()[iii].getActivation();
					//thru[ii].addToDCDW(toAdd, iii); frozen
				}
				//for the bias
				double toAdd = carryover[ii];
				toAdd = toAdd * thru[ii].getDADZ();
				//thru[ii].addToDCDB(toAdd); frozen
			}
			//get dCdA for nextlayer
			thru = thru[0].getPrevLayer();
			carryover = new double[thru.length];
			for (int c = 0; c < thru.length; c++) {
				carryover[c] = thru[c].getDCDA();
			}
		}
		//for the last layer
		double [] transferC = new double [genExample.length];
		for (int l = 0; l < thru.length; l++) {
			for (int ll = 0; ll < genExample.length; ll++) {
				double toAdd = carryover[l];
				toAdd = toAdd*thru[l].getDADZ();
				transferC[ll] += toAdd * thru[l].getWeight(ll);
				toAdd = toAdd*genExample[ll];
				//thru[l].addToDCDW(toAdd, ll); frozen
			}
			//for the bias
			double toAdd = carryover[l];
			toAdd = toAdd * thru[l].getDADZ();
			//thru[l].addToDCDB(toAdd); frozen
		}
		
		// Start the backpropogation for the gen 
		carryover = transferC;
		thru = GenOutputLayer;
		for (int i = 0; i < discLayers - 1; i++) {//for each layer but the last
			for (int ii = 0; ii < thru.length; ii++) {//for each node
				//calculating the change in gradient for each node 
				for (int iii = 0; iii < thru[ii].getPrevLayer().length; iii++) {//for each connection
					double toAdd = carryover[ii];
					toAdd = toAdd * thru[ii].getDADZ();
					thru[ii].getPrevLayer()[iii].addToDCDA(toAdd * thru[ii].getWeight(iii));
					toAdd = toAdd * thru[ii].getPrevLayer()[iii].getActivation();
					thru[ii].addToDCDW(toAdd, iii);
				}
				//for the bias
				double toAdd = carryover[ii];
				toAdd = toAdd * thru[ii].getDADZ();
				thru[ii].addToDCDB(toAdd);
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
			for (int ll = 0; ll < noise.length; ll++) {
				double toAdd = carryover[l];
				toAdd = toAdd*thru[l].getDADZ();
				toAdd = toAdd*noise[ll];
				thru[l].addToDCDW(toAdd, ll);
			}
			//for the bias
			double toAdd = carryover[l];
			toAdd = toAdd * thru[l].getDADZ();
			thru[l].addToDCDB(toAdd);
		}
		
		// clear the junk from the disc for training 
		Neuron [] sweeper = DiscOutputLayer;
		for (int i = 0; i < discLayers; i++) {
			for (int ii = 0; ii < sweeper.length; ii++) {
				sweeper[ii].cleanupDCDA();//clears the calc stuff from frozen backpropogation we don't want for the real deal
			}
			sweeper = sweeper[0].getPrevLayer();
		}
		
		// get the associated cost derivatives for Disc 
		// double [] discDCDA = {-1.0 / (Math.log(10)*genAcc)};
		double [] discDCDA = {(0 - genAcc)};
		
		// Start the backpropogation for the Disc (not frozen) 
		carryover = discDCDA;
		thru = DiscOutputLayer;
		for (int i = 0; i < discLayers - 1; i++) {//for each layer but the last
			for (int ii = 0; ii < thru.length; ii++) {//for each node
				//calculating the change in gradient for each node 
				for (int iii = 0; iii < thru[ii].getPrevLayer().length; iii++) {//for each connection
					double toAdd = carryover[ii];
					toAdd = toAdd * thru[ii].getDADZ();
					thru[ii].getPrevLayer()[iii].addToDCDA(toAdd * thru[ii].getWeight(iii));
					toAdd = toAdd * thru[ii].getPrevLayer()[iii].getActivation();
					thru[ii].addToDCDW(toAdd, iii);
				}
				//for the bias
				double toAdd = carryover[ii];
				toAdd = toAdd * thru[ii].getDADZ();
				thru[ii].addToDCDB(toAdd);
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
			for (int ll = 0; ll < genExample.length; ll++) {
				double toAdd = carryover[l];
				toAdd = toAdd*thru[l].getDADZ();
				toAdd = toAdd*genExample[ll];
				thru[l].addToDCDW(toAdd, ll);
			}
			//for the bias
			double toAdd = carryover[l];
			toAdd = toAdd * thru[l].getDADZ();
			thru[l].addToDCDB(toAdd);
		}
		
		// feed a real example to the discriminator
		double [] realExample = samples[(int)Math.floor(Math.random()*samples.length)];
		double realguess = getExample(realExample, DiscOutputLayer)[0];
		
		// compare this to the expected value 
		double discAcc = Math.abs(realguess);
		
		// get the associatied cost 
		// double [] trueDCDA = {-1.0 / (Math.log(10)*discAcc)};
		double [] trueDCDA = {(1-realguess)};
		
		// start the backpropogation for the disc
		thru = DiscOutputLayer;
		carryover = trueDCDA;
		for (int i = 0; i < discLayers - 1; i++) {//for each layer but the last
			for (int ii = 0; ii < thru.length; ii++) {//for each node
				//calculating the change in gradient for each node 
				for (int iii = 0; iii < thru[ii].getPrevLayer().length; iii++) {//for each connection
					double toAdd = carryover[ii];
					toAdd = toAdd * thru[ii].getDADZ();
					thru[ii].getPrevLayer()[iii].addToDCDA(toAdd * thru[ii].getWeight(iii));
					toAdd = toAdd * thru[ii].getPrevLayer()[iii].getActivation();
					thru[ii].addToDCDW(toAdd, iii);
				}
				//for the bias
				double toAdd = carryover[ii];
				toAdd = toAdd * thru[ii].getDADZ();
				thru[ii].addToDCDB(toAdd);
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
			for (int ll = 0; ll < genExample.length; ll++) {
				double toAdd = carryover[l];
				toAdd = toAdd*thru[l].getDADZ();
				toAdd = toAdd*realExample[ll];
				thru[l].addToDCDW(toAdd, ll);
			}
			//for the bias
			double toAdd = carryover[l];
			toAdd = toAdd * thru[l].getDADZ();
			thru[l].addToDCDB(toAdd);
		}
		discError += genAcc + discAcc;
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
	
	public double getDiscAcc() {
		return discError;
	}
}