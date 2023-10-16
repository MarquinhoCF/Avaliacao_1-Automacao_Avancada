package io.sim;

import de.tudresden.sumo.objects.SumoColor;
import io.sim.driver.Car;
import it.polito.appeal.traci.SumoTraciConnection;

import java.util.Random;

public class CarCreator {

    // Método estático para criar um de objeto Car com cores aleatórias
    public static Car createCar(String idCar, String driverID, long taxaAquisicao, SumoTraciConnection sumo, String companyServerHost, int companyServerPort) {
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
            Car car = new Car(on_off, idCar, randomColor, driverID, sumo, taxaAquisicao, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber, companyServerHost, companyServerPort);
            return car;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        
    }
}