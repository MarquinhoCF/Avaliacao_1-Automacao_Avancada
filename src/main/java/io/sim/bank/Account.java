package io.sim.bank;

import java.util.ArrayList;

public class Account extends Thread {
    private String accountID;
    private String senha;
    private double saldo;
    private ArrayList<Register> historico;

    //private boolean houveTransacao;

    public Account(String _accountID, String _senha, double _saldo) {
        this.accountID = _accountID;
        this.senha = _senha;
        this.saldo = _saldo;
        this.historico = new ArrayList<Register>();
    }

    @Override
    public void run() {
        // Sempre que tiver uma operação withdraw ou deposit criar uma transaction e guardar
    }

    public String getAccountID() {
        return accountID;
    }

    public String getSenha() {
        return senha;
    }

    public double getBalance() {
        return saldo;
    }

    public void setBalance(double _saldo) {
        this.saldo = _saldo;
    }

    public void deposito(double quantia) {
        if (quantia > 0) {
            saldo += quantia;
        } else {
            System.out.println("O valor do depósito deve ser positivo.");
        }
    }

    public void saque(double quantia) {
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


