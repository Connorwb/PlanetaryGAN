/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 3/31/2021
 * 
 * Purpose: This object manages the creation of the Neurons and insures they are all linked properly.
 * 
 * Attributes :
 * mode: variable that tells this object what the activation method for the network it is building will be.
 * outputLayer: the last layer of the network of Neurons it created, stored to pass on to the Trainer.
 * sizes: the number of Neurons in each layer, except for the zeroth element, which informs the object how many inputs there will be.
 * sigmoid: the Behavior object that will be used for every network as the last layer. Included in possBehavior.
 * possBehavior: an array of all the possible behaviors, made to prevent making too many Behavior objects.
 * batchsize: the number of training examples that are used for each run of backpropagation.
 * 
 * Methods : 
 * makeGenerator: makes the Generator for the GAN.
 * makeDiscriminator: makes the Discriminator for the GAN.
 * assignBehavior: returns one of the behaviors from possBehavior, either returning the only one that the mode allows or returning a 
 * random behavior from the array if the mode is set to random.
 * randArray: returns a random array of doubles of the size parameter given to the method.
 *
 */

public class NodeWriteHead {
	private String mode;
	private Neuron[] outputLayer;
	private int[] sizes;
	private Behavior sigmoid;
	private Behavior[] possbehavior;
	private int batchsize;
	
	public NodeWriteHead(String mode, int[] sizes, boolean isDiscriminator, int batch) throws Exception {
		this.sizes = sizes;
		this.mode = mode;
		batchsize = batch;
		sigmoid = new Behavior();
		if (mode == "Rand") {
			possbehavior = new Behavior[4];
			possbehavior[0] = sigmoid;
			possbehavior[1] = new ReLU();
			possbehavior[2] = new SoftPlus();
			possbehavior[3] = new Swish(-.5);
		} else {
			possbehavior = new Behavior[1];
			switch (mode) {
				case "Sig":
					possbehavior[0] = sigmoid;
					break;
				case "ReLU":
					possbehavior[0] = new ReLU();
					break;
				case "Soft":
					possbehavior[0] = new SoftPlus();
				case "Swish":
					possbehavior[0] = new Swish(-.5);
					break;
				default:
					throw new Exception("Behavior mode " + mode + " not valid");	
			}
		}
		if (isDiscriminator) {
			makeDiscriminator();
		} else {
			makeGenerator();
		}
	}
	
	private void makeGenerator() {
		Neuron[] thisLayer = null;
		Neuron[] prevLayer = null;
		for (int i = 1; i < sizes.length; i++) {
			thisLayer = new Neuron[sizes[i]];
			for (int ii = 0; ii < sizes[i]; ii++) {
				thisLayer[ii] = new Neuron(assignBehavior(), batchsize);
				thisLayer[ii].setPrevLayer(prevLayer);
				thisLayer[ii].setWeights(randArray(sizes[i-1]));
				thisLayer[ii].setBias((Math.random()*2)-1);
			}
			prevLayer = thisLayer;
		}
		Neuron[] nextLayer = thisLayer;
		outputLayer = nextLayer;
		while (thisLayer[0].hasPrevLayer()) {
			nextLayer = thisLayer;
			thisLayer = thisLayer[0].getPrevLayer();
			for (int i = 0; i < thisLayer.length; i++) {
				thisLayer[i].setNextLayer(nextLayer);
			}
		}
	}
	
	private void makeDiscriminator() {
		
	}
	
	private Behavior assignBehavior() {
		int rand = (int) Math.floor(Math.random() * possbehavior.length);
		return possbehavior[rand];
	}
	
	private double[] randArray(int size) {
		double [] w = new double[size];
		for (int i = 0; i < size; i++) {
			w[i] = Math.random()*2 - 1;
		}
		return w;
	}
	
	public Neuron[] getOutputLayer() {
		return outputLayer;
	}
}