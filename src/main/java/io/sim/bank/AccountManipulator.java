package io.sim.bank;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import io.sim.AESencrypt;
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
            int numBytesMsg;
            byte[] mensagemEncriptada;
            
            while (!sair) {
                numBytesMsg = JSONConverter.extraiTamanhoBytes(AESencrypt.decripta(entrada.readNBytes(AESencrypt.getTamNumBytes())));
                String[] login = JSONConverter.extraiLogin(AESencrypt.decripta(entrada.readNBytes(numBytesMsg)));

                if (alphaBank.fazerLogin(login)) {
                    System.out.println(login[0] + " FEZ O LOGIN");
                    numBytesMsg = JSONConverter.extraiTamanhoBytes(AESencrypt.decripta(entrada.readNBytes(AESencrypt.getTamNumBytes())));
                    TransferData tf = JSONConverter.extraiTransferData(AESencrypt.decripta(entrada.readNBytes(numBytesMsg)));
                    System.out.println("Leu as informações de Operacao!!");
                    String operacao = tf.getOperacao();
                    switch (operacao) {
                        case "Pagamento":
                            String recebedorID = tf.getRecebedor();
                            double quantia = tf.getQuantia();
                        System.out.println(recebedorID + " VAI RECEBER R$" + quantia);
                            if (alphaBank.transferencia(login[0], recebedorID, quantia)) {
                                mensagemEncriptada = AESencrypt.encripta(JSONConverter.criaRespostaTransferencia(true));
                                saida.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
                                saida.write(mensagemEncriptada);
                                alphaBank.adicionaRegistros(tf);
                            } else {
                                mensagemEncriptada = AESencrypt.encripta(JSONConverter.criaRespostaTransferencia(false));
                                saida.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
                                saida.write(mensagemEncriptada);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
