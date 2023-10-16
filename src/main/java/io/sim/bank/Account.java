package io.sim.bank;

import java.util.ArrayList;
import java.util.Random;

public class Account extends Thread {
    private String accountID;
    private String senha;
    private double saldo;
    private ArrayList<Register> historico;

    public Account(String _accountID, String _senha, double _saldo) {
        this.accountID = _accountID;
        this.senha = setSenha(6);
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

    private String setSenha(int tamanho) {
        if (tamanho > 0) {
            String caracteresPermitidos = "0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(tamanho);
            
            // Gerando números aleatórios e anexe-os à string
            for (int i = 0; i < tamanho; i++) {
                int index = random.nextInt(caracteresPermitidos.length());
                char randomNumber = caracteresPermitidos.charAt(index);
                sb.append(randomNumber);
            }
            return sb.toString();
        } else {
            return null;
        }
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

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            System.out.println("O valor do depósito deve ser positivo.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0) {
            if (balance >= amount) {
                balance -= amount;
            } else {
                System.out.println("Saldo insuficiente para efetuar o saque.");
            }
        } else {
            System.out.println("O valor do saque deve ser positivo.");
        }
    }
}


