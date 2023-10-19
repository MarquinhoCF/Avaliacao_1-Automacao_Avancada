package io.sim;

import io.sim.driver.Car;
import io.sim.driver.Driver;
import io.sim.fuelStation.FuelStation;
import it.polito.appeal.traci.SumoTraciConnection;

import java.util.ArrayList;
import java.util.Random;

import de.tudresden.sumo.objects.SumoColor;

public class DriverANDCarCreator {
    public static ArrayList<Driver> criaListaDrivers(int qtdDrivers, FuelStation fuelStation, long taxaAquisicao, SumoTraciConnection sumo, String host, int portaCompanny, int portaAlphaBank) {
        ArrayList<Driver> drivers = new ArrayList<>();

        for (int i = 0; i < qtdDrivers; i++) {
            String driverID = "Driver " + (i + 1);
            String carID = "Car " + (i + 1);
            Car car = createCar(carID, driverID, taxaAquisicao, sumo, host, portaCompanny);

            Driver driver = new Driver(driverID, car, taxaAquisicao, fuelStation, portaAlphaBank, host);
            drivers.add(driver);
            //driver.start();
        }

        return drivers;
    }

    // Método estático para criar um de objeto Car com cores aleatórias
    public static Car createCar(String idCar, String driverID, long taxaAquisicao, SumoTraciConnection sumo, String host, int companyServerPort) {
        try {
            // Defina as características comuns para os novos objetos Auto
            boolean on_off = false;
            int fuelType = 2; // Gasolina
            int fuelPreferential = 2; // Gasolina
            double fuelPrice = 3.40;
            int personCapacity = 1;
            int personNumber = 1;

            Random random = new Random();

            // Gere uma cor aleatória
            SumoColor randomColor = new SumoColor(
                    random.nextInt(256), // Valor de vermelho entre 0 e 255
                    random.nextInt(256), // Valor de verde entre 0 e 255
                    random.nextInt(256), // Valor de azul entre 0 e 255
                    126 // Valor de alfa (transparência) fixo em 126
            );
        
            // Crie um novo objeto Car com características comuns e cor aleatória
            Car car = new Car(on_off, idCar, randomColor, driverID, sumo, taxaAquisicao, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber, host, companyServerPort);
            return car;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}