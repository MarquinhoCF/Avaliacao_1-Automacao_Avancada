package io.sim;

import io.sim.simulator.EnvSimulator;

/**
 * Classe que inica toda a aplicação
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        // Cria uma instância da classe EnvSimulator
        EnvSimulator ev = new EnvSimulator();
        
        // Inicia a execução da simulação chamando o método "start" na instância
        ev.start();
        ev.join();
        
        System.out.println("Encerando APP!");
    }
}
