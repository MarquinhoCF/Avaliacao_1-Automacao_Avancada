package io.sim.driver;

import io.sim.company.Rota;
import io.sim.bank.Account;
import io.sim.bank.AlphaBank;
import io.sim.company.Company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;


public class Driver extends Thread {
    // Cliente de AlphaBank
    private Account account;
    private Socket socket;
    private int alphaBankServerPort;
    private String alphaBankServerHost; 
    private DataInputStream entrada;
    private DataOutputStream saida;
    
    // Atributos da Classe
    private String driverID;
    private Car car;
    private long taxaAquisicao;
    private ArrayList<Rota> rotasDisp = new ArrayList<Rota>();
    private Rota rotaAtual;
    private ArrayList<Rota> rotasTerminadas = new ArrayList<Rota>();
    private boolean initRoute = false;
    private long saldoInicial;

    public Driver(String _driverID, Car _car, long _taxaAquisicao, int _alphaBankServerPort, String _alphaBankServerHost) {
        this.driverID = _driverID;
        this.car = _car;
        this.taxaAquisicao = _taxaAquisicao;
        this.rotasDisp = new ArrayList<Rota>();
        rotaAtual = null;
        this.rotasTerminadas = new ArrayList<Rota>();
        this.saldoInicial = 0;
        this.alphaBankServerPort = _alphaBankServerPort;
        this.alphaBankServerHost = _alphaBankServerHost;

        // pensar na logica de inicializacao do TransporteService e do Car
        // BotPayment payment = new BotPayment(fuelPrice);
    }

    @Override
    public void run() {
        try {
            System.out.println("Iniciando " + this.driverID);
            
            socket = new Socket(this.alphaBankServerHost, this.alphaBankServerPort);
            entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());
            this.account = Account.criaAccount(driverID, saldoInicial);
            AlphaBank.adicionarAccount(account);
            account.start();
            System.out.println(driverID + " se conectou ao Servido do AlphaBank!!");
            
            Thread t = new Thread(this.car);
            t.start();

            while(Company.temRotasDisponiveis()) {
                Thread.sleep(this.car.getAcquisitionRate());
                if(car.getCarStatus() == "finalizado") {
                    System.out.println(this.driverID + " rota " + this.rotasDisp.get(0).getID() + " finalizada");
                    rotasTerminadas.add(rotaAtual);
                    initRoute = false;
                } else if((this.car.getCarStatus() == "rodando") && !initRoute) {
                    System.out.println(this.driverID + " rota "+ this.car.getRota().getID() +" iniciada");
                    rotaAtual = car.getRota();
                    initRoute = true; 
                }
                System.out.println(account.getAccountID() + " tem R$" + account.getSaldo() + " de saldo");
            }
            car.setFinalizado(true);  
            System.out.println("Encerrando " + driverID);
            // this.car.join();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
