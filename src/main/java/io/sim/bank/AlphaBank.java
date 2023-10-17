package io.sim.bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import io.sim.company.Company;

public class AlphaBank extends Thread {
    
    private ServerSocket serverAlphaBank;
    private static ArrayList<Account> accounts;
    private static ArrayList<Register> registrosPendentes;
    static int qtdClientes = 0;

    // Atributo de sincronização
    private Object sincroniza;

    public AlphaBank(ServerSocket serverSocket) throws IOException {
        this.serverAlphaBank = serverSocket;
        accounts = new ArrayList<Account>();
        registrosPendentes = new ArrayList<Register>();
        this.sincroniza = new Object();
    }

    @Override
    public void run() {
        try {
            System.out.println("AlphaBank iniciado. Aguardando conexões...");

            while (true) {
                Socket clientSocket = serverAlphaBank.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                AccountManipulator accountManipulator = new AccountManipulator(clientSocket, this);
                accountManipulator.start();
            }
            //System.out.println("Encerrando AlphaBank");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void conectar(String accountID, Socket socket) {
    //     synchronized (sincroniza) {
    //         conexoes.put(accountID, socket);
    //     }
    // }

    // public Map<String, Socket> getConexoes() {
    //     return conexoes;
    // }

    public static void adicionarAccount(Account conta) {
        synchronized (AlphaBank.class) {
            if (accounts != null) {
                accounts.add(conta);
            } else {
                System.out.println("Adicao de conta mal sucedida: AlphaBank não foi iniciado ainda");
            }
        }
    }

    public boolean fazerLogin(String accountID, String senha) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(accountID)) {
                if (account.getSenha().equals(senha)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean transferencia(String pagadorID, String recebedorID, double quantia) {
        Account pagador = getAccountPeloID(pagadorID);
        Account recebedor = getAccountPeloID(recebedorID);
        
        synchronized (sincroniza) {
            if (pagador != null && recebedor != null) {
                if (pagador.getSaldo() >= quantia) {
                    pagador.saque(quantia);
                    recebedor.deposito(quantia);
                    return true;
                } else {
                    System.out.println("AB - Problemas de transferencia: " + pagador + " nao tem saldo suficiente");
                }
            } else {
                System.out.println("AB - Problemas de transferencia: ID do recebedor");
            }
            return false;
        }
    }

    private static Account getAccountPeloID(String accountID) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(accountID)) {
                return account;
            }
        }
        return null;
    }

    public void adicionaRegistro(Register register) {
        registrosPendentes.add(register);
    }

    public static Register pegarRegistro(String accountID) {
        synchronized (AlphaBank.class) {
            for (int i = 0; i < registrosPendentes.size(); i++) {
                if (accountID.equals(registrosPendentes.get(i).getUsuario())) {
                    return registrosPendentes.remove(i);
                }
            }
            System.out.println("Não há registros para esa conta");
            return null;
        }
    }
}


