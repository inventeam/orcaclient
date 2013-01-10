package inventeam.orca.client;

import inventeam.orca.client.ORCA;
import inventeam.orca.client.ORCABuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxisBuilder;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxisBuilder;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.util.Duration;

/**
 *
 * @author robin
 */
public class ORCAClient {

    private ORCA orca;
    private ORCABuffer buffer;

    public ORCAClient(ORCA orca, ORCABuffer buffer) {
        this.orca = orca;
        this.buffer = buffer;
    }

    public Pane createRoot(double spacing) {
        final String ipAddressPattern = "\\d{3}.\\d{3}.\\d.\\d";
        final String portPattern = "\\d{2}|\\d{4}";


        final TextArea status = new TextArea();
        status.setEditable(false);
        orca.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                status.appendText(t1);
            }
        });

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(spacing));
        root.setLeft(createORCADatePane(spacing));
        root.setCenter(createChartsPane(spacing));
        root.setRight(createControlsPane(spacing));
        root.setBottom(status);
        return root;
    }

    private Pane createControlsPane(double spacing) {

        Button startClientButton = new Button("Start Client");
        startClientButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                orca.start();
            }
        });
        Button stopClientButton = new Button("Stop Client");
        stopClientButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                orca.stop();
            }
        });

        HBox clientControls = HBoxBuilder
                .create()
                .spacing(spacing)
                .children(startClientButton, stopClientButton)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                orca.stop();
            }
        }));

        return clientControls;
    }

    private Pane createChartsPane(double spacing) {
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

        final XYChart.Series<String, Number> mpsSeries = new XYChart.Series<>();
        mpsSeries.setName("Flow Speed");
        final XYChart.Series<String, Number> rpsSeries = new XYChart.Series<>();
        rpsSeries.setName("Sensor Speed");

        KeyFrame chartUpdateFrame = new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (orca.isRunning()) {
                    ORCABuffer.Capture rpsCapture = buffer.getRotationsPerSecondBuffer().get(buffer.getRotationsPerSecondBuffer().size() - 1);
                    rpsSeries.getData().add(new XYChart.Data<String, Number>(dateFormat.format(rpsCapture.getCaptureDate().getTime()), rpsCapture.getData()));
                    ORCABuffer.Capture mpsCapture = buffer.getMetersPerSecondBuffer().get(buffer.getMetersPerSecondBuffer().size() - 1);
                    mpsSeries.getData().add(new XYChart.Data<String, Number>(dateFormat.format(mpsCapture.getCaptureDate().getTime()), mpsCapture.getData()));
                }
            }
        });

        Timeline chartUpdate = new Timeline(chartUpdateFrame);
        chartUpdate.setCycleCount(Timeline.INDEFINITE);
        chartUpdate.play();

        LineChart<String, Number> mpsChart = new LineChart(
                CategoryAxisBuilder
                .create()
                .label("Date")
                .build(),
                NumberAxisBuilder
                .create()
                .label("Meters Per Second")
                .build());

        mpsChart.setTitle("Flow Speed");
        mpsChart.getData().add(mpsSeries);

        LineChart<String, Number> rpsChart = new LineChart(
                CategoryAxisBuilder
                .create()
                .label("Date")
                .build(),
                NumberAxisBuilder
                .create()
                .label("Rotations Per Sectond")
                .build());

        rpsChart.setTitle("Sensor Speed");
        rpsChart.getData().add(rpsSeries);

        VBox charts = new VBox(spacing);
        charts.getChildren().addAll(mpsChart, rpsChart);

        return charts;
    }

    private Pane createORCADatePane(double spacing) {
        final char degree = 176;

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

        return orcaDataPane;
    }
}
