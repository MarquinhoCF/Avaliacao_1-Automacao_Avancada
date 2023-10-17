package io.sim.bank;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class AccountManipulator extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream saida;
    private AlphaBank alphaBank;

    public AccountManipulator(Socket _socket, AlphaBank _alphaBank) {
        this.socket = _socket;
        this.alphaBank = _alphaBank;
    }

    @Override
    public void run() {
        boolean sair = false;
        try {
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            JSONObject accountJSON = new JSONObject((String) entrada.readUTF());
            alphaBank.conectar(accountJSON.getString("Account ID"), socket);
            alphaBank.adicionarAccount(accountJSON.getString("Account ID"), accountJSON.getString("Senha"), accountJSON.getLong("Saldo"));
            
            while (!sair) {
                JSONObject dadosOperacao = new JSONObject((String) entrada.readUTF());
                String operacao = dadosOperacao.getString("Operacao");

                System.out.println("Leu as informações de Operacao!!");
                switch (operacao) {
                    case "Transferencia":
                        System.out.println("TENTA FAZER TRANSFERENCIA");
                        String pagadorID = dadosOperacao.getString("ID do Pagador");
                        String senha = dadosOperacao.getString("Senha do Pagador");
                        if (alphaBank.fazerLogin(pagadorID, senha)) {
                            System.out.println("ACCOUNT MANIPULATOR CRIA BOTPAYMENT");
                            System.out.println(dadosOperacao.toString());
                            // Terminar a lógica!!
                        } else {
                            System.out.println("AB - Login mal sucedido, verifique o ID e a senha: " + pagadorID);
                        }
                        break;
                    case "Sair":
                        sair = true;
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // public void enviarMensagemReservada(Mensagem mensagem) throws IOException {
    //     for (Map.Entry<String, Socket> cliente : clientesMap.entrySet()) {
    //         if (mensagem.getDestinatario().equals(cliente.getKey())) {
    //             ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
    //             saida.writeObject(mensagem);
    //         }
    //     }
    // }
    

}
