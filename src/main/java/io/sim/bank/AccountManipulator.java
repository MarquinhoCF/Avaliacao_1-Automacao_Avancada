package io.sim.bank;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import io.sim.JSONConverter;

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
                String[] login = JSONConverter.extraiLogin(entrada.readUTF());

                if (alphaBank.fazerLogin(login)) {
                    TransferData tf = JSONConverter.extraiTransferData(entrada.readUTF());
                    System.out.println("Leu as informações de Operacao!!");
                    String operacao = tf.getOperacao();
                    switch (operacao) {
                        case "Pagamento":
                            String recebedorID = tf.getRecebedor();
                            double quantia = tf.getQuantia();
                            if (alphaBank.transferencia(login[0], recebedorID, quantia)) {
                                saida.writeUTF(JSONConverter.criaRespostaTransferencia(true));
                                alphaBank.adicionaRegistros(tf);
                            } else {
                                saida.writeUTF(JSONConverter.criaRespostaTransferencia(false));
                            }
                            
                            break;
                        case "Sair":
                            sair = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("AB - Login mal sucedido, verifique o ID e a senha: " + login[0]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
