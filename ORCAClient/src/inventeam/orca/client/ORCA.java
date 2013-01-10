package inventeam.orca.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 *
 * @author robin
 */
public class ORCA {

    private SimpleDoubleProperty temerature = new SimpleDoubleProperty();
    private SimpleDoubleProperty batteryTemperature = new SimpleDoubleProperty();
    private SimpleDoubleProperty batteryVoltage = new SimpleDoubleProperty();
    private SimpleDoubleProperty direction = new SimpleDoubleProperty();
    private SimpleDoubleProperty rotationsPerSecond = new SimpleDoubleProperty();
    private SimpleDoubleProperty metersPerSecond = new SimpleDoubleProperty();
    private SimpleStringProperty address = new SimpleStringProperty("192.168.2.2");
    private SimpleIntegerProperty port = new SimpleIntegerProperty(80);
    private InputStream clientInputStream;
    private OutputStream clientOutputStream;
    private Socket socket;
    private StringProperty messageProperty = new SimpleStringProperty();
    private Timeline clientLoop;
    private boolean running = false;

    public ORCA() {
        //Nothing needs to be done here...
    }

    public ORCA(String address, int port) {
        this.address.set(address);
        this.port.set(port);
    }

    private String connectionInfo() {
        return getAddress() + ":" + getPort();
    }

    private static String getInetIPAsString(InetAddress inetAddress) {
        StringBuilder ipBuilder = new StringBuilder();
        byte[] ipAddress = inetAddress.getAddress();

        for (int i = 0; i < ipAddress.length; i++) {
            byte b = ipAddress[i];
            ipBuilder.append(Byte.toString(b));
            if (i == 2 || i == 5 || i == 6) {
                ipBuilder.append(".");
            }
        }

        return ipBuilder.toString();
    }

    private void connect() {
        setMessage("Attempting to connect to ORCA at: " + connectionInfo());
        try {
            socket = new Socket(getAddress(), getPort());
            clientInputStream = socket.getInputStream();
            clientOutputStream = socket.getOutputStream();
        } catch (UnknownHostException ex) {
            setMessage("Could not connect to:" + connectionInfo());
            return;
        } catch (IOException ex) {
            return;
        }

        clientLoop = TimelineBuilder.create().cycleCount(Timeline.INDEFINITE).build();
        KeyFrame loopFrame = new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    checkClient();
                } catch (IOException ex) {
                }
            }
        });
        clientLoop.getKeyFrames().add(loopFrame);
        running = true;
    }

    public void start() {
        connect();
        clientLoop.playFromStart();
    }

    public void stop() {
        if (clientLoop != null) {
            clientLoop.stop();
            disconnect();
        }
    }

    private void disconnect() {
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                clientInputStream.close();
                clientOutputStream.close();
                socket.close();
            } catch (IOException ex) {
                return;
            }
        }
        running = false;
    }

    private void checkClient() throws IOException {
        int data;
        if (getClientInputStream().available() >= 6) {
            data = getClientInputStream().read();
            if (data == 255) {
                data = getClientInputStream().read();
                if (data == 1) {
                    data = 0;
                    data = getClientInputStream().read();
                    data = data ^ (getClientInputStream().read() << 8);
                    data = data ^ (getClientInputStream().read() << 16);
                    data = data ^ (getClientInputStream().read() << 24);
                    setTemerature(data / 100.0);
                } else if (data == 2) {
                    data = 0;
                    data = getClientInputStream().read();
                    data = data ^ (getClientInputStream().read() << 8);
                    data = data ^ (getClientInputStream().read() << 16);
                    data = data ^ (getClientInputStream().read() << 24);
                    setBatteryTemperature(data / 100.0);
                } else if (data == 3) {
                    data = 0;
                    data = getClientInputStream().read();
                    data = data ^ (getClientInputStream().read() << 8);
                    data = data ^ (getClientInputStream().read() << 16);
                    data = data ^ (getClientInputStream().read() << 24);
                    setBatteryVoltage(data / 100.0);
                } else if (data == 4) {
                    data = 0;
                    data = getClientInputStream().read();
                    data = data ^ (getClientInputStream().read() << 8);
                    data = data ^ (getClientInputStream().read() << 16);
                    data = data ^ (getClientInputStream().read() << 24);
                    setDirection(data / 100.0);
                } else if (data == 5) {
                    data = 0;
                    data = getClientInputStream().read();
                    data = data ^ (getClientInputStream().read() << 8);
                    data = data ^ (getClientInputStream().read() << 16);
                    data = data ^ (getClientInputStream().read() << 24);
                    double rps = data / 1000.0;
                    double mps = .3007 * rps;
                    if (mps > 0) {
                        mps += .118;
                    }
                    setRotationsPerSecond(rps);
                    setMetersPerSecond(mps);
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public SimpleDoubleProperty temperatureProperty() {
        return temerature;
    }

    public double getTemerature() {
        return temerature.get();
    }

    public void setTemerature(double temerature) {
        this.temerature.setValue(temerature);
    }

    public SimpleDoubleProperty batteryTemperatureProperty() {
        return batteryTemperature;
    }

    public double getBatteryTemperature() {
        return batteryTemperature.get();
    }

    public void setBatteryTemperature(double batteryTemperature) {
        this.batteryTemperature.set(batteryTemperature);
    }

    public SimpleDoubleProperty batteryVoltageProperty() {
        return batteryVoltage;
    }

    public double getBatteryVoltage() {
        return batteryVoltage.get();
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage.set(batteryVoltage);
    }

    public SimpleDoubleProperty directionProperty() {
        return direction;
    }

    public double getDirection() {
        return direction.get();
    }

    public void setDirection(double direction) {
        this.direction.set(direction);
    }

    public SimpleDoubleProperty rotationsPerSecondProperty() {
        return rotationsPerSecond;
    }

    public double getRotationsPerSecond() {
        return rotationsPerSecond.get();
    }

    public void setRotationsPerSecond(double rotationsPerSecond) {
        this.rotationsPerSecond.set(rotationsPerSecond);
    }

    public SimpleDoubleProperty metersPerSecondProperty() {
        return metersPerSecond;
    }

    public double getMetersPerSecond() {
        return metersPerSecond.get();
    }

    public void setMetersPerSecond(double metersPerSecond) {
        this.metersPerSecond.set(metersPerSecond);
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public SimpleIntegerProperty portProperty() {
        return port;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getClientInputStream() {
        return clientInputStream;
    }

    public OutputStream getClientOutputStream() {
        return clientOutputStream;
    }

    public StringProperty messageProperty() {
        return messageProperty;
    }

    public String getMessage() {
        return messageProperty.getValue();
    }

    private void setMessage(String message) {
        this.messageProperty.setValue(message);
    }

    public boolean isRunning() {
        return running;
    }
    //</editor-fold>
}
