package inventeam;

import java.net.Inet4Address;
import java.text.NumberFormat;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author robin
 */
public class NetworkScanner {

    private Service<Void> scanService;
    private ListView<String> ipsFound;
    private SimpleBooleanProperty finishedProperty = new SimpleBooleanProperty(false);
    private SimpleStringProperty orcaIPProperty = new SimpleStringProperty("192.168.2.2");
    private SimpleIntegerProperty orcaPortProperty = new SimpleIntegerProperty(80);

    public NetworkScanner() {
        initScanService();
    }

    private void initScanService() {
        scanService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        int[] ip = {192, 168, 1, 1}; //This might need to be changed...
                        for (int i = 1; i < 255; i++) {
                            if (isCancelled()) {
                                break;
                            }
                            updateProgress(i, 255);
                            ip[3] = i;
                            final String ipString = getIPFromArray(ip);
                            updateMessage("Testing: " + ipString + "...");
                            final Inet4Address address = (Inet4Address) Inet4Address.getByName(ipString);
                            boolean reachable = address.isReachable(100);
                            if (reachable) {
                                updateMessage("\tSucceded.");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        ipsFound.getItems().add(ipString);
                                    }
                                });
                            }
                        }
                        return null;
                    }
                };
            }
        };
    }

    private static String getIPFromArray(int[] ip) {
        if (ip.length == 4) {
            StringBuilder ipBuilder = new StringBuilder();
            for (int i = 0; i < ip.length; i++) {
                ipBuilder.append(Integer.toString(ip[i]));
                if (i < ip.length - 1) {
                    ipBuilder.append(".");
                }
            }
            return ipBuilder.toString();
        }
        return null;
    }

    public Pane createRoot(double spacing) {

        ProgressBar scanProgress = new ProgressBar();
        scanProgress.progressProperty().bind(scanService.progressProperty());
        Label scanStatus = new Label();
        scanStatus.textProperty().bind(scanService.messageProperty());
        Label ipLabel = new Label("ORCA IP: ");
        final TextField orcaIPField = new TextField();
        orcaIPField.setPromptText(orcaIPProperty.getValue());
        orcaIPField.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                orcaIPProperty.setValue(orcaIPField.getText());
            }
        });
        final TextField portField = new TextField();
        portField.setPromptText(orcaPortProperty.getValue().toString());
        portField.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                setOrcaPort(Integer.parseInt(portField.getText()));
            }
        });

        ipsFound = new ListView<>();
        ipsFound.setPrefHeight(200);

        scanService.start();

        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                setFinished(true);
                scanService.cancel();
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                setFinished(true);
                System.exit(0);
            }
        });
        
        HBox ipSettings = new HBox(spacing);
        ipSettings.getChildren().addAll(ipLabel, orcaIPField, new Label(":"), portField);


        VBox root = new VBox(spacing);
        root.setPadding(new Insets(spacing));
        root.getChildren().addAll(
                scanStatus,
                scanProgress,
                new Label("Devices Found: "),
                ipsFound, 
                ipSettings,
                HBoxBuilder.create().spacing(spacing).children(okButton, cancelButton).build());
        return root;
    }

    public SimpleBooleanProperty finishedProperty() {
        return finishedProperty;
    }

    public boolean getFinished() {
        return finishedProperty.getValue();
    }

    private void setFinished(boolean finished) {
        finishedProperty.setValue(finished);
    }

    public SimpleStringProperty orcaIPProperty() {
        return orcaIPProperty;
    }

    public String getOrcaIP() {
        return orcaIPProperty.getValue();
    }

    public void setOrcaIP(String orcaIP) {
        this.orcaIPProperty.setValue(orcaIP);
    }

    public SimpleIntegerProperty orcaPortProperty() {
        return orcaPortProperty;
    }

    public int getOrcaPort() {
        return orcaPortProperty.getValue();
    }

    public void setOrcaPort(int orcaPort) {
        this.orcaPortProperty.setValue(orcaPort);
    }
}
