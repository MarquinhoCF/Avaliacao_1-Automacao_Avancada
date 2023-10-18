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
    private double preco;
    
    // Atributos como cliente de AlphaBank
    private Socket socket;
    private Account account;
    private int alphaBankServerPort;
    private String alphaBankServerHost; 
    private DataInputStream entrada;
    private DataOutputStream saida;

    public FuelStation(int _alphaBankServerPort, String _alphaBankServerHost) {
        this.bombas = new Semaphore(2); // Duas bombas de combustível disponíveis
        this.preco = 5.87;
        
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
            System.out.println("Fuel Station se conectou ao Servido do AlphaBank!!");

            while (true) {
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Encerrando a Fuel Station...");
    }

    public double abastecerCarro(Car car, double litros) {
        try {
            bombas.acquire(); // Tenta adquirir uma bomba de combustível
            System.out.println("Car " + car.getIdCar() + " está abastecendo no Posto de Gasolina");
            car.setSpeed(0);
            Thread.sleep(30000); // Tempo de abastecimento de 2 minutos (120000 em milissegundos)
            car.abastecido();
            System.out.println("Car " + car.getIdCar() + " terminou de abastecer");
            bombas.release(); // Libera a bomba de combustível
            return (litros * preco)/1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FS - Erro no abastecimento do " + car.getIdCar());
        return 0;
    }

    public String getFSAccountID() {
        return this.account.getAccountID();
    }
}

