package io.sim.driver;

import io.sim.company.Rota;
import io.sim.company.Company;
import java.util.ArrayList;

public class Driver extends Thread {
    // // Cliente de AlphaBank
    // private Account account;
    
    // Atributos da Classe
    private String driverID;
    private Car car;
    private long taxaAquisicao;
    private ArrayList<Rota> rotasDisp = new ArrayList<Rota>();
    private Rota rotaAtual;
    private ArrayList<Rota> rotasTerminadas = new ArrayList<Rota>();
    private boolean initRoute = false;

    public Driver(String _driverID, Car _car, long _taxaAquisicao)
    {
        this.driverID = _driverID;
        this.car = _car;
        this.taxaAquisicao = _taxaAquisicao;
        this.rotasDisp = new ArrayList<Rota>();
        rotaAtual = null;
        this.rotasTerminadas = new ArrayList<Rota>();

        // pensar na logica de inicializacao do TransporteService e do Car
        // BotPayment payment = new BotPayment(fuelPrice);
    }

    @Override
    public void run()
    {
        try {
            System.out.println("Iniciando " + this.driverID);
            Thread t = new Thread(this.car);
            t.start();
            while(Company.temRotasDisponiveis()) {
                Thread.sleep(this.car.getAcquisitionRate());
                if(car.getCarRepport().getCarStatus() == "finalizado") {
                    // retirar de routesInExe e colocar em routesExecuted
                    System.out.println(this.driverID + " rota "+ this.rotasDisp.get(0).getID() + " finalizada");
                    rotasTerminadas.add(rotaAtual);
                    initRoute = false;
                } else if((this.car.getCarRepport().getCarStatus() == "rodando") && !initRoute) {
                    System.out.println(this.driverID + " rota "+ this.car.getRota().getID() +" iniciada");
                    rotaAtual = car.getRota();
                    initRoute = true; 
                }
            }
            car.setTerminado(true);  
            System.out.println("Encerrando " + driverID);
            // this.car.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
