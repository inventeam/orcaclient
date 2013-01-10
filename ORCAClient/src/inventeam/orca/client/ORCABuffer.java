package inventeam.orca.client;

import java.util.Calendar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author robin
 */
public class ORCABuffer {

    private ObservableList<Capture> temeratureBuffer = FXCollections.observableArrayList();
    private ObservableList<Capture> batteryTemperatureBuffer = FXCollections.observableArrayList();
    private ObservableList<Capture> batteryVoltageBuffer = FXCollections.observableArrayList();
    private ObservableList<Capture> directionBuffer = FXCollections.observableArrayList();
    private ObservableList<Capture> rotationsPerSecondBuffer = FXCollections.observableArrayList();
    private ObservableList<Capture> metersPerSecondBuffer = FXCollections.observableArrayList();
    private ORCA orca;

    public ORCABuffer(ORCA orca) {
        this.orca = orca;
        init();
    }

    private void init() {
        orca.batteryTemperatureProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                batteryTemperatureBuffer.add(new Capture(t1.doubleValue()));
            }
        });
        orca.batteryVoltageProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                batteryVoltageBuffer.add(new Capture(t1.doubleValue()));
            }
        });
        orca.directionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                directionBuffer.add(new Capture(t1.doubleValue()));
            }
        });
        orca.metersPerSecondProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                metersPerSecondBuffer.add(new Capture(t1.doubleValue()));
            }
        });
        orca.rotationsPerSecondProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                rotationsPerSecondBuffer.add(new Capture(t1.doubleValue()));
            }
        });
        orca.temperatureProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                temeratureBuffer.add(new Capture(t1.doubleValue()));
            }
        });
    }

    public ObservableList<Capture> getBatteryTemperatureBuffer() {
        return batteryTemperatureBuffer;
    }

    public ObservableList<Capture> getBatteryVoltageBuffer() {
        return batteryVoltageBuffer;
    }

    public ObservableList<Capture> getDirectionBuffer() {
        return directionBuffer;
    }

    public ObservableList<Capture> getMetersPerSecondBuffer() {
        return metersPerSecondBuffer;
    }

    public ObservableList<Capture> getRotationsPerSecondBuffer() {
        return rotationsPerSecondBuffer;
    }

    public ObservableList<Capture> getTemeratureBuffer() {
        return temeratureBuffer;
    }

    public static class Capture {

        private double data;
        private Calendar captureDate = Calendar.getInstance();

        public Capture(double data) {
            this.data = data;
        }

        public Calendar getCaptureDate() {
            return captureDate;
        }

        public double getData() {
            return data;
        }
    }
}
