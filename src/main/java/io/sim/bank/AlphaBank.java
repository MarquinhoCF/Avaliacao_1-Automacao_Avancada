package io.sim.bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.sim.company.Company;

public class AlphaBank extends Thread {
    
    private ServerSocket serverAlphaBank;
    private static Map<String, Socket> conexoes;
    private static ArrayList<Account> accounts;
    static int qtdClientes = 0;

    public AlphaBank(ServerSocket serverSocket) throws IOException {
        this.serverAlphaBank = serverSocket;
        conexoes = new HashMap<>();
        accounts = new ArrayList<Account>();
    }

    @Override
    public void run() {
        try {
            System.out.println("AlphaBank iniciado. Aguardando conexões...");

            while (Company.temRotasDisponiveis()) {
                Socket clientSocket = serverAlphaBank.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                AccountManipulator accountManipulator = new AccountManipulator();
                accountManipulator.start();
            }
            System.out.println("Encerrando AlphaBank");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void conectar(String accountID, Socket socket) {
        conexoes.put(accountID, socket);
    }

    public void adicionarAccount(String accountID, String senha, long saldo) {
        Account conta = new Account(accountID, senha, saldo);
        accounts.add(conta);
    }

    public boolean hasAccount(int identifier) {
        for (Account account : accounts) {
            if (account.getIdentifier() == identifier) {
                return true;
            }
        }
        return false;
    }

    public boolean transfer(int senderID, int receiverID, double amount) {
        Account sender = getAccountByID(senderID);
        Account receiver = getAccountByID(receiverID);

        if(sender == null){
            System.out.println("SENDER");
        }

        if(receiver == null){
            System.out.println("RECIEVER");
        }

        //System.out.println("PROBELMA COM FOI SENDER OU RECIEVER");
        
        if (sender != null && receiver != null) {
            System.out.println("NAO FOI SENDER OU RECIEVER");
            if (sender.getBalance() >= amount) {
                sender.withdraw(amount);
                receiver.deposit(amount);
                return true;
            }
        }
        return false;
    }

    private Account getAccountByID(int identifier) {
        for (Account account : accounts) {
            if (account.getIdentifier() == identifier) {
                return account;
            }
        }
        return null;
    }
}


