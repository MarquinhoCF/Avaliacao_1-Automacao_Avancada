package io.sim.simulator;

import it.polito.appeal.traci.SumoTraciConnection;

// A classe ExecutaSimulador é responsável por executar a simulação no SUMO
public class ExecutaSimulador extends Thread {
    private SumoTraciConnection sumo;
    private long taxaAquisicao;

    public ExecutaSimulador(SumoTraciConnection _sumo, long _taxaAquisicao) {
        this.sumo = _sumo;
        this.taxaAquisicao = _taxaAquisicao;
    }

    @Override
    public void run() {
         // Loop infinito para continuar a execução do simulador
        while(true) {
            try {
                this.sumo.do_timestep();
                // Executa um passo de simulação no SUMO
                sleep(taxaAquisicao);
                // Aguarda um determinado tempo (taxa de aquisição) antes de continuar
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
