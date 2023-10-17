package io.sim.projeto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import io.sim.bank.Account;
import io.sim.bank.AlphaBank;
import io.sim.driver.Car;

public class FuelStation extends Thread {
    // Atributos da classe
    private Semaphore bombas;
    
    // Atributos como cliente de AlphaBank
    private Socket socket;
    private Account account;
    private int alphaBankServerPort;
    private String alphaBankServerHost; 
    private DataInputStream entrada;
    private DataOutputStream saida;

    public FuelStation(int _alphaBankServerPort, String _alphaBankServerHost) {
        this.bombas = new Semaphore(2); // Duas bombas de combustível disponíveis
        
        // Atributos como cliente de AlphaBank
        alphaBankServerPort = _alphaBankServerPort;
        alphaBankServerHost = _alphaBankServerHost;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fuel Station iniciando...");

            socket = new Socket(this.alphaBankServerHost, this.alphaBankServerPort);
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            this.account = Account.criaAccount("Fuel Station", 0);
            AlphaBank.adicionarAccount(account);
            account.start();
            System.out.println("Company se conectou ao Servido do AlphaBank!!");

            while (true) {
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Encerrando a Fuel Station...");
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
}

