package io.sim.bank;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

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

            // Construa a solicitação em formato JSON
            JSONObject jsonTransferencia = criaJSONTransferencia("Transferencia", pagadorID, pagadorSenha, recebedorID, quantia);

            // Envie a solicitação ao servidor AlphaBank
            output.writeUTF(jsonTransferencia.toString());

            // Aguarde a resposta do servidor AlphaBank
            JSONObject resposta = new JSONObject((String) input.readUTF());
            boolean sucesso = resposta.getBoolean("Resposta");

            if (sucesso) {
                System.out.println("Transferência bem-sucedida!");
            } else {
                System.out.println("Transferência falhou.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject criaJSONTransferencia(String operacao, String pagadorID, String senhaPagador, String recebedorID, double quantia) {
		JSONObject my_json = new JSONObject();
        my_json.put("Operacao", operacao);
		my_json.put("ID do Pagador", pagadorID);
		my_json.put("Senha do Pagador", senhaPagador);
        my_json.put("ID do Recebedor", recebedorID);
		my_json.put("Quantia", quantia);
		
		return my_json;
	}
}


