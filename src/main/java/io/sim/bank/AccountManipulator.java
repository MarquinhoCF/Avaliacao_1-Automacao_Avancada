package io.sim.bank;

import java.io.IOException;
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
                Action action = mensagem.getAction();

                switch (action) {
                    case CONNECT:
                        conectar(mensagem);
                        enviarMensagemTodos(mensagem);
                        enviarUsuariosOnline();
                        break;
                    case DISCONNECT:
                        desconectar(mensagem);
                        enviarMensagemTodos(mensagem);
                        enviarUsuariosOnline();
                        sair = true;
                        break;
                    case SEND:
                        enviarMensagemTodos(mensagem);
                        break;
                    case SEND_ONE:
                        enviarMensagemReservada(mensagem);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ThreadServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void conectar(Mensagem mensagem) {
        clientesMap.put(mensagem.getRemetente(), socket);
    }

    public void desconectar(Mensagem mensagem) throws IOException {
        clientesMap.remove(mensagem.getRemetente());
    }

    public void enviarMensagemTodos(Mensagem mensagem) throws IOException {
        for (Map.Entry<String, Socket> cliente : clientesMap.entrySet()) {
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
            saida.writeObject(mensagem);
        }
    }

    public void enviarMensagemReservada(Mensagem mensagem) throws IOException {
        for (Map.Entry<String, Socket> cliente : clientesMap.entrySet()) {
            if (mensagem.getDestinatario().equals(cliente.getKey())) {
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
                saida.writeObject(mensagem);
            }
        }
    }
    
    public void enviarUsuariosOnline() throws IOException {
        ArrayList<String> usuariosOnline = new ArrayList();
        
        for (Map.Entry<String, Socket> cliente : clientesMap.entrySet()) {
            usuariosOnline.add(cliente.getKey());
        }
        
        Mensagem mensagem = new Mensagem();
        mensagem.setAction(Action.USERS_ONLINE);
        mensagem.setUsuariosOnline(usuariosOnline);
        
        for (Map.Entry<String, Socket> cliente : clientesMap.entrySet()) {
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getValue().getOutputStream());
            saida.writeObject(mensagem);
        }
        
    }

}
