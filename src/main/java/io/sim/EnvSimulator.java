package io.sim;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import io.sim.driver.Driver;
import io.sim.bank.AlphaBank;
import io.sim.company.Company;
import it.polito.appeal.traci.SumoTraciConnection;

/**Classe que faz a conexao com o SUMO e cria os objetos da simulacao. 
 * Acaba funcionando como uma classe principal
 */
public class EnvSimulator extends Thread {
    private SumoTraciConnection sumo;
	private static String host;
	private static int portaSUMO; // NEWF
	private static int portaCompany;
	private static int portaAlphaBank;
	private static long taxaAquisicao;
	private static int numDrivers;

    public EnvSimulator() {
		/* SUMO */
		String sumo_bin = "sumo-gui";		
		String config_file = "map/map.sumo.cfg";
		
		// Sumo connection
		this.sumo = new SumoTraciConnection(sumo_bin, config_file);
		
		host = "localhost";
		portaSUMO = 12345;
		portaCompany = 23415;
		portaAlphaBank = 54321;
		taxaAquisicao = 500;
		numDrivers = 1;
	}

    public void run() {
		// Start e configurações inicias do SUMO
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end

		try {
			sumo.runServer(portaSUMO); // porta servidor SUMO
			System.out.println("SUMO conectado.");
			Thread.sleep(5000);

			ServerSocket alphaBankServer = new ServerSocket(portaAlphaBank);
			AlphaBank alphaBank = new AlphaBank(alphaBankServer);
			alphaBank.start();

			Thread.sleep(1000);

			ServerSocket companyServer = new ServerSocket(portaCompany);
			Company company = new Company(companyServer, "data/dados.xml", numDrivers,  portaAlphaBank, host);
			company.start();

			// Roda o metodo join em todos os Drivers, espera todos os drivers terminarem a execução
			ArrayList<Driver> drivers = DriverANDCarCreator.criaListaDrivers(numDrivers, taxaAquisicao, sumo, host, portaCompany, portaAlphaBank);
			for(int i = 0; i < drivers.size(); i++) {
				drivers.get(i).join();
			}

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
