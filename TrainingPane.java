import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

//UNUSED CLASS

public class TrainingPane extends Pane {
	private Button goTest, toggleTraining, saveNet;
	private Trainer trainer;
	private Scene testingScene;
	private Stage pStage;
	private TestingPane testPane;
	private boolean busy, isTraining;
	private double trialKeep;
	private double [][] datatosend;
	private double [] defNoise;
	
	public TrainingPane (Trainer trainerin, Stage pStagein, 
			TestingPane testPanein, double[][] datatosendin) {
		trainer = trainerin;
		pStage = pStagein;
		testPane = testPanein;
		datatosend = datatosendin;
		isTraining = false;
		busy = false;
		goTest = new Button("Start analysing");
		goTest.setOnAction(e -> {
			pStage.setScene(testingScene);
			testPane.loadPane(trainer);
		});
		goTest.relocate(300,300);
		saveNet = new Button("Save Network");
		saveNet.setOnAction(new saveNetwork());
		Timeline trainline = new Timeline();
		trainline.setCycleCount(7200); //TODO remove cap
		//trainline.setCycleCount(Timeline.INDEFINITE);
		toggleTraining = new Button("Start Training");
		toggleTraining.setTranslateX(450);
		toggleTraining.setTranslateY(50);
		toggleTraining.setOnAction(e -> {
			if (isTraining) {
				isTraining = false;
				saveNet.setDisable(false);
				trainline.stop();
				toggleTraining.setText("Start Training");
			} else {
				isTraining = true;
				Neuron [] temptr = trainer.getGenOutputLayer();
				while (temptr[0].hasPrevLayer()) {
					temptr = temptr[0].getPrevLayer();
				}
				defNoise = new double[temptr[0].getWeights().length];
				for (int ii = 0; ii < defNoise.length; ii++) {
					defNoise[ii] = Math.random() * 2 - 1;
				}
				saveNet.setDisable(true);
				trainline.play();
				toggleTraining.setText("Stop Training");
			}
		});
		WritableImage sample = new WritableImage(28, 28);
		PixelWriter pixelWriter = sample.getPixelWriter();
		WritableImage exampic = new WritableImage(28, 28);
		PixelWriter pixelWriterTwo = exampic.getPixelWriter();
		Canvas canvasgen = new Canvas(224, 224);
		Canvas canvasreal = new Canvas(224, 224);
		canvasreal.setTranslateX(canvasgen.getWidth());
		KeyFrame keyframe = new KeyFrame(Duration.millis(1000), action -> {
			if (isTraining) {
				busy = true;
				trainer.train();
				double [] toshow =  trainer.getExample(defNoise, trainer.getGenOutputLayer());
				for (int ii = 0; ii < 28; ii++) {
					for (int iii = 0; iii < 28; iii++) {
						int disp = (int) Math.ceil(toshow[(ii*28)+iii] * 255);
						pixelWriter.setColor(ii, iii, Color.rgb(disp,disp,disp));
					}
				}
				canvasgen.getGraphicsContext2D().drawImage(sample, 0, 0, canvasgen.getWidth(), canvasgen.getHeight());
				double [] twoshow =  datatosend[(int)Math.floor(Math.random()*datatosend.length)];
				for (int ii = 0; ii < 28; ii++) {
					for (int iii = 0; iii < 28; iii++) {
						int disp = (int) Math.ceil(twoshow[(ii*28)+iii] * 255);
						pixelWriterTwo.setColor(ii, iii, Color.rgb(disp,disp,disp));
					}
				}
				canvasreal.getGraphicsContext2D().drawImage(exampic, 0, 0, canvasreal.getWidth(), canvasreal.getHeight());
				//System.out.println(toshow[0] - trialKeep);
				trialKeep = toshow[0];
				busy = false;
				try {
					trainer.join();
				} catch (InterruptedException e1) {
					isTraining = false;
					e1.printStackTrace();
				}
			}
		});
		
		trainline.getKeyFrames().add(keyframe);
		this.getChildren().addAll(canvasgen, canvasreal, toggleTraining, saveNet, goTest);
	}
	
	private class saveNetwork implements EventHandler<ActionEvent>{
		saveNetwork(){
		}
		
		@Override
		public void handle(ActionEvent e) {
			NodeReadHead saver = new NodeReadHead(trainer.getGenOutputLayer());
		}
	}
	
	public void giveScenes(Scene test) {
		testingScene = test;
	}
}