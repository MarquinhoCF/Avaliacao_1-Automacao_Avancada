package io.sim.simulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import io.sim.driver.Driver;
import io.sim.fuelStation.FuelStation;
import io.sim.report.ExcelReport;
import io.sim.bank.AlphaBank;
import io.sim.company.Company;
import it.polito.appeal.traci.SumoTraciConnection;

/**
 * A classe EnvSimulator é responsável por configurar e iniciar a simulação
 */
public class EnvSimulator extends Thread {
    private SumoTraciConnection sumo;
	private static String host;
	private static int portaSUMO;
	private static int portaCompany;
	private static int portaAlphaBank;
	private static long taxaAquisicao;
	private static int numDrivers;
	private static String rotasXML;

    public EnvSimulator() {
        // Configuração inicial do ambiente e conexão com o SUMO
        String sumo_bin = "sumo-gui";        
        String config_file = "map/map.sumo.cfg";
        
        // Inicializa a conexão com o SUMO
        this.sumo = new SumoTraciConnection(sumo_bin, config_file);
        
		// Configurações gerais para simulação
        host = "localhost";
        portaSUMO = 12345;
        portaCompany = 23415;
        portaAlphaBank = 54321;
        taxaAquisicao = 300;
        numDrivers = 100;
        rotasXML = "data/dados.xml";
    }

    public void run() {
        // Configuração inicial do SUMO para automação
        sumo.addOption("start", "1"); // Inicia a simulação automaticamente na GUI
        sumo.addOption("quit-on-end", "1"); // Fecha o SUMO automaticamente ao final

        try {
            sumo.runServer(portaSUMO); // Inicia o servidor SUMO na porta especificada
            System.out.println("SUMO conectado.");
            Thread.sleep(5000);

            // Inicia a execução do simulador
            ExecutaSimulador execSimulador = new ExecutaSimulador(this.sumo, taxaAquisicao);
            execSimulador.start();

            // Inicia um servidor AlphaBank na porta especificada
            ServerSocket alphaBankServer = new ServerSocket(portaAlphaBank);
            AlphaBank alphaBank = new AlphaBank(alphaBankServer);
            alphaBank.start();
            Thread.sleep(2000);

            // Inicia uma estação de combustível (FuelStation)
            FuelStation fuelStation = new FuelStation(portaAlphaBank, host);
            fuelStation.start();

            // Inicia um servidor Company na porta especificada
            ServerSocket companyServer = new ServerSocket(portaCompany);
            Company company = new Company(companyServer, rotasXML, numDrivers, portaAlphaBank, host);
            company.start();

            // Cria e inicia uma lista de drivers
            ArrayList<Driver> drivers = DriverANDCarCreator.criaListaDrivers(numDrivers, fuelStation, taxaAquisicao, sumo, host, portaCompany, portaAlphaBank);

            // Cria planilhas de relatórios
            ExcelReport.criaPlanilhas(company, drivers, fuelStation);

            // Inicia e aguarda a conclusão da execução de todos os drivers
            for(int i = 0; i < drivers.size(); i++) {
                drivers.get(i).start();
                Thread.sleep(500);
            }
            for(int i = 0; i < drivers.size(); i++) {
                drivers.get(i).join();
            }

            // Encerra o servidor Company
            companyServer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Encerrando EnvSimulator");
    }
}