package io.sim.driver;

import de.tudresden.sumo.cmd.Vehicle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;

import it.polito.appeal.traci.SumoTraciConnection;
import javafx.animation.RotateTransition;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;
import io.sim.company.Rota;

/**Define os atributos que coracterizam um Carro.
 * Por meio de metodos get da classe Vehicle, 
 */
public class Car extends Vehicle implements Runnable {
	// atributos de cliente
    private Socket socket;
    private int companyServerPort;
    private String companyServerHost; 
	private DataInputStream entrada;
	private DataOutputStream saida;
	
	// atributos da classe
	private String idCar; // id do carro
	private SumoColor colorCar;
	private String driverID; // id do motorista
	private SumoTraciConnection sumo;
	private boolean on_off;
	private boolean terminado; // chamado pelo Driver
	private long acquisitionRate; // taxa de aquisicao de dados dos sensores
	private int fuelType; 			// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private int fuelPreferential; 	// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double fuelPrice; 		// price in liters
	private int personCapacity;		// the total number of persons that can ride in this vehicle
	private int personNumber;		// the total number of persons which are riding in this vehicle
	private double speed; //NEWF
	private Rota rota;
	private String carStatus;
	private int distanciaPercorrida; // Em m
	private ArrayList<DrivingData> drivingRepport; // dados de conducao do veiculo
	private TransportService ts;

	public Car(boolean _on_off, String _idCar, SumoColor _colorCar, String _driverID, SumoTraciConnection _sumo, long _acquisitionRate,
			int _fuelType, int _fuelPreferential, double _fuelPrice, int _personCapacity, int _personNumber, String _companyServerHost, 
			int _companyServerPort) throws Exception {

		this.companyServerPort = _companyServerPort;
		this.companyServerHost = _companyServerHost;
		this.on_off = _on_off;
		this.idCar = _idCar;
		this.colorCar = _colorCar;
		this.driverID = _driverID;
		this.sumo = _sumo;
		this.acquisitionRate = _acquisitionRate;
		
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
		
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}

		this.terminado = false;
		this.fuelPrice = _fuelPrice;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;
		this.speed = 50;
		this.rota = null;
		this.carStatus = "aguardando";
		this.drivingRepport = new ArrayList<DrivingData>();
	}

	@Override
	public void run() {
		System.out.println(this.idCar + " iniciando");
		try {
            socket = new Socket(this.companyServerHost, this.companyServerPort);
            entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());

			System.out.println(this.idCar + " conectado!!");

			while (!terminado) {
				// Recebndo Rota
				// Manda "aguardando" da primeira vez
				saida.writeUTF(criaJSONComunicacao(carStatus).toString());
				System.out.println(this.idCar + " aguardando rota");
				rota = (Rota) transfString2Rota(entrada.readUTF());
				System.out.println(this.idCar + " leu " + rota.getID());

				// Zerando o atributo Distance
				this.distanciaPercorrida = 0;
				double latRef = 0;
				double lonRef = 0;

				ts = new TransportService(true, this.idCar, rota, this, this.sumo);
				ts.start();
				System.out.println("CAR - TransportService ativo");
				String edgeFinal = this.getEdgeFinal(); 
				this.on_off = true;
				Thread.sleep(this.acquisitionRate);

				boolean initRoute = true;
				while (this.on_off) {
					String edgeAtual = (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar));
					Thread.sleep(this.acquisitionRate);
					if (initRoute) {
						double[] coordGeo = calculaCoordGeograficas();
						latRef = coordGeo[0];
						lonRef = coordGeo[1];
						initRoute = false;
					}

					if(verificaRotaTerminada(edgeAtual, edgeFinal)) {
						System.out.println(this.idCar + " acabou a rota.");
						this.ts.setOn_off(false);
						this.carStatus = "finalizado";
						saida.writeUTF(criaJSONComunicacao(carStatus).toString());
						this.on_off = false;
					} else {
						System.out.println(this.idCar + " -> edge atual: " + edgeAtual);
						atualizaDistanciaPercorrida(latRef, lonRef);
						atualizaSensores();
						this.carStatus = "rodando";
						saida.writeUTF(criaJSONComunicacao(carStatus).toString());
					}
				}
				System.out.println(this.idCar + " off.");

				if(!terminado) {
					this.carStatus = "aguardando";
				}

				if(terminado) {
					this.carStatus = "encerrado";
				}
			}
			System.out.println("Encerrando: " + idCar);
			entrada.close();
			saida.close();
			socket.close();
			this.ts.setTerminado(true);
        } catch (Exception e) {
            // TODO Car-generated catch block
            e.printStackTrace();
        }

		System.out.println(this.idCar + " encerrado.");
	}

	private void atualizaSensores() {
		try {
			if (!this.getSumo().isClosed()) {
				SumoPosition2D sumoPosition2D;
				sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idCar));

				// System.out.println("CarID: " + this.getIdCar());
				// System.out.println("RoadID: " + (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar)));
				// System.out.println("RouteID: " + (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)));
				// System.out.println("RouteIndex: " + this.sumo.do_job_get(Vehicle.getRouteIndex(this.idCar)));
				
				// Criacao dos dados de conducao do veiculo
				DrivingData repport = new DrivingData(
						this.idCar, this.driverID, System.currentTimeMillis(), sumoPosition2D.x, sumoPosition2D.y,
						(String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar)),
						(String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)),
						(double) this.sumo.do_job_get(Vehicle.getSpeed(this.idCar)),
						getDistanciaPercorrida(),

						(double) this.sumo.do_job_get(Vehicle.getFuelConsumption(this.idCar)),
						// Vehicle's fuel consumption in ml/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30
						
						1/*averageFuelConsumption (calcular)*/,

						this.fuelType, this.fuelPrice,

						(double) this.sumo.do_job_get(Vehicle.getCO2Emission(this.idCar)),
						// Vehicle's CO2 emissions in mg/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30

						(double) this.sumo.do_job_get(Vehicle.getHCEmission(this.idCar)),
						// Vehicle's HC emissions in mg/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30
						
						this.personCapacity,
						// the total number of persons that can ride in this vehicle
						
						this.personNumber
						// the total number of persons which are riding in this vehicle

				);

				// Criar relat�rio auditoria / alertas
				// velocidadePermitida = (double)
				// sumo.do_job_get(Vehicle.getAllowedSpeed(this.idSumoVehicle));

				this.drivingRepport.add(repport);

				//System.out.println("Data: " + this.drivingRepport.size());
				// System.out.println("idCar = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getCarID());
				//System.out.println(
				//		"timestamp = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getTimeStamp());
				//System.out.println("X=" + this.drivingRepport.get(this.drivingRepport.size() - 1).getX_Position() + ", "
				//		+ "Y=" + this.drivingRepport.get(this.drivingRepport.size() - 1).getY_Position());
				// System.out.println("speed = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getSpeed());
				// System.out.println("odometer = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getOdometer());
				// System.out.println("Fuel Consumption = "
				// 		+ this.drivingRepport.get(this.drivingRepport.size() - 1).getFuelConsumption());
				//System.out.println("Fuel Type = " + this.fuelType);
				//System.out.println("Fuel Price = " + this.fuelPrice);

				// System.out.println(
				// 		"CO2 Emission = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getCo2Emission());

				//System.out.println();
				//System.out.println("************************");
				//System.out.println("testes: ");
				//System.out.println("getAngle = " + (double) sumo.do_job_get(Vehicle.getAngle(this.idCar)));
				//System.out
				//		.println("getAllowedSpeed = " + (double) sumo.do_job_get(Vehicle.getAllowedSpeed(this.idCar)));
				//System.out.println("getSpeed = " + (double) sumo.do_job_get(Vehicle.getSpeed(this.idCar)));
				//System.out.println(
				//		"getSpeedDeviation = " + (double) sumo.do_job_get(Vehicle.getSpeedDeviation(this.idCar)));
				//System.out.println("getMaxSpeedLat = " + (double) sumo.do_job_get(Vehicle.getMaxSpeedLat(this.idCar)));
				//System.out.println("getSlope = " + (double) sumo.do_job_get(Vehicle.getSlope(this.idCar))
				//		+ " the slope at the current position of the vehicle in degrees");
				//System.out.println(
				//		"getSpeedWithoutTraCI = " + (double) sumo.do_job_get(Vehicle.getSpeedWithoutTraCI(this.idCar))
				//				+ " Returns the speed that the vehicle would drive if no speed-influencing\r\n"
				//				+ "command such as setSpeed or slowDown was given.");

				//sumo.do_job_set(Vehicle.setSpeed(this.idCar, (1000 / 3.6)));
				//double auxspeed = (double) sumo.do_job_get(Vehicle.getSpeed(this.idCar));
				//System.out.println("new speed = " + (auxspeed * 3.6));
				//System.out.println(
				//		"getSpeedDeviation = " + (double) sumo.do_job_get(Vehicle.getSpeedDeviation(this.idCar)));
				
				
				this.sumo.do_job_set(Vehicle.setSpeedMode(this.idCar, 0));
				this.setSpeed(speed); // NEWF

				
				// System.out.println("getPersonNumber = " + sumo.do_job_get(Vehicle.getPersonNumber(this.idCar)));
				//System.out.println("getPersonIDList = " + sumo.do_job_get(Vehicle.getPersonIDList(this.idCar)));
				
				// System.out.println("************************");

			} else {
				this.on_off = false;
				System.out.println("SUMO is closed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject criaJSONComunicacao(String carStatus) {
		JSONObject my_json = new JSONObject();
		my_json.put("Driver ID", driverID);
		my_json.put("Status do Carro", carStatus);
		if (rota != null) {
			my_json.put("ID da Rota", rota.getID());
		} else {
			my_json.put("ID da Rota", "");
		}
		my_json.put("Distancia Percorrida", getDistanciaPercorrida());
		
		return my_json;
	}

	public String getCarStatus() {
		return carStatus;
	}

	public Rota getRota() {
		return rota;
	}

	public boolean isOn_off() {
		return this.on_off;
	}

	public void setOn_off(boolean _on_off) {
		this.on_off = _on_off;
	}

	public void setTerminado(boolean _terminado) {
		this.terminado = _terminado;
	}

	public long getAcquisitionRate() {
		return this.acquisitionRate;
	}

	public void setAcquisitionRate(long _acquisitionRate) {
		this.acquisitionRate = _acquisitionRate;
	}

	public String getIdCar() {
		return this.idCar;
	}

	public void incrementaDistanciaPercorrida() {
		distanciaPercorrida++;
	}

	public int getDistanciaPercorrida() {
		return distanciaPercorrida;
	}

	public SumoTraciConnection getSumo() {
		return this.sumo;
	}

	public int getFuelType() {
		return this.fuelType;
	}

	public void setFuelType(int _fuelType) {
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
	}

	public double getFuelPrice() {
		return this.fuelPrice;
	}

	public void setFuelPrice(double _fuelPrice) {
		this.fuelPrice = _fuelPrice;
	}

	public SumoColor getColorCar() {
		return this.colorCar;
	}

	public int getFuelPreferential() {
		return this.fuelPreferential;
	}

	public void setFuelPreferential(int _fuelPreferential) {
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}
	}

	public int getPersonCapacity() {
		return this.personCapacity;
	}

	public int getPersonNumber() {
		return this.personNumber;
	}

	public void setSpeed(double speed) throws Exception {
		this.sumo.do_job_set(Vehicle.setSpeed(this.idCar, speed));
	}

	// Pega a última posição da Rota
	// Método auxiliar para verificar se a rota terminou
	private String getEdgeFinal() {
		SumoStringList edge = new SumoStringList();
		edge.clear();
		String aux = this.rota.getEdges();
		for(String e : aux.split(" ")) {
			edge.add(e);
		}
		return edge.get(edge.size() - 1);
	}

	// Verifica se a rota atual terminou
	private boolean verificaRotaTerminada(String _edgeAtual, String _edgeFinal) throws Exception {
		// Cria lista de IDs dos carros do SUMO
		SumoStringList lista = (SumoStringList) this.sumo.do_job_get(Vehicle.getIDList());

		// Verificação dupla para determinar o término da Rota
		if (!lista.contains(idCar) && (_edgeFinal.equals(_edgeAtual))) {
			return true;
		} else {
			return false;
		}
	}

	// Transforma uma String em uma Rota
	private Rota transfString2Rota(String _string) {
		String[] aux = _string.split(",");
		Rota route = new Rota(aux[0], aux[1]);
		return route;
	}

	private double[] calculaCoordGeograficas() throws Exception {
		SumoPosition2D sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idCar));

		double x = sumoPosition2D.x; // coordenada X em metros
		double y = sumoPosition2D.y; // coordenada Y em metros

		double raioTerra = 6371000; // raio médio da Terra em metros

		double latRef = 0;
		double lonRef = 0;

		// Conversão de metros para graus
		double lat = latRef + (y / raioTerra) * (180 / Math.PI);
		double lon = lonRef + (x / raioTerra) * (180 / Math.PI) / Math.cos(latRef * Math.PI / 180);

		double[] coordGeo = new double[] { lat, lon };
		return coordGeo;
	}

	private double calculaDistancia(double lat1, double lon1, double lat2, double lon2) {
		double raioTerra = 6371000;
	
		// Diferenças das latitudes e longitudes
		double diferancaLat = Math.toRadians(lat2 - lat1);
		double diferancaLon = Math.toRadians(lon2 - lon1);
	
		// Fórmula de Haversine
		double a = Math.sin(diferancaLat / 2) * Math.sin(diferancaLat / 2) +
				   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				   Math.sin(diferancaLon / 2) * Math.sin(diferancaLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distancia = raioTerra * c;
	
		return distancia;
	}

	public void atualizaDistanciaPercorrida(double latInicial, double lonInicial) throws Exception {
		double[] coordGeo = calculaCoordGeograficas();

		double latAtual = coordGeo[0];
		double lonAtual = coordGeo[1];

		double distancia = calculaDistancia(latInicial, lonInicial, latAtual, lonAtual);

		System.out.println(idCar + " percorreu " + distancia + " metros");
		if (distancia > (distanciaPercorrida + 100)) {
			distanciaPercorrida += 100;
		}
	}
}