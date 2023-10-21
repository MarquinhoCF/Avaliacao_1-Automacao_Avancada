package io.sim.bank;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import io.sim.comunication.AESencrypt;
import io.sim.comunication.JSONConverter;

public class BotPayment extends Thread {
    private Socket socket;
    private String pagadorID;
    private String pagadorSenha;
    private String recebedorID;
    private double quantia;

    public BotPayment(Socket _socket, String _pagadorID, String _pagadorSenha, String _recebedorID, double _quantia) {
        this.socket = _socket;
        this.pagadorID = _pagadorID;
        this.pagadorSenha = _pagadorSenha;
        this.recebedorID = _recebedorID;
        this.quantia = _quantia;
    }

    @Override
    public void run() {
        try {
            // Crie streams de entrada e saída para comunicar com o servidor AlphaBank
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            int numBytesMsg;
            byte[] mensagemEncriptada;

            String[] login = { pagadorID, pagadorSenha };

            mensagemEncriptada = AESencrypt.encripta(JSONConverter.criarJSONLogin(login));
			output.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
			output.write(mensagemEncriptada);

            TransferData td = new TransferData(pagadorID, "Pagamento", recebedorID, quantia);

            mensagemEncriptada = AESencrypt.encripta(JSONConverter.criaJSONTransferData(td));
			output.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
			output.write(mensagemEncriptada);

            // Aguarde a resposta do servidor AlphaBank
            numBytesMsg = JSONConverter.extraiTamanhoBytes(AESencrypt.decripta(input.readNBytes(AESencrypt.getTamNumBytes())));
            boolean sucesso = JSONConverter.extraiResposta(AESencrypt.decripta(input.readNBytes(numBytesMsg)));

            if (sucesso) {
                System.out.println("Transferência bem-sucedida!");
            } else {
                System.out.println("Transferência falhou.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


