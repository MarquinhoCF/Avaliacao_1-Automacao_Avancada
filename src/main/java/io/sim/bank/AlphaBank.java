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
    private static Map<String, Socket> conexoes;
    private static ArrayList<Account> accounts;
    static int qtdClientes = 0;

    // Atributo de sincronização
    private Object sincroniza;

    public AlphaBank(ServerSocket serverSocket) throws IOException {
        this.serverAlphaBank = serverSocket;
        conexoes = new HashMap<>();
        accounts = new ArrayList<Account>();
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

    public void conectar(String accountID, Socket socket) {
        synchronized (sincroniza) {
            conexoes.put(accountID, socket);
        }
    }

    public void adicionarAccount(String accountID, String senha, long saldo) {
        synchronized (sincroniza) {
            Account conta = new Account(accountID, senha, saldo);
            accounts.add(conta);
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
                }
            }
            return false;
        }
    }

    private Account getAccountPeloID(String accountID) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(accountID)) {
                return account;
            }
        }
        return null;
    }

    public static JSONObject criaJSONTransferencia(String operacao, String pagadorID, String senhaPagador, String recebedorID, double quantia) {
		JSONObject my_json = new JSONObject();
        my_json.put("Operacao", operacao);
		my_json.put("ID do Pagador", pagadorID);
		my_json.put("Senha do Pagador", senhaPagador);
        my_json.put("ID do Recebedor", recebedorID);
		my_json.put("Quantia", quantia);
		
		return my_json;
	}
}


