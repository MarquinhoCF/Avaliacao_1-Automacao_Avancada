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
    private static Map<String, Socket> accounts;
    static int qtdClientes = 0;

    public AlphaBank(ServerSocket serverSocket) throws IOException {
        this.serverAlphaBank = serverSocket;
        accounts = new HashMap<>();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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


