import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 3/28/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: The ProgramWindow class initializes many of the main components of the network, and handles the 
 * GUI.
 * 
 * Attributes :
 * Numerous GUI elements exist to keep GUI elements extant after certain events.
 * datatosend: The 2d array of numeric data that represents the training set.
 * defNoise: The conistant values used to generate each of the 15 live examples.
 * 
 * Methods : 
 * Start - application start method.
 * main - static main method that launches the Application.
 *
 */

public class ProgramWindow extends Application {
	private Stage pStage;
	private Trainer trainer;
	private Pane settingsPane, trainPane;
	private TestingPane testPane;
	private Button createBut, toggleTraining, saveNet, loadNet, goTest;
	private ComboBox activations;
	private Scene settingsScene, trainingScene, testingScene;
	private Text guideSet, guideLoad, accDisp;
	private TextField [] sizesBoxes;
	private boolean busy, isTraining;
	private double [][] datatosend;
	private double [][] defNoise;
	
	public ProgramWindow() {
		busy = false;
		isTraining = false;
		settingsPane = new Pane();
		trainPane = new Pane();
		testPane = new TestingPane();
		File defaultFile = new File("Default.txt");
		try {
			//See MnistMatrix file for credits for the imports
			//TODO replace with custom data import methods
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
			datatosend = new double[mnistMatrix.length][784];
			for (int i = 0; i < mnistMatrix.length; i++) {
				for (int ii = 0; ii < 28; ii++) {
					for (int iii = 0; iii < 28; iii++) {
						datatosend[i][(ii*28)+iii] = mnistMatrix[i].getValue(ii, iii)/255.0;
					}
				}
			}
			NodeWriteHead defaultHead = new NodeWriteHead(defaultFile,1);
			trainer = new Trainer(.1, null);
			trainer.setOutputLayer(defaultHead.getOutputLayer(), false);
			testPane.loadPane(trainer);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		guideSet = new Text("Enter the number of Neurons in each layer");
		guideSet.relocate(30, 50);
		guideLoad = new Text("Or load a network from a file");
		guideLoad.relocate(415, 50);
		ObservableList<String> options = FXCollections.observableArrayList(
						"Sigmoid",
						"ReLU",
						"SoftPlus",
						"Swish",
						"Random");
		activations = new ComboBox<String>(options);
		activations.relocate(106, 270);
		createBut = new Button("Create");
		createBut.relocate(128, 315);
		sizesBoxes = new TextField[4];
		sizesBoxes[0] = new TextField("10");
		sizesBoxes[0].setDisable(true);
		sizesBoxes[1] = new TextField("25");
		sizesBoxes[2] = new TextField("100");
		sizesBoxes[3] = new TextField("784");
		sizesBoxes[3].setDisable(true);
		for (int i = 0; i < sizesBoxes.length; i++) {
			sizesBoxes[i].setTranslateX(70);
			sizesBoxes[i].setTranslateY((i*40) + 90);
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
				//System.out.println("Network created without incident.");
				defNoise = new double[15][sizes[0]];
				for (int ii = 0; ii < defNoise.length; ii++) {
					for (int iii = 0; iii < defNoise[ii].length; iii++) {
						defNoise[ii][iii] = Math.random() * 2 - 1;
					}
				}
				pStage.setWidth(1698);
				pStage.setHeight(672);
				pStage.setTitle("Live Training Window");
				pStage.setScene(trainingScene);
			} catch (Exception exept) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				if (activations.getValue() == null) {
					alert.setContentText("Make sure to select an activation type.");
				} else {
					alert.setContentText("Make sure only numbers are in the boxes.");
				}
				alert.show();
			}
		});
		loadNet = new Button("Load Network");
		loadNet.relocate(450, 175);
		loadNet.setOnAction(e -> {
			try {
				File loadFrom;
				JFrame parentFrame = new JFrame();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select the file location for the network");   
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
					defNoise = new double[15][writer.getSizes()[writer.getSizes().length-1]];
					for (int ii = 0; ii < defNoise.length; ii++) {
						for (int iii = 0; iii < defNoise[ii].length; iii++) {
							defNoise[ii][iii] = Math.random() * 2 - 1;
						}
					}
					pStage.setWidth(1698);
					pStage.setHeight(672);
					pStage.setScene(trainingScene);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		goTest = new Button("Start Analyzing");
		goTest.setOnAction(e -> {
			pStage.setWidth(700);
			pStage.setHeight(450);
			pStage.setScene(testingScene);
			pStage.setTitle("Testing Panel");
			testPane.loadPane(trainer);
		});
		goTest.relocate(1210,558);
		saveNet = new Button("Save Network");
		saveNet.relocate(370, 558);
		saveNet.setOnAction(new saveNetwork());
		Timeline trainline = new Timeline();
		trainline.setCycleCount(Timeline.INDEFINITE);
		toggleTraining = new Button("Start Training");
		toggleTraining.relocate(790,558);
		toggleTraining.setOnAction(e -> {
			if (isTraining) {
				isTraining = false;
				saveNet.setDisable(false);
				goTest.setDisable(false);
				trainline.stop();
				toggleTraining.setText("Start Training");
			} else {
				isTraining = true;
				saveNet.setDisable(true);
				goTest.setDisable(true);
				trainline.play();
				toggleTraining.setText("Stop Training");
			}
		});
		WritableImage [] samples = new WritableImage[15];
		WritableImage [] exampics = new WritableImage[15];
		Canvas [] genList = new Canvas[15];
		Canvas [] realList = new Canvas[15];
		accDisp = new Text();
		accDisp.relocate(700, 530);
		for (int i = 0; i < genList.length; i++) {
			genList[i] = new Canvas(168, 168);
			genList[i].relocate(336*(i%5), Math.floor(i/5) * 168);
			samples[i] = new WritableImage(28, 28);
		}
		for (int i = 0; i < realList.length; i++) {
			realList[i] = new Canvas(168, 168);
			realList[i].relocate(168 + (336*(i%5)), Math.floor(i/5) * 168);
			exampics[i] = new WritableImage(28, 28);
		}
		KeyFrame keyframe = new KeyFrame(Duration.millis(2000), action -> {
			if ((!busy)&&isTraining) {
				busy = true;
				trainer.train();
				for (int i = 0; i < genList.length; i++) {
					PixelWriter pixelWriter = samples[i].getPixelWriter();
					double [] toshow =  trainer.getExample(defNoise[i], trainer.getGenOutputLayer());
					//TODO make more efficient by taking examples from the trainer instead of making new ones
					for (int ii = 0; ii < 28; ii++) {
						for (int iii = 0; iii < 28; iii++) {
							int disp = (int) Math.ceil(toshow[(ii*28)+iii] * 255);
							pixelWriter.setColor(ii, iii, Color.rgb(disp,disp,disp));
						}
					}
					genList[i].getGraphicsContext2D().drawImage(samples[i], 0, 0, genList[i].getWidth(), genList[i].getHeight());
				}
				for (int i = 0; i < realList.length; i++) {
					PixelWriter pixelWriterTwo = exampics[i].getPixelWriter();
					double [] twoshow =  datatosend[(int)Math.floor(Math.random()*datatosend.length)];
					for (int ii = 0; ii < 28; ii++) {
						for (int iii = 0; iii < 28; iii++) {
							int disp = (int) Math.ceil(twoshow[(ii*28)+iii] * 255);
							pixelWriterTwo.setColor(ii, iii, Color.rgb(disp,disp,disp));
						}
					}
					realList[i].getGraphicsContext2D().drawImage(exampics[i], 0, 0, realList[i].getWidth(), realList[i].getHeight());
				}
				accDisp.setText("Discriminator Accuracy: " + trainer.getDiscAcc() + "%");
				//System.out.println(toshow[0] - trialKeep);
				//trialKeep = toshow[0];
				busy = false;
			}
			for (int i = 0; i < 6; i++) {
				trainer.train();
			}
		});
		
		trainline.getKeyFrames().add(keyframe);
		settingsPane.getChildren().addAll(activations, createBut, loadNet, guideSet, guideLoad);
		settingsPane.getChildren().addAll(sizesBoxes);
		trainPane.getChildren().addAll(genList);
		trainPane.getChildren().addAll(realList);
		trainPane.getChildren().addAll(toggleTraining, saveNet, goTest, accDisp);
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		pStage = primaryStage;
		settingsScene = new Scene(settingsPane, 700, 400);
		trainingScene = new Scene(trainPane, 700, 400);
		testingScene = new Scene(testPane, 700, 400);
		testPane.giveTransfers(settingsScene, pStage);
		primaryStage.setScene(testingScene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Testing Panel");
		primaryStage.show();
		
		//Everything below is meant for testing. Make sure to remove for final product.
		//testCaseOneAB(0);
		//manager.testCaseOneAB(1);
		//double [] standardIn = {1,0};
		//manager.testCaseOneC(standardIn);
		//testTraining();
		//manager.dissect();
		//manager.MNISTTest(primaryStage);
		//Platform.exit();
	}
	
	public static void main(String[] args) {
		ProgramWindow.launch(args);
		
//		//Everything below is meant for testing. Make sure to remove for final product.
//		ProgramWindow manager = new ProgramWindow();
//		//double dummy = Math.log10(1+Math.exp(2));
//		//System.out.println(dummy);
//		//System.out.println(Math.log10(1+Math.exp((dummy*3)+1)));
//		manager.testCaseOneAB(0);
//		manager.testCaseOneAB(1);
//		double [] standardIn = {1,0};
//		try {
//			manager.testCaseOneC(standardIn);
//			//manager.testTraining();
//		} catch (Exception ex) {}
//		//manager.dissect();
//		//manager.MNISTTest(primaryStage);
//		Platform.exit();
	}
	
	private class saveNetwork implements EventHandler<ActionEvent>{
		saveNetwork(){
		}
		
		@Override
		public void handle(ActionEvent e) {
			NodeReadHead saver = new NodeReadHead(trainer.getGenOutputLayer());
		}
	}
	
	
	/*
	 * All methods below are meant to validate certain parts of the program or perform some kind of test.
	 * Make sure to remove these from the final product.
	 */

//	public void testCaseOneAB(double input) {
//		/*
//		 * Expected Results:
//		 * .850
//		 * .868
//		 */
//		Behavior sigmoid = new Behavior();
//		double[] one = {1};
//		Neuron n1 = new Neuron(sigmoid, 12);
//		n1.setWeights(one);
//		n1.setBias(1);
//		Neuron n2 = new Neuron(sigmoid, 12);
//		n2.setWeights(one);
//		n2.setBias(1);
//		Neuron [] next1 = {n2};
//		n1.setNextLayer(next1);
//		double [] inputAr = {input};
//		n1.feedforward(inputAr);
//		inputAr[0] = n1.getActivation();
//		n2.feedforward(inputAr);
//		System.out.println(n2.getActivation());
//	}
//	
//	public void testCaseOneC(double[] input) throws Exception {
//		/*
//		 * Expected Results:
//		 * .974
//		 * .974
//		 * TODO: update these results in testing file
//		 */
//		int [] sizes = {2, 3, 2};
//		NodeWriteHead writer = new NodeWriteHead("Soft", sizes, false, 5);
//		Neuron [] l3 = writer.getOutputLayer();
//		Neuron [] l2 = l3[0].getPrevLayer();
//		double [] one2 = {1, 1};
//		for (int i = 0; i < l2.length; i++) {
//			l2[i].setWeights(one2);
//			l2[i].setBias(1);
//		}
//		double [] one3 = {1, 1, 1};
//		for (int i = 0; i < l3.length; i++) {
//			l3[i].setWeights(one3);
//			l3[i].setBias(1);
//			l3[i].setMode(new SoftPlus());
//		}
//		Trainer trainer = new Trainer(.5, null);
//		trainer.setOutputLayer(l3, false);
//		double [] results = trainer.getExample(input, l3);
//		System.out.println(results[0]);
//		System.out.println(results[1]);
//	}
//	
//	public void testTraining() throws Exception {
//		int [] sizes = {3,5,5};
//		NodeWriteHead writer = new NodeWriteHead("Sig", sizes, false, 50);
//		Trainer trainer = new Trainer(.1, null);
//		trainer.setOutputLayer(writer.getOutputLayer(), false);
//		writer = new NodeWriteHead("Sig", sizes, true, 50);
//		trainer.setOutputLayer(writer.getOutputLayer(), true);
//		for (int i = 0; i < 30000; i++) {
//			trainer.train();
//			double [] noise = new double[sizes[0]];
//			for (int ii = 0; ii < noise.length; ii++) {
//				noise[ii] = Math.random() * 2 - 1;
//			}
//			System.out.println(trainer.getExample(noise, trainer.GenOutputLayer)[0]+ " " +
//					trainer.getExample(noise, trainer.GenOutputLayer)[1]+ " " +
//					trainer.getExample(noise, trainer.GenOutputLayer)[2]+ " " +
//					trainer.getExample(noise, trainer.GenOutputLayer)[3]+ " " +
//					trainer.getExample(noise, trainer.GenOutputLayer)[4]+ " ");
//		}
//	}
//	
//	public void dissect() throws Exception {
//		int [] sizes = {2, 2};
//		NodeWriteHead writer = new NodeWriteHead("Sig", sizes, false, 3);
//		Neuron [] l3 = writer.getOutputLayer();
//		double [] tset = {0.7, 0.4};
//		l3[0].setWeights(tset);
//		l3[0].setBias(-.2);
//		double [] tset2 = {0.3, -0.9};
//		l3[1].setWeights(tset2);
//		l3[1].setBias(.8);
//		Trainer trainer = new Trainer(.1, null);
//		trainer.setOutputLayer(l3, false);
//		writer = new NodeWriteHead("Sig", sizes, true, 3);
//		trainer.setOutputLayer(writer.getOutputLayer(), true);
//		Neuron end = writer.getOutputLayer()[0];
//		double [] tset3 = {0.1, -0.1};
//		end.setWeights(tset3);
//		end.setBias(.6);
//		/*
//		double [] inputAr = {.5, .5};
//		l3[0].feedforward(inputAr);
//		l3[1].feedforward(inputAr);
//		inputAr[0] = l3[0].getActivation();
//		inputAr[1] = l3[1].getActivation();
//		end.feedforward(inputAr);
//		System.out.println(end.getActivation());
//		*/
//		trainer.train();
//	}
//	
//	public void MNISTTest(Stage stage) throws Exception { 
//		//See MnistMatrix file for credits for the imports
//		//Super inefficient, but gets the job done. I'm promising to myself that this will not be how I do the actual implementation.
//		MnistMatrix[] mnistMatrix = new MnistDataReader().readData("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
//		double [][] datatosend = new double[mnistMatrix.length][784];
//		for (int i = 0; i < mnistMatrix.length; i++) {
//			for (int ii = 0; ii < 28; ii++) {
//				for (int iii = 0; iii < 28; iii++) {
//					datatosend[i][(ii*28)+iii] = mnistMatrix[i].getValue(ii, iii)/255.0;
//				}
//			}
//		}
//		Trainer trainer = new Trainer(0.1, datatosend);
//		int [] sizes = {25,100,784};
//		NodeWriteHead writer = new NodeWriteHead("Sig", sizes, false, 50);
//		trainer.setOutputLayer(writer.getOutputLayer(), false);
//		writer = new NodeWriteHead("Sig", sizes, true, 50);
//		trainer.setOutputLayer(writer.getOutputLayer(), true);
//		Timeline timeline = new Timeline();
//		timeline.setCycleCount(Timeline.INDEFINITE);
//		WritableImage sample = new WritableImage(28, 28);
//		PixelWriter pixelWriter = sample.getPixelWriter();
//		WritableImage exampic = new WritableImage(28, 28);
//		PixelWriter pixelWriterTwo = exampic.getPixelWriter();
//		Canvas canvasgen = new Canvas(224, 224);
//		Canvas canvasreal = new Canvas(224, 224);
//		canvasreal.setTranslateX(canvasgen.getWidth());
//	    Scene scene = new Scene(new Group(canvasgen, canvasreal), canvasgen.getWidth()+canvasreal.getWidth(), canvasgen.getHeight());
//	    stage.setScene(scene);
//	    stage.show();
//	    double [] noise = new double[sizes[0]];
//		for (int ii = 0; ii < noise.length; ii++) {
//			noise[ii] = Math.random() * 2 - 1;
//		}
//	    KeyFrame keyframe = new KeyFrame(Duration.millis(1000), action -> {
//			trainer.train();
//			double [] toshow =  trainer.getExample(noise, trainer.GenOutputLayer);
//			for (int ii = 0; ii < 28; ii++) {
//				for (int iii = 0; iii < 28; iii++) {
//					int disp = (int) Math.ceil(toshow[(ii*28)+iii] * 255);
//					pixelWriter.setColor(ii, iii, Color.rgb(disp,disp,disp));
//				}
//			}
//			canvasgen.getGraphicsContext2D().drawImage(sample, 0, 0, canvasgen.getWidth(), canvasgen.getHeight());
//			double [] twoshow =  datatosend[(int)Math.floor(Math.random()*datatosend.length)];
//			for (int ii = 0; ii < 28; ii++) {
//				for (int iii = 0; iii < 28; iii++) {
//					int disp = (int) Math.ceil(twoshow[(ii*28)+iii] * 255);
//					pixelWriterTwo.setColor(ii, iii, Color.rgb(disp,disp,disp));
//				}
//			}
//			canvasreal.getGraphicsContext2D().drawImage(exampic, 0, 0, canvasreal.getWidth(), canvasreal.getHeight());
//			System.out.println(trainer.getExample(noise, trainer.GenOutputLayer)[0]);
//	    });
//	    timeline.getKeyFrames().add(keyframe);
//	    for (int i = 0; i < 1500; i++) {
//	    	trainer.train();
//	    	System.out.print(i + "/" + 60000 + " ");
//	    }
//		timeline.play();
//	}
}