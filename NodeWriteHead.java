import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 5/3/2021
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
					break;
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
	
	public NodeWriteHead(File readFrom, int batch) throws Exception {
		int [] fileSizes;
		this.batchsize = batch;
		sigmoid = new Behavior();
		String line;
		String[] lineColumns;
		FileReader fr = new FileReader(readFrom);
		BufferedReader br = new BufferedReader(fr);
		line = br.readLine();
		lineColumns = line.split(" ");
		fileSizes = new int[lineColumns.length];
		for (int i = 0; i < lineColumns.length; i++) {
			fileSizes[lineColumns.length - (i+1)] = Integer.parseInt(lineColumns[i]); 
		}
		this.sizes = fileSizes;
		possbehavior = new Behavior[3];
		possbehavior[0] = sigmoid;
		possbehavior[1] = new ReLU();
		possbehavior[2] = new SoftPlus();
		makeGenerator();
		Neuron[] thru = outputLayer;
		for (int i = 0; i < fileSizes.length - 1; i++) {
			line = br.readLine();
			if (!line.equals("NEW LAYER ")) {
				throw new Exception("Network size and info mismatch. Make sure you did not alter the save file! Error Line: " + line);
			}
			for (int ii = 0; ii < (fileSizes[fileSizes.length - (1+i)]); ii++) {
				line = br.readLine();
				lineColumns = line.split(" ");
				if (!lineColumns[0].equals("NEW")) {
					throw new Exception("Neuron connection and info mismatch. Make sure you did not alter the save file!");
				}
				switch (lineColumns[2]) {
				case "Sig":
					thru[ii].setMode(possbehavior[0]); 
					break;
				case "ReLU":
					thru[ii].setMode(possbehavior[1]);
					break;
				default:
					if (lineColumns[2].equals("Soft")) {
						thru[ii].setMode(possbehavior[2]);
					} else if (lineColumns[2].regionMatches(0, "Swish", 0, 5)) {
						thru[ii].setMode(new Swish(Double.parseDouble(lineColumns[2].substring(5))));
					} else {
						throw new Exception("Behavior mode " + mode + " not valid. Relevant data: " + lineColumns[2]);	
					}
				}
				double[] loadWeights = new double[fileSizes[fileSizes.length - (i+2)]];
				//System.out.println("woop1 " + ii);
				//System.out.println(fileSizes[fileSizes.length - (i+2)]);
				//System.out.println(lineColumns.length);
				for (int iii = 3; iii < loadWeights.length+3; iii++) { 
					//System.out.println("woop2 " + iii + " " + lineColumns[iii]);
					loadWeights[iii-3] = Double.parseDouble(lineColumns[iii]);
				}
				//System.out.println("woop3");
				thru[ii].setWeights(loadWeights);
				thru[ii].setBias(Double.parseDouble(lineColumns[lineColumns.length - 1]));
			}
			thru = thru[0].getPrevLayer();
		}
		//double two = 1+1;
	}
	
	private void makeGenerator() {
		Neuron[] thisLayer = null;
		Neuron[] prevLayer = null;
		for (int i = 1; i < sizes.length - 1; i++) {
			thisLayer = new Neuron[sizes[i]];
			for (int ii = 0; ii < sizes[i]; ii++) {
				thisLayer[ii] = new Neuron(assignBehavior(), batchsize);
				thisLayer[ii].setWeights(randArray(sizes[i-1]));
				thisLayer[ii].setPrevLayer(prevLayer);
				thisLayer[ii].setBias((Math.random()*2)-1);
			}
			prevLayer = thisLayer;
		}
		thisLayer = new Neuron[sizes[sizes.length - 1]];
		for (int ii = 0; ii < sizes[sizes.length - 1]; ii++) {
			thisLayer[ii] = new Neuron(sigmoid, batchsize);
			thisLayer[ii].setWeights(randArray(sizes[sizes.length - 2]));
			thisLayer[ii].setPrevLayer(prevLayer);
			thisLayer[ii].setBias((Math.random()*2)-1);
		}
		Neuron[] nextLayer = thisLayer;
		this.outputLayer = nextLayer;
		while (thisLayer[0].hasPrevLayer()) {
			nextLayer = thisLayer;
			thisLayer = thisLayer[0].getPrevLayer();
			for (int i = 0; i < thisLayer.length; i++) {
				thisLayer[i].setNextLayer(nextLayer);
			}
		}
	}
	
	private void makeDiscriminator() {
		int [] dc = new int [sizes.length];
		dc[sizes.length-1] = 1;
		for (int i = sizes.length - 2; i > 0; i--) {
			dc[i] = (int) Math.ceil(sizes[sizes.length - i] /1.7);
		}
		dc[0] = sizes[sizes.length - 1];
		sizes = dc;
		makeGenerator();
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
	
	public int[] getSizes() {
		return sizes;
	}
}