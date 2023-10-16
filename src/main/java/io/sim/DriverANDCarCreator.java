package io.sim;

import io.sim.driver.Car;
import io.sim.driver.Driver;
import it.polito.appeal.traci.SumoTraciConnection;

import java.util.ArrayList;

public class DriverANDCarCreator {
    public static ArrayList<Driver> createDriversANDCars(int qtdDrivers, long taxaAquisicao, SumoTraciConnection sumo, String companyServerHost, int portaCompanny) {
        ArrayList<Driver> drivers = new ArrayList<>();

        for (int i = 0; i < qtdDrivers; i++) {
            String driverID = "Driver " + (i + 1);
            String carID = "Car " + (i + 1);
            Car car = CarCreator.createCar(carID, driverID, taxaAquisicao, sumo, companyServerHost, portaCompanny);

            Driver driver = new Driver(driverID, car, taxaAquisicao);
            drivers.add(driver);
            driver.start();
        }

        return drivers;
    }
}