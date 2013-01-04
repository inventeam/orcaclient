package inventeam.orca;

import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxisBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
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
        final String ipAddressPattern = "\\d{3}.\\d{3}.\\d.\\d";
        final String portPattern = "\\d{2}|\\d{4}";
        final char degree = 176;
        final Service<Void> orcaClient = orca.getNewOrcaClient();

        final String temperatureMsg = "Temperature: ";
        Label temperatureLabel = new Label();
        temperatureLabel.textProperty().bindBidirectional(orca.temperatureProperty(), new DecimalFormat(temperatureMsg + "##.####" + degree));
        ProgressBar temperatureBar = new ProgressBar();
        temperatureBar.progressProperty().bindBidirectional(orca.temperatureProperty());

        final String batteryTemperatureMsg = "Battery Temperature: ";
        Label batteryTemperatureLabel = new Label();
        batteryTemperatureLabel.textProperty().bindBidirectional(orca.batteryTemperatureProperty(), new DecimalFormat(batteryTemperatureMsg + "##.####" + degree));
        ProgressBar batteryTemperatureBar = new ProgressBar();
        batteryTemperatureBar.progressProperty().bind(orca.batteryTemperatureProperty());

        final String batteryVoltageMsg = "Battery Voltage: ";
        Label batteryVoltageLabel = new Label();
        batteryVoltageLabel.textProperty().bindBidirectional(orca.batteryVoltageProperty(), new DecimalFormat(batteryVoltageMsg + "##.#### V"));
        ProgressBar batteryVoltageBar = new ProgressBar();
        batteryVoltageBar.progressProperty().bindBidirectional(orca.batteryVoltageProperty());

        String directionMsg = "Direction: ";
        Label directionLabel = new Label();
        directionLabel.textProperty().bindBidirectional(orca.directionProperty(), new DecimalFormat(directionMsg + "##.####" + degree));
        
        String rotationsPerSecondMsg = "Rotations Per Second: ";
        Label rotationsPerSecondLabel = new Label();
        rotationsPerSecondLabel.textProperty().bindBidirectional(orca.rotationsPerSecondProperty(), new DecimalFormat(rotationsPerSecondMsg + "###.#### rpms"));
        
        String metersPerSecondMsg = "Meters Per Second: ";
        Label metersPerSecondLabel = new Label();
        metersPerSecondLabel.textProperty().bindBidirectional(orca.metersPerSecondProperty(), new DecimalFormat(metersPerSecondMsg + "###.#### mps"));
        
        GridPane orcaDataPane = new GridPane();
        orcaDataPane.setHgap(spacing);
        orcaDataPane.setVgap(spacing);
        orcaDataPane.addRow(0, directionLabel);
        orcaDataPane.addRow(1, rotationsPerSecondLabel);
        orcaDataPane.addRow(2, metersPerSecondLabel);
        orcaDataPane.addRow(3, temperatureLabel, temperatureBar);
        orcaDataPane.addRow(4, batteryTemperatureLabel, batteryTemperatureBar);
        orcaDataPane.addRow(5, batteryVoltageLabel, batteryVoltageBar);
        
        LineChart<Number, Number> mpsChart = new LineChart(
                NumberAxisBuilder
                .create()
                .label("Time")
                .build(), 
                NumberAxisBuilder
                .create()
                .label("Meters Per Second")
                .build());
        
        mpsChart.setTitle("Flow Speed");
        
        LineChart<Number, Number> rpsChart = new LineChart(
                NumberAxisBuilder
                .create()
                .label("Time")
                .build(), 
                NumberAxisBuilder
                .create()
                .label("Rotations Per Sectond")
                .build());
        
        rpsChart.setTitle("Sensor Speed");
        
        VBox charts = new VBox(spacing);
        charts.getChildren().addAll(mpsChart, rpsChart);
        
        Button startClientButton = new Button("Start Client");
        startClientButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                orcaClient.start();
            }
        });
        Button stopClientButton = new Button("Stop Client");
        stopClientButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                orcaClient.cancel();
            }
        });
        
        HBox clientControls = HBoxBuilder
                .create()
                .spacing(spacing)
                .children(startClientButton, stopClientButton)
                .build();
        
        final TextField addressField = new TextField();
        addressField.setPromptText("ORCA IP Address");
        
        final TextField portField = new TextField();
        portField.setPromptText("ORCA Port");

        Button setAddressButton = new Button("Set");
        setAddressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                orca.setAddress(addressField.getText());
                orca.setPort(Integer.decode(portField.getText()));
            }
        });
        
        HBox wifiSettingsPane = new HBox(spacing);
        wifiSettingsPane.getChildren().addAll(addressField, portField, setAddressButton);
        
        VBox controlsBox = VBoxBuilder
                .create()
                .spacing(spacing)
                .children(clientControls, wifiSettingsPane)
                .build();
        
        final TextArea status = new TextArea();
        status.setEditable(false);
        orcaClient.messageProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                status.appendText(t1);
            }
        });
        
        

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(spacing));
        root.setLeft(orcaDataPane);
        root.setCenter(charts);
        root.setRight(controlsBox);
        root.setBottom(status);
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
