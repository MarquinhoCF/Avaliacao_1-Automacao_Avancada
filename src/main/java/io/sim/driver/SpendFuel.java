package io.sim.driver;

import java.util.Random;

/**
 * A classe SpendFuel é responsável por simular o gasto de combustível de um veículo (Car).
 * O consumo de combustível é simulado por números aleatórios para representar variações realistas no consumo do veículo.
 */
public class SpendFuel extends Thread {
    Car car; // Referência para o objeto Car que será atualizado
    Random random; // Objeto para gerar números aleatórios

    public SpendFuel(Car _car) {
        this.car = _car;
        random = new Random();
    }

    @Override
    public void run() {
        try {
            boolean soNoInicio = true; // Variável para controlar o atraso no início

            while (!car.getEncerrado()) {
                if (soNoInicio) {
                    Thread.sleep(200); // Atraso inicial de 200 milissegundos
                    soNoInicio = false;
                }

                while (car.isOn_off()) { // Enquanto o veículo está ligado e não está abastecendo
                    if (!car.getCarStatus().equals("abastecendo")) {
                        // Gere um número aleatório entre 2.2 e 3.9 para simular o consumo de combustível
                        double consumo = 2.2 + (3.9 - 2.2) * random.nextDouble();
                        car.gastaCombustivel(consumo); // Atualiza o nível de combustível no veículo
                    }
                    Thread.sleep(1000); // Aguarde 1 segundo antes de verificar novamente
                }

                if (!car.isOn_off()) {
                    soNoInicio = true;
                }
            }
            System.out.println("Finalizando SpendFuel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
