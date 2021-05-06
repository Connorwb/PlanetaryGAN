import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//UNUSED CLASS

public class SettingsPane extends Pane {
	private ComboBox activations;
	private TextField [] sizesBoxes;
	private Button createBut, loadNet;
	private Trainer trainer;
	private double [][] datatosend;
	private Stage pStage;
	private Scene trainingScene;
	
	SettingsPane(Trainer trainerin, double [][] datatosendin, Stage pStagein) {
		super();
		trainer = trainerin;
		datatosend = datatosendin;
		pStage = pStagein;
		ObservableList<String> options = FXCollections.observableArrayList(
				"Sigmoid",
				"ReLU",
				"SoftPlus",
				"Swish",
				"Random");
		activations = new ComboBox<String>(options);
		createBut = new Button("Create");
		createBut.setTranslateY(30);
		createBut.setTranslateX(30);
		sizesBoxes = new TextField[4];
		sizesBoxes[0] = new TextField("10");
		sizesBoxes[0].setDisable(true);
		sizesBoxes[1] = new TextField("25");
		sizesBoxes[2] = new TextField("100");
		sizesBoxes[3] = new TextField("784");
		for (int i = 0; i < sizesBoxes.length; i++) {
			sizesBoxes[i].setTranslateX((50 * i)+100);
			sizesBoxes[i].setTranslateY(100);
		}
		createBut.setOnAction(e -> {
			try {
				int [] parsedSizes = new int[sizesBoxes.length];
				int validtally = 0;
				for (int j = 0; j < sizesBoxes.length; j++) {
					parsedSizes[j] = Integer.parseInt(sizesBoxes[j].getText());
					if (parsedSizes[j] > 0) {
						validtally += 1;
					}
				}
				int [] sizes = new int[validtally];
				for (int j = 0; j < validtally; j++) {
					sizes[j] = parsedSizes[j];
				}
				String mode = (String) activations.getValue();
				switch (mode) {
					case "Sigmoid":
						mode = "Sig";
						break;
					case "ReLU":
						break;
					case "SoftPlus":
						mode = "Soft";
						break;
					case "Swish":
						break;
					case "Random":
						mode = "Rand";
						break;
					default:
						mode = "Sig";
						break;	
				}
				trainer = new Trainer(0.1, datatosend);
				NodeWriteHead writer = new NodeWriteHead(mode, sizes, false, 50);
				trainer.setOutputLayer(writer.getOutputLayer(), false);
				writer = new NodeWriteHead(mode, sizes, true, 50);
				trainer.setOutputLayer(writer.getOutputLayer(), true);
				System.out.println("Network created without incident.");
				pStage.setScene(trainingScene);
			} catch (Exception exept) {}
		});
		loadNet = new Button("Load Network");
		loadNet.relocate(50, 150);
		loadNet.setOnAction(e -> {
			try {
				File loadFrom;
				JFrame parentFrame = new JFrame();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Name and choose a location for the network");   
				int userSelection = fileChooser.showSaveDialog(parentFrame);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    loadFrom = fileChooser.getSelectedFile();
				    createBut.setDisable(true);
				    loadNet.setDisable(false);
				    trainer = new Trainer(0.1, datatosend);
				    NodeWriteHead writer = new NodeWriteHead(loadFrom, 50);
					trainer.setOutputLayer(writer.getOutputLayer(), false);
					writer = new NodeWriteHead("Sig", writer.getSizes(), true, 50);
					trainer.setOutputLayer(writer.getOutputLayer(), true);
					pStage.setScene(trainingScene);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		this.getChildren().addAll(activations, createBut, loadNet);
		this.getChildren().addAll(sizesBoxes);
	}
	
	public void giveScenes(Scene trainin) {
		trainingScene = trainin;
	}
}