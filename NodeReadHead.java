import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javafx.scene.control.Alert;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 4/30/2021
 * Date Modified: 5/3/2021
 *
 * Purpose : The NodeReadHead class is used to get information about the network without the nodes of the network
 * in one place, which can then be used to save the network's information.
 *
 */

public class NodeReadHead{
	private File saveTo;
	
	public NodeReadHead(Neuron[] toSave){
		//https://www.codejava.net/java-se/swing/show-save-file-dialog-using-jfilechooser
		JFrame parentFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Name and choose a location for the network");   
		int userSelection = fileChooser.showSaveDialog(parentFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    saveTo = fileChooser.getSelectedFile();
		}
		try {
			FileWriter output = new FileWriter(saveTo);
			BufferedWriter bf = new BufferedWriter(output);
			Neuron [] thru = toSave;
			while (thru[0].hasPrevLayer()) {
				bf.write(thru.length + " ");
				thru = thru[0].getPrevLayer();
			}
			bf.write(thru.length + " ");
			bf.write(thru[0].getWeights().length + " ");
			bf.newLine();
			thru = toSave;
			while (thru[0].hasPrevLayer()) {
				bf.write("NEW LAYER ");
				for (int i = 0; i < thru.length; i++) {
					bf.newLine();
					bf.write("NEW NODE ");
					bf.write(thru[i].getMode() + " ");
					for (int ii = 0; ii < thru[i].getWeights().length; ii++) {
						bf.write(thru[i].getWeight(ii) + " ");
					}
					bf.write(thru[i].getBias() + " ");
				}
				thru = thru[0].getPrevLayer();
				bf.newLine();
			}
			bf.write("NEW LAYER ");
			for (int i = 0; i < thru.length; i++) {
				bf.newLine();
				bf.write("NEW NODE ");
				bf.write(thru[i].getMode() + " ");
				for (int ii = 0; ii < thru[i].getWeights().length; ii++) {
					bf.write(thru[i].getWeight(ii) + " ");
				}
				bf.write(thru[i].getBias() + " ");
			}
			thru = thru[0].getPrevLayer();
			bf.newLine();
			bf.close();
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("The program failed to save correctly. Make sure to input a valid file name.");
			alert.show();
		}
	}
}