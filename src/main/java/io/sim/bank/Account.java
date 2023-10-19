package io.sim.bank;

import java.util.ArrayList;
import java.util.Random;

public class Account extends Thread {
    private String accountID;
    private String senha;
    private double saldo;
    private ArrayList<TransferData> historico;

    // Atributos de sincronização
    private Object sincroniza;

    public Account(String _accountID, String _senha, double _saldo) {
        this.accountID = _accountID;
        this.senha = _senha;
        this.saldo = _saldo;
        this.historico = new ArrayList<TransferData>();
        this.sincroniza = new Object();
    }

    @Override
    public void run() {
        System.out.println("Account: " + accountID + " iniciando...");
        // Sempre que tiver uma operação withdraw ou deposit criar uma transaction e guardar
        try {
            while (true) {
                if (AlphaBank.numeroDeRegistrosPend() != 0) {
                    Thread.sleep(500);
                    TransferData register = AlphaBank.pegarRegistro(accountID);
                    if (register != null) {
                        historico.add(register);
                        System.out.println(register.getDescricao());
                    }
                }
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

    public String getAccountID() {
        return accountID;
    }

    public String getSenha() {
        return senha;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setBalance(double _saldo) {
        this.saldo = _saldo;
    }

    public void deposito(double quantia) {
        synchronized (sincroniza) {
            if (quantia > 0) {
                saldo += quantia;
            } else {
                System.out.println("O valor do depósito deve ser positivo.");
            }
        }
    }

    public void saque(double quantia) {
        synchronized (sincroniza) {
            if (quantia > 0) {
                if (saldo >= quantia) {
                    saldo -= quantia;
                } else {
                    System.out.println("Saldo insuficiente para efetuar o saque.");
                }
            } else {
                System.out.println("O valor do saque deve ser positivo.");
            }
        }
    }

    public static Account criaAccount(String accountID, long saldo) {
        //Gerando uma senha de 6 digitos aleatória
        String numerosPermitidos = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(numerosPermitidos.length());
            char randomNumber = numerosPermitidos.charAt(index);
            sb.append(randomNumber);
        }
        String senha = sb.toString();

        Account account = new Account(accountID, senha, saldo);
        return account;
    }
}


