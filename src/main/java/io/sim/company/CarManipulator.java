package io.sim.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

public class CarManipulator extends Thread {
    private Socket carSocket;
    private DataInputStream entrada;
    private DataOutputStream saida;

    private Company company;

    // Atributos para sincronização
    private Object sincroniza = new Object();

    public CarManipulator(Socket _carSocket, Company _company) {
        this.company = _company;
        this.carSocket = _carSocket;
        try {
            // variaveis de entrada e saida do servidor
            entrada = new DataInputStream(carSocket.getInputStream());
            saida = new DataOutputStream(carSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sincroniza = new Object();
    }

    @Override
    public void run() {
        try {
            String StatusDoCarro = "";
            double distanciaAnterior = 0;

             // loop principal
            while(!StatusDoCarro.equals("encerrado")) {
                // System.out.println("Aguardando mensagem...");
                JSONObject jsonComunicacao = new JSONObject((String) entrada.readUTF());
                StatusDoCarro = jsonComunicacao.getString("Status do Carro"); // lê solicitacao do cliente
                
                long distanciaPercorrida = jsonComunicacao.getLong("Distancia Percorrida");
                if (distanciaPercorrida > distanciaAnterior) {
                    String driverID = jsonComunicacao.getString("Driver ID");
                    company.fazerPagamento(driverID);
                    distanciaAnterior = distanciaPercorrida;
                } else {
                    distanciaAnterior = distanciaPercorrida;
                }
                
                if (StatusDoCarro.equals("aguardando")) {
                    if(!Company.temRotasDisponiveis()) {
                        System.out.println("SMC - Sem mais rotas para liberar.");
                        Rota rota = new Rota("-1", "00000");
                        saida.writeUTF(criaJSONRota(rota).toString());
                        break;
                    }

                    if(Company.temRotasDisponiveis()) {
                        synchronized (sincroniza) {
                            Rota resposta = company.executarRota();
                            saida.writeUTF(criaJSONRota(resposta).toString());
                        }
                    }
                } else if(StatusDoCarro.equals("finalizado")) {
                    String routeID = jsonComunicacao.getString("ID da Rota");
                    System.out.println("SMC - Rota " + routeID + " finalizada.");
                    company.terminarRota(routeID);
                    System.out.println("Aguardando mensagem...");
                } else if(StatusDoCarro.equals("rodando")) {
                    // a principio, nao faz nada
                } else if (StatusDoCarro.equals("encerrado")) {
                    break;
                }
            }

            System.out.println("Encerrando canal.");
            entrada.close();
            saida.close();
            carSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject criaJSONRota(Rota rota) {
        JSONObject rotaJSON = new JSONObject();
        rotaJSON.put("ID da Rota", rota.getID());
        rotaJSON.put("Edges", rota.getEdges());
        return rotaJSON;
    }
    
}