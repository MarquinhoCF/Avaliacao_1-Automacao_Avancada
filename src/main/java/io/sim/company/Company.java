package io.sim.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import io.sim.bank.Account;
import io.sim.bank.AlphaBank;

public class Company extends Thread {
    // Atributos de Servidor
    private ServerSocket serverSocket;

    // Atributos para sincronização
    private Object sincroniza = new Object();
    private static boolean rotasDisponiveis = true;
    private boolean canectandoCars;

    // Atributos da classe
    private static ArrayList<Rota> rotasDisp;
    private static ArrayList<Rota> rotasEmExec;
    private static ArrayList<Rota> rotasTerminadas;
    private static double preco;
    private static int numDrivers;

    // Atributos como cliente de AlphaBank
    private Socket socket;
    private Account account;
    private int alphaBankServerPort;
    private String alphaBankServerHost; 
    private DataInputStream entrada;
    private DataOutputStream saida;
    
    public Company(ServerSocket serverSocket, String xmlPath, int _numDrivers, int _alphaBankServerPort, String _alphaBankServerHost) {
        // Inicializa servidor
        this.serverSocket = serverSocket;

        // Inicializa atributos de sincronização
        this.sincroniza = new Object();
        rotasDisponiveis = true;
        this.canectandoCars = true;

        // Atributos da classe
        rotasDisp = Rota.criaRotasXML(xmlPath);
		System.out.println("Rotas: "+ rotasDisp.size()+" rotas disponiveis");
        rotasEmExec = new ArrayList<Rota>();
        rotasTerminadas = new ArrayList<Rota>();
        preco = 3.25;
        numDrivers = _numDrivers;

        // Atributos como cliente de AlphaBank
        alphaBankServerPort = _alphaBankServerPort;
        alphaBankServerHost = _alphaBankServerHost;
        account = Account.criaAccount("Company", 100000);
    }

    @Override
    public void run() {
        try {
            System.out.println("Company iniciando...");

            socket = new Socket(this.alphaBankServerHost, this.alphaBankServerPort);
            entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());
            saida.writeUTF(Account.criaJSONAccount(account).toString());
            System.out.println("Company se conectou ao Servido do AlphaBank!!");

            while (rotasDisponiveis) {
                // Pequeno delay para evitar problemas
                if(!canectandoCars) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Verifica se ainda tem roas disponíveis
                if(rotasDisp.size() == 0 && rotasEmExec.size() == 0) {
                    System.out.println("Rotas terminadas");
                    rotasDisponiveis = false;
                }

                // Talvez passar essa função pra outra classe
                if(canectandoCars) {
                    for(int i = 0; i < numDrivers; i++) {
                        // conecta os clientes -> IMP mudar para ser feito paralelamente (ou n)
                        System.out.println("Company - Esperando para conectar " + (i + 1));
                        Socket socket = serverSocket.accept();
                        System.out.println("Car conectado");

                        // Cria uma thread para comunicacao de cada Car
                        CarManipulator carManipulator = new CarManipulator(socket, this);
                        carManipulator.start();
                    }
                    System.out.println("Company: Todos os drivers criados");
                    canectandoCars = false;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Encerrando a Company...");
    }

    public static boolean temRotasDisponiveis() {
        return rotasDisponiveis;
    }

    public String transfRota2String(Rota _route) {
        String stringRota;
        stringRota = _route.getID() + "," + _route.getEdges();
        return stringRota;
    }

    // Libera uma rota para o cliente que a solicitou. Para isso, remove de "rotasDisp" e adiciona em "rotasEmExec"
    public Rota executarRota() {
        synchronized (sincroniza) {
            Rota rota = rotasDisp.remove(0);
            rotasEmExec.add(rota);
            return rota;
        }
    }

    public void terminarRota(String _routeID) {
        synchronized (sincroniza) {
            System.out.println("Arquivando rota: " + _routeID);
            int i = 0;
            while (!rotasEmExec.get(i).getID().equals(_routeID)) {
                i++;
            }
            rotasTerminadas.add(rotasEmExec.remove(i));
        }
    }

    public void fazerPagamento(String driverID) throws IOException {
        // Ao invés disso deveria chamar o BotPayment!!
        JSONObject transferencia = AlphaBank.criaJSONTransferencia("Transferencia", account.getAccountID(), account.getSenha(), driverID, this.preco);
        saida.writeUTF(transferencia.toString());
    }
}