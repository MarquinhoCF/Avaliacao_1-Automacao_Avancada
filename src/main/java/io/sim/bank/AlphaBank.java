package io.sim.bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AlphaBank extends Thread {
    
    private ServerSocket serverAlphaBank;
    private static ArrayList<Account> accounts;
    private static ArrayList<TransferData> registrosPendentes;
    static int qtdClientes = 0;

    // Atributo de sincronização
    private Object sincroniza;

    public AlphaBank(ServerSocket serverSocket) throws IOException {
        this.serverAlphaBank = serverSocket;
        accounts = new ArrayList<Account>();
        registrosPendentes = new ArrayList<TransferData>();
        this.sincroniza = new Object();
    }

    @Override
    public void run() {
        try {
            System.out.println("AlphaBank iniciado. Aguardando conexões...");
            boolean primeiraVez = true;
            while (true) {
                Socket clientSocket = serverAlphaBank.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                AccountManipulator accountManipulator = new AccountManipulator(clientSocket, this);
                accountManipulator.start();

                if (primeiraVez) {
                    AlphaBankAttExcel attExcel = new AlphaBankAttExcel(this);
                    attExcel.start();
                    primeiraVez = false;
                }
            }
            //System.out.println("Encerrando AlphaBank");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void adicionarAccount(Account conta) {
        synchronized (AlphaBank.class) {
            if (accounts != null) {
                accounts.add(conta);
            } else {
                System.out.println("Adicao de conta mal sucedida: AlphaBank não foi iniciado ainda");
            }
        }
    }

    public boolean fazerLogin(String[] login) {
        String accountID = login[0];
        String senha = login[1];

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

    private Account getAccountPeloID(String accountID) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(accountID)) {
                return account;
            }
        }
        return null;
    }

    public void adicionaRegistros(TransferData registerPag) {
        registerPag.setTimestamp();
        registerPag.setAccountID(registerPag.getPagador());
        registrosPendentes.add(registerPag);
        TransferData registerReceb = new TransferData(registerPag.getPagador(), "Recebimento", registerPag.getRecebedor(), registerPag.getQuantia());
        registerReceb.setTimestamp();
        registerReceb.setAccountID(registerPag.getRecebedor());
        registrosPendentes.add(registerReceb);
    }

    public boolean temRegistro() {
        return !registrosPendentes.isEmpty();
    }

    public TransferData pegaRegistro() {
        return registrosPendentes.remove(0);
    }

    public void mandaRegistroAcc(TransferData data) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(data.getAccountID())) {
                account.addHistorico(data);
            }
        }
    }
}


