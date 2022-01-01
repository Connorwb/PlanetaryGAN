

public class EncTrainer extends Trainer {
	
	public EncTrainer(double lr, double[][] data) {
		super(lr, data);		
	}
	
	@Override
	public void setOutputLayer(Neuron[] toSet, boolean isDisc) {
		if (isDisc) {
			DiscOutputLayer = toSet;
		} else {
			GenOutputLayer = toSet;
		}
	}
	
	@Override
	public void trainIteration() {
		//Temporary Stitch
		Neuron [] GenInputLayer = GenOutputLayer;
		while (GenInputLayer[0].hasPrevLayer()) {
			GenInputLayer = GenInputLayer[0].getPrevLayer();
		}
		for (int i = 0; i < DiscOutputLayer.length; i++) {
			DiscOutputLayer[i].setNextLayer(GenInputLayer);
		}
		for (int i = 0; i < GenInputLayer.length; i++) {
			GenInputLayer[i].setPrevLayer(DiscOutputLayer);
		}
		
		int discLayers = 1; 
		Neuron [] last = GenOutputLayer;
		while (last[0].hasPrevLayer()) {
			last = last[0].getPrevLayer();
			discLayers++;
		}
		
		double [] realExample = samples[(int)Math.floor(Math.random()*samples.length)];
		double [] realguess = getExample(realExample, GenOutputLayer);
		
		double MSE = 0;
		double [] trueDCDA = new double[realguess.length];
		for (int i = 0; i < trueDCDA.length; i++) {
			trueDCDA[i] = -2*(realguess[i] - realExample[i]);
			MSE += Math.pow(realguess[i] - realExample[i], 2);
		}
		
		Neuron [] thru = GenOutputLayer;
		double [] carryover = trueDCDA;
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
			for (int ll = 0; ll < realExample.length; ll++) {
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
		discError += MSE;
		
		//De-stitch
		for (int i = 0; i < DiscOutputLayer.length; i++) {
			DiscOutputLayer[i].setNextLayer(null);
		}
		for (int i = 0; i < GenInputLayer.length; i++) {
			GenInputLayer[i].setPrevLayer(null);
		}
	}
}