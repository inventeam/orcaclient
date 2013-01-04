package inventeam.orca;

import inventeam.orca.client.ORCATask;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

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

    public ORCA() {
        //Nothing needs to be done.
    }
    
    public ORCA(String address, int port){
        setAddress(address);
        setPort(port);
    }
    
    public Service<Void> startNewOrcaListener(){
        return new ORCAService(this);
    }

    public SimpleDoubleProperty temperatureProperty() {
        return temerature;
    }

    public double getTemerature() {
        return temerature.get();
    }

    public void setTemerature(double temerature) {
        this.temerature.set(temerature);
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

    private static class ORCAService extends Service<Void> {

        private ORCA orca;
        
        public ORCAService(ORCA orca){
            this.orca = orca;
        }

        @Override
        protected Task<Void> createTask() {
            return new ORCATask(orca, orca.getAddress(), orca.getPort());
        }
    }
}
