package inventeam.orca.client;

import inventeam.orca.ORCA;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.concurrent.Task;

/**
 *
 * @author robin
 */
public class ORCATask extends Task<Void> {

    private InputStream clientInputStream = null;
    private OutputStream clientOutputStream = null;
    private ORCA orca;
    private Socket socket = null;
    private final String address;
    private final int port;

    public ORCATask(ORCA orca, String address, int port) {
        this.orca = orca;
        this.address = address;
        this.port = port;
    }

    @Override
    protected Void call() throws Exception {
        setupClient();
        while (!isCancelled()) {
            checkORCA();
        }
        disconnect();
        return null;
    }

    private void setupClient() {
        try {
            socket = new Socket(address, port);
            updateMessage("Connected at " + address);
            clientInputStream = socket.getInputStream();
            clientOutputStream = socket.getOutputStream();
        } catch (UnknownHostException ex) {
            updateMessage(ex.getMessage());
        } catch (IOException ex) {
            updateMessage(ex.getMessage());
        }
    }

    private void checkORCA() throws IOException {
        int data;
        if (clientInputStream.available() >= 6) {
            data = clientInputStream.read();
            if (data == 255) {
                data = clientInputStream.read();
                if (data == 1) {
                    data = 0;
                    data = clientInputStream.read();
                    data = data ^ (clientInputStream.read() << 8);
                    data = data ^ (clientInputStream.read() << 16);
                    data = data ^ (clientInputStream.read() << 24);
                    orca.setTemerature(data / 100.0);
                } else if (data == 2) {
                    data = 0;
                    data = clientInputStream.read();
                    data = data ^ (clientInputStream.read() << 8);
                    data = data ^ (clientInputStream.read() << 16);
                    data = data ^ (clientInputStream.read() << 24);
                    orca.setBatteryTemperature(data / 100.0);
                } else if (data == 3) {
                    data = 0;
                    data = clientInputStream.read();
                    data = data ^ (clientInputStream.read() << 8);
                    data = data ^ (clientInputStream.read() << 16);
                    data = data ^ (clientInputStream.read() << 24);
                    orca.setBatteryVoltage(data / 100.0);
                } else if (data == 4) {
                    data = 0;
                    data = clientInputStream.read();
                    data = data ^ (clientInputStream.read() << 8);
                    data = data ^ (clientInputStream.read() << 16);
                    data = data ^ (clientInputStream.read() << 24);
                    orca.setDirection(data / 100.0);
                } else if (data == 5) {
                    data = 0;
                    data = clientInputStream.read();
                    data = data ^ (clientInputStream.read() << 8);
                    data = data ^ (clientInputStream.read() << 16);
                    data = data ^ (clientInputStream.read() << 24);
                    double rps = data / 1000.0;
                    double mps = .3007 * rps;
                    if (mps > 0) {
                        mps += .118;
                    }
                    orca.setRotationsPerSecond(rps);
                    orca.setMetersPerSecond(mps);
                }
            }
        }
    }

    private void disconnect() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            clientInputStream.close();
            clientOutputStream.close();
            socket.close();
        } catch (IOException ex) {
            updateMessage(ex.getMessage());
        }
    }
}
