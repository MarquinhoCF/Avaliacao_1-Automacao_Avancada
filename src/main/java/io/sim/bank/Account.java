package io.sim.bank;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

public class Account extends Thread {
    private String accountID;
    private String senha;
    private double saldo;
    private ArrayList<Register> historico;

    // Atributos de sincronização
    private Object sincroniza;
    private boolean houveTransacao;


    public Account(String _accountID, String _senha, double _saldo) {
        this.accountID = _accountID;
        this.senha = _senha;
        this.saldo = _saldo;
        this.historico = new ArrayList<Register>();
        this.sincroniza = new Object();
        this.houveTransacao = true;
    }

    @Override
    public void run() {
        // Sempre que tiver uma operação withdraw ou deposit criar uma transaction e guardar
        while (true) {
            if (houveTransacao) {
                try {
                    Thread.sleep(500);
                    Register register = AlphaBank.pegarRegistro(accountID);
                    historico.add(register);
                    houveTransacao = false;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
        houveTransacao = true;
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
        houveTransacao = true;
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


