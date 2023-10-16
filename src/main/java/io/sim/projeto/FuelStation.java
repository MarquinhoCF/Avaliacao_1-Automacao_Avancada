package io.sim.projeto;

import java.util.concurrent.Semaphore;

import io.sim.driver.Car;

public class FuelStation extends Thread {
    private Semaphore bombas;
    //private AlphaBankClient alphaBankClient;

    public FuelStation() {
        this.bombas = new Semaphore(2); // Duas bombas de combustível disponíveis
        //this.alphaBankClient = alphaBankClient;
    }

    public void refuelCar(Car car) {
        try {
            bombas.acquire(); // Tenta adquirir uma bomba de combustível
            System.out.println("Car " + car.getIdCar() + " está abastecendo no Posto de Gasolina");
            car.abastecendo();
            Thread.sleep(120000); // Tempo de abastecimento de 2 minutos (em milissegundos)
            car.abastecido();
            System.out.println("Car " + car.getIdCar() + " terminou de abastecer");
            bombas.release(); // Libera a bomba de combustível
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Implemente a lógica para a Fuel Station realizar transações com o AlphaBank
        // para receber pagamentos dos Drivers.
        // Você pode usar o alphaBankClient para se comunicar com o AlphaBank.
    }
}

