package io.sim.bank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import io.sim.comunication.AESencrypt;
import io.sim.comunication.JSONConverter;

public class EndAccount extends Thread {
    private Socket socket;
    private Account account;

    public EndAccount(Socket _socket, Account _account) {
        this.socket = _socket;
        this.account = _account;
    }

    @Override
    public void run() {
        try {
            // Crie streams de entrada e saída para comunicar com o servidor AlphaBank
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            byte[] mensagemEncriptada;

            String[] login = { account.getAccountID(), account.getSenha() };

            mensagemEncriptada = AESencrypt.encripta(JSONConverter.criarJSONLogin(login));
			output.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
			output.write(mensagemEncriptada);

            TransferData td = new TransferData(account.getAccountID(), "Sair", "", 0);

            mensagemEncriptada = AESencrypt.encripta(JSONConverter.criaJSONTransferData(td));
			output.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
			output.write(mensagemEncriptada);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
