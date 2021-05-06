import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/*
 * Class: 
 * Author: Connor Bramhall
 * Date Created: 5/1/2021
 * Date Modified: 5/3/2021
 * 
 * Purpose: This object holds most of the data needed for the user demonstration of the Generator.
 *
 */

public class TestingPane extends Pane {
	private Trainer tester;
	private Slider [] noise;
	private PixelWriter pixelWriter;
	private Canvas canvasgen;
	private WritableImage sample;
	private boolean busy;
	private Button newNetwork;
	private Stage pStage;
	private Scene settings;
	
	public TestingPane() {
		super();
		busy = false;
		noise = new Slider [10];
		sample = new WritableImage(28, 28);
		pixelWriter = sample.getPixelWriter();
		canvasgen = new Canvas(280, 280);
		canvasgen.relocate(300, 50);
		for (int i = 0; i < noise.length; i++) {
			noise[i] = new Slider(0,1,0.5);
			noise[i].relocate(50, (i*30)+35);
			noise[i].setOnMouseDragged(e -> {
				if (!busy) {
					busy = true;
					double [] toshow =  tester.getExample(getNoise(), tester.getGenOutputLayer());
					for (int ii = 0; ii < 28; ii++) {
						for (int iii = 0; iii < 28; iii++) {
							int disp = (int) Math.ceil(toshow[(ii*28)+iii] * 255);
							pixelWriter.setColor(ii, iii, Color.rgb(disp,disp,disp));
						}
					}
					canvasgen.getGraphicsContext2D().drawImage(sample, 0, 0, canvasgen.getWidth(), canvasgen.getHeight());
					busy = false;
				}
			});
		}
		newNetwork = new Button("Create New Network");
		newNetwork.relocate(40,340);
		this.getChildren().addAll(canvasgen, newNetwork);
		this.getChildren().addAll(noise);
	}
	
	public void loadPane(Trainer in) {
		tester = in;
		double [] toshow =  tester.getExample(getNoise(), tester.getGenOutputLayer());
		for (int ii = 0; ii < 28; ii++) {
			for (int iii = 0; iii < 28; iii++) {
				int disp = (int) Math.ceil(toshow[(ii*28)+iii] * 255);
				pixelWriter.setColor(ii, iii, Color.rgb(disp,disp,disp));
			}
		}
		canvasgen.getGraphicsContext2D().drawImage(sample, 0, 0, canvasgen.getWidth(), canvasgen.getHeight());
	}
	
	private double[] getNoise() {
		double [] from = new double[noise.length];
		for (int i = 0; i < from.length; i++) {
			from[i] = noise[i].getValue();
		}
		return from;
	}
	
	public void giveTransfers(Scene settingsin, Stage pStagein) {
		settings = settingsin;
		pStage = pStagein;
		newNetwork.setOnAction(e -> {
			pStage.setTitle("Settings Menu");
			pStage.setScene(settings);
		});
	}
}