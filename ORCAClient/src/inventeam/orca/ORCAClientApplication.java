package inventeam.orca;

import com.sun.javafx.binding.StringFormatter;
import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author robin
 */
public class ORCAClientApplication extends Application {

    private ORCA orca;

    @Override
    public void start(Stage primaryStage) {

        orca = new ORCA();

        BorderPane root = createRoot(10);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BorderPane createRoot(double spacing) {
        //<editor-fold defaultstate="collapsed" desc="Indicator Bars">
        Label temperatureLabel = new Label("Temperature:");
        Label temperature = new Label();
        ProgressBar temperatureBar = new ProgressBar();

        Label batteryTemperatureLabel = new Label("Battery Temperature:");
        Label batteryTemperature = new Label();
        ProgressBar batteryTemperatureBar = new ProgressBar();

        Label batteryVoltageLabel = new Label("Battery Voltage:");
        Label batteryVoltage = new Label();
        ProgressBar batteryVoltageBar = new ProgressBar();

        GridPane progressBarsPane = new GridPane();
        progressBarsPane.setHgap(spacing);
        progressBarsPane.setVgap(spacing);
        progressBarsPane.addColumn(0, temperatureLabel, temperature, temperatureBar);
        progressBarsPane.addColumn(1, batteryTemperatureLabel, batteryTemperature, batteryTemperatureBar);
        progressBarsPane.addColumn(2, batteryVoltageLabel, batteryVoltage, batteryVoltageBar);
        //</editor-fold>

        Label directionLabel = new Label("Direction:");
        final char degree = 176;
        directionLabel.textProperty().bindBidirectional(orca.directionProperty(), new DecimalFormat("Direction: ##.####" + degree));
        progressBarsPane.add(directionLabel, 0, 4);
        
        BorderPane root = new BorderPane();
        root.setCenter(progressBarsPane);
        return root;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
