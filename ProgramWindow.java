import javafx.application.Application;
import javafx.stage.Stage;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 3/28/2021
 * 
 * Purpose: 
 * 
 * Attributes :
 * 
 * Methods : 
 *
 */

public class ProgramWindow extends Application {
	
	public ProgramWindow() {
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ProgramWindow manager = new ProgramWindow();
		manager.testCaseOneAB(0);
		manager.testCaseOneAB(1);
	}
	
	public static void main(String[] args) {
		ProgramWindow.launch(args);
	}
	
	public void testCaseOneAB(double input) {
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
}