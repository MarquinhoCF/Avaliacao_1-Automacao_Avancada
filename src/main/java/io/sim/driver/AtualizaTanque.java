package io.sim.driver;

public class AtualizaTanque extends Thread {
    Car car;
    double litros;

    public AtualizaTanque(Car _car, double _litros) {
        this.car = _car;
        this.litros = _litros;
    }

    @Override
    public void run() {
        try {
            while (!car.getFinalizado()) {
                System.out.println("TRAVOU AQUI");
                while (car.isOn_off()) {
                    System.out.println("Velocidade do carro: " + car.getSpeed());
                    if (car.getSpeed() != 0) {
                        car.gastaCombustivel(litros);
                    }
                    Thread.sleep(1000);
                }
                System.out.println("Não executou o While");
            }
            System.out.println("Finalizando Atualiza Fuel Tank");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
