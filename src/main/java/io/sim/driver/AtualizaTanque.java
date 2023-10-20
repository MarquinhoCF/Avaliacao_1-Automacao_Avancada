package io.sim.driver;

import java.util.Random;

public class AtualizaTanque extends Thread {
    Car car;
    //double litros;
    Random random;

    public AtualizaTanque(Car _car) {
        this.car = _car;
        //this.litros = _litros;
        random = new Random();
    }

    @Override
    public void run() {
        try {
            boolean soNoInicio = true;
            while (!car.getFinalizado()) {
                if (soNoInicio) {
                    Thread.sleep(200);
                    soNoInicio = false;
                }

                while (car.isOn_off()) {
                    if (!car.getCarStatus().equals("abastecendo")) {
                        // Gere um número aleatório entre 2.2 e 3.9
                        double consumo = 2.2 + (3.9 - 2.2) * random.nextDouble();
                        car.gastaCombustivel(consumo);
                        //car.gastaCombustivel(this.car.getConsumoCombustivel());
                    }
                    Thread.sleep(1000);
                }

                if (!car.isOn_off()) {
                    soNoInicio = true;
                }
            }
            System.out.println("Finalizando Atualiza Fuel Tank");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
