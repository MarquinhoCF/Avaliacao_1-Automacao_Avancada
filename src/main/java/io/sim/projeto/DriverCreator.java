package io.sim.projeto;

import io.sim.Car;
import it.polito.appeal.traci.SumoTraciConnection;

import java.util.ArrayList;

public class DriverCreator {
    public static ArrayList<Driver> createDrivers(SumoTraciConnection sumo, ArrayList<Car> cars) {
        ArrayList<Driver> drivers = new ArrayList<>();

        for (int i = 1; i < cars.size(); i++) {
            String driverID = "Driver" + i;
            Car car = cars.get(i);
            car.setDriverID(driverID);

            Driver driver = new Driver(sumo, driverID, car);
            drivers.add(driver);
            driver.start();
        }

        return drivers;
    }
}