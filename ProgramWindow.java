import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 3/31/2021
 * 
 * Purpose: The ProgramWindow method initializes many of the main components of the network, and handles the 
 * GUI.
 * 
 * Attributes :
 * 
 * Methods : 
 * Start - application start method.
 * main - static main method that launches the Application.
 *
 */

public class ProgramWindow extends Application {
	
	public ProgramWindow() {
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ProgramWindow manager = new ProgramWindow();
		
		//Everything below is meant for testing. Make sure to remove for final product.
		manager.testCaseOneAB(0);
		manager.testCaseOneAB(1);
		double [] standardIn = {1,0};
		manager.testCaseOneC(standardIn);
		Platform.exit();
	}
	
	public static void main(String[] args) {
		ProgramWindow.launch(args);
	}
	
	public void testCaseOneAB(double input) {
		/*
		 * Expected Results:
		 * .850
		 * .868
		 */
		Behavior sigmoid = new Behavior();
		double[] one = {1};
		Neuron n1 = new Neuron(sigmoid, 12);
		n1.setWeights(one);
		n1.setBias(1);
		Neuron n2 = new Neuron(sigmoid, 12);
		n2.setWeights(one);
		n2.setBias(1);
		Neuron [] next1 = {n2};
		n1.setNextLayer(next1);
		double [] inputAr = {input};
		n1.feedforward(inputAr);
		inputAr[0] = n1.getActivation();
		n2.feedforward(inputAr);
		System.out.println(n2.getActivation());
	}
	
	public void testCaseOneC(double[] input) throws Exception {
		/*
		 * Expected Results:
		 * .974
		 * .974
		 * TODO: update these results in testing file
		 */
		int [] sizes = {2, 3, 2};
		NodeWriteHead writer = new NodeWriteHead("Sig", sizes, false, 5);
		Neuron [] l3 = writer.getOutputLayer();
		Neuron [] l2 = l3[0].getPrevLayer();
		double [] one2 = {1, 1};
		for (int i = 0; i < l2.length; i++) {
			l2[i].setWeights(one2);
			l2[i].setBias(1);
		}
		double [] one3 = {1, 1, 1};
		for (int i = 0; i < l3.length; i++) {
			l3[i].setWeights(one3);
			l3[i].setBias(1);
		}
		Trainer trainer = new Trainer(.05);
		trainer.setOutputLayer(l3, false);
		double [] results = trainer.getExample(input, l3);
		System.out.println(results[0]);
		System.out.println(results[1]);
	}
}