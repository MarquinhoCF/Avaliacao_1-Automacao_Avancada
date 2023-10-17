package io.sim.bank;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

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
            
            while (!sair) {
                JSONObject dadosOperacao = new JSONObject((String) entrada.readUTF());
                String operacao = dadosOperacao.getString("Operacao");

                System.out.println("Leu as informações de Operacao!!");
                switch (operacao) {
                    case "Transferencia":
                        String pagadorID = dadosOperacao.getString("ID do Pagador");
                        String senha = dadosOperacao.getString("Senha do Pagador");
                        if (alphaBank.fazerLogin(pagadorID, senha)) {
                            String recebedorID = dadosOperacao.getString("ID do Recebedor");
                            double quantia = dadosOperacao.getDouble("Quantia");
                            dadosOperacao.getDouble("Quantia");
                            if (alphaBank.transferencia(pagadorID, recebedorID, quantia)) {
                                saida.writeUTF(criaRespostaServidor(true).toString());
                                criarRegistro(true, pagadorID, recebedorID, quantia);
                                criarRegistro(false, pagadorID, recebedorID, quantia);
                            } else {
                                saida.writeUTF(criaRespostaServidor(false).toString());
                            }
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

    private JSONObject criaRespostaServidor(boolean sucesso) {
        JSONObject my_json = new JSONObject();
        my_json.put("Resposta", sucesso);
        return my_json;
    }

    public void criarRegistro(boolean operacao, String pagadorID, String recebedorID, double quantia) throws IOException {
        Register notificacao = null;

        // operacao = true -> Pagamento
        if (operacao) {
            notificacao = new Register(pagadorID, "Pagamento", recebedorID, quantia);
        } else {
            notificacao = new Register(recebedorID, "Recebimento", pagadorID, quantia);
        }

        if (notificacao != null) {
            alphaBank.adicionaRegistro(notificacao);
        } else {
            System.out.println("SAB - Erro na criacao do registro!!");
        }
        
    }
    

}
