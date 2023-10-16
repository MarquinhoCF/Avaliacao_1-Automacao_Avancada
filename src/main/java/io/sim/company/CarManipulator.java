package io.sim.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import io.sim.driver.DrivingData;
import io.sim.driver.TransportService;

public class CarManipulator extends Thread {
    private Socket carSocket;
    private DataInputStream input;
    private DataOutputStream output;

    private Company company;

    // Atributos para sincronização
    private Object sincroniza = new Object();

    public CarManipulator(Socket _carSocket, Company _company) {
        this.company = _company;
        this.carSocket = _carSocket;
        try {
            input = new DataInputStream(carSocket.getInputStream());
            output = new DataOutputStream(carSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sincroniza = new Object();
    }

    @Override
    public void run() {
        try {
            // variaveis de entrada e saida do servidor
            ObjectInputStream entrada = new ObjectInputStream(carSocket.getInputStream());
            DataOutputStream saida = new DataOutputStream(carSocket.getOutputStream());

            String mensagem = "";

             // loop principal
            while(!mensagem.equals("encerrado")) {
                System.out.println("Aguardando mensagem...");
                DrivingData objIn = (DrivingData) entrada.readObject();
                // verifica distancia para pagamento
                mensagem = objIn.getCarStatus(); // lê solicitacao do cliente
                System.out.println("SMC ouviu " + mensagem);
                if (mensagem.equals("aguardando")) {
                    synchronized (sincroniza) {
                        Rota resposta = company.executarRota();
                        
                        System.out.println("SMC - Liberando rota:\n" + resposta.getID());
                        saida.writeUTF(company.transfRota2String(resposta));
                    }
                } else if(mensagem.equals("finalizado")) {
                    String routeID = objIn.getRouteIDSUMO();
                    System.out.println("SMC - Rota " + routeID + " finalizada.");
                    this.company.terminarRota(routeID);
                } else if(mensagem.equals("rodando")) {
                    // a principio, nao faz nada
                } else if (mensagem.equals("encerrado")) {
                    break;
                }
            }

            System.out.println("Encerrando canal.");
            entrada.close();
            saida.close();
            carSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}