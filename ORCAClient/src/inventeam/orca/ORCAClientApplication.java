package inventeam.orca;

import inventeam.NetworkScanner;
import inventeam.orca.client.ORCAClient;
import inventeam.orca.client.ORCA;
import inventeam.orca.client.ORCABuffer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author robin
 */
public class ORCAClientApplication extends Application {

    private ORCA orca;
    private ORCABuffer buffer;

    @Override
    public void start(final Stage primaryStage) {
        NetworkScanner networkScanner = new NetworkScanner();
        orca = new ORCA();
        buffer = new ORCABuffer(orca);

        ORCAClient client = new ORCAClient(orca, buffer);
        final Pane networkScannerPane = networkScanner.createRoot(10);

        final Pane clientPane = client.createRoot(10);
        clientPane.setVisible(false);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(networkScannerPane, clientPane);

        final FadeTransition networkFade = new FadeTransition(Duration.millis(1000));
        networkFade.setNode(networkScannerPane);
        networkFade.setFromValue(1);
        networkFade.setToValue(0);
        final FadeTransition clientFade = new FadeTransition(Duration.millis(1000));
        clientFade.setNode(clientPane);
        clientFade.setFromValue(0);
        clientFade.setToValue(1);


        networkScanner.finishedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                networkFade.play();
                clientPane.setVisible(true);
                clientFade.play();
            }
        });

        networkFade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                networkScannerPane.setVisible(false);
            }
        });

        Scene scene = new Scene(stackPane);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
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
