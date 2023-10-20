package io.sim.driver;

import de.tudresden.sumo.cmd.Vehicle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;
import io.sim.AESencrypt;
import io.sim.JSONConverter;
import io.sim.company.Company;
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
	private boolean finalizado; // chamado pelo Driver
	private long acquisitionRate; // taxa de aquisicao de dados dos sensores
	private int fuelType; 			// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private int fuelPreferential; 	// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double fuelPrice; 		// price in liters
	private int personCapacity;		// the total number of persons that can ride in this vehicle
	private int personNumber;		// the total number of persons which are riding in this vehicle
	private double speed; //NEWF
	private Rota rota;
	private double fuelTank;
	private double maxFuelCapacity;
	private String carStatus;
	private double latInicial;
	private double lonInicial;
	private double latAtual;
	private double lonAtual;
	private DrivingData drivingDataAtual;
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

		this.finalizado = false;
		this.fuelPrice = _fuelPrice;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;
		this.speed = 50;
		this.rota = null;
		this.fuelTank = 10000;
		this.maxFuelCapacity = 55000;
		this.carStatus = "aguardando";
		this.drivingRepport = new ArrayList<DrivingData>();
		
		this.drivingDataAtual = new DrivingData(idCar, driverID, "aguardando", 0, 0, 0, 0, 
												0 , "", 0, 0, 0, this.fuelType, 0);
	}

	@Override
	public void run() {
		System.out.println(this.idCar + " iniciando");
		AtualizaTanque at = new AtualizaTanque(this);
		at.start();

		try {
            socket = new Socket(this.companyServerHost, this.companyServerPort);
            entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());

			// System.out.println(this.idCar + " conectado!!");
			int numBytesMsg;
			byte[] mensagemEncriptada;

			while (!finalizado) {
				// Manda "aguardando" da primeira vez
				mensagemEncriptada = AESencrypt.encripta(JSONConverter.criarJSONDrivingData(drivingDataAtual));
				saida.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
				saida.write(mensagemEncriptada);

				System.out.println(this.idCar + " aguardando rota");
				numBytesMsg = JSONConverter.extraiTamanhoBytes(AESencrypt.decripta(entrada.readNBytes(AESencrypt.getTamNumBytes())));
                rota = JSONConverter.extraiRota(AESencrypt.decripta(entrada.readNBytes(numBytesMsg)));

				if(rota.getID().equals("-1")) {
					System.out.println(this.idCar +" - Sem rotas a receber.");
					finalizado = true;
					break;
				}

				System.out.println(this.idCar + " leu " + rota.getID());

				ts = new TransportService(true, this.idCar, rota, this, this.sumo);
				ts.start();
				//System.out.println("CAR - TransportService ativo");
			
				String edgeFinal = this.getEdgeFinal(); 
				this.on_off = true;
				while(!Company.estaNoSUMO(this.idCar, this.sumo)) {
					Thread.sleep(this.acquisitionRate);
				}
				String edgeAtual = (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar));

				boolean initRoute = true;
				while (this.on_off) {
					// Thread.sleep(this.acquisitionRate);
					if (initRoute) {
						double[] coordGeo = calculaCoordGeograficas();
						latInicial = coordGeo[0];
						lonInicial = coordGeo[1];
						initRoute = false;
					}

					if(verificaRotaTerminada(edgeAtual, edgeFinal)) {
						System.out.println(this.idCar + " acabou a rota.");
						//this.ts.setOn_off(false);
						this.carStatus = "finalizado";
						mensagemEncriptada = AESencrypt.encripta(JSONConverter.criarJSONDrivingData(drivingDataAtual));
						saida.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
						saida.write(mensagemEncriptada);
						this.on_off = false;
						break;
					} 

					Thread.sleep(this.acquisitionRate);
					
					if(!verificaRotaTerminada(edgeAtual, edgeFinal)) {
						// System.out.println(this.idCar + " -> edge atual: " + edgeAtual);
						System.out.println(this.idCar + " -> fuelTank: " + fuelTank);
						double[] coordGeo = calculaCoordGeograficas(); // BOzaço aqui IMPORTANTE TRATAR ISSO
						latAtual = coordGeo[0];
						lonAtual = coordGeo[1];
						atualizaSensores();
						if (carStatus != "abastecendo") {
							this.carStatus = "rodando";
						}
						
						mensagemEncriptada = AESencrypt.encripta(JSONConverter.criarJSONDrivingData(drivingDataAtual));
						saida.write(AESencrypt.encripta(JSONConverter.criaJSONTamanhoBytes(mensagemEncriptada.length)));
						saida.write(mensagemEncriptada);
						
						if(this.carStatus.equals("finalizado")) {
							this.on_off = false;
							break;
						} else {
							edgeAtual = (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar));
						}
					}
				}
				System.out.println(this.idCar + " off.");

				if(!finalizado) {
					if (carStatus != "abastecendo") {
						this.carStatus = "aguardando";
					}
				}

				if(finalizado) {
					this.carStatus = "encerrado";
				}
			}
			System.out.println("Encerrando: " + idCar);
			entrada.close();
			saida.close();
			socket.close();
			this.ts.setTerminado(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

		System.out.println(this.idCar + " encerrado.");
	}

	private void atualizaSensores() {
		try {
			if (!this.getSumo().isClosed()) {

				// System.out.println("CarID: " + this.getIdCar());
				// System.out.println("RoadID: " + (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar)));
				// System.out.println("RouteID: " + (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)));
				// System.out.println("RouteIndex: " + this.sumo.do_job_get(Vehicle.getRouteIndex(this.idCar)));
				
				// Criacao dos dados de conducao do veiculo
				drivingDataAtual = new DrivingData(
						this.idCar, this.driverID, this.carStatus, this.latInicial, this.lonInicial,
						this.latAtual, this.lonAtual,
						
						System.currentTimeMillis(), (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)), 
						(double) this.sumo.do_job_get(Vehicle.getSpeed(this.idCar)), 
						(double) sumo.do_job_get(Vehicle.getDistance(this.idCar)), // IMPORTANTE alterar para calculo
						(double) this.sumo.do_job_get(Vehicle.getFuelConsumption(this.idCar)), this.fuelType,
						(double) this.sumo.do_job_get(Vehicle.getCO2Emission(this.idCar)));
						// Vehicle's fuel consumption in ml/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30

						// Vehicle's CO2 emissions in mg/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30
						
						// 1/*averageFuelConsumption (calcular)*/,

				this.drivingRepport.add(drivingDataAtual);
				
				if (carStatus != "abastecendo") {
					this.setSpeed(speed);
				}

			} else {
				this.on_off = false;
				System.out.println("SUMO is closed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public boolean getFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean _finalizado) {
		this.finalizado = _finalizado;
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

	public double getConsumoCombustivel() {
		return drivingDataAtual.getFuelConsumption();
	}

	public void gastaCombustivel(double litros) {
		if (fuelTank >= litros) {
			fuelTank -= litros;
		} else {
			try {
				pararCarro();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public double getNivelDoTanque() {
		return this.fuelTank;
	}

	public double getCapacidadeDoTanque() {
		return this.maxFuelCapacity;
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

	public double getSpeed() throws Exception{
		return (double) this.sumo.do_job_get(Vehicle.getSpeed(this.idCar));
	}

	public void setSpeed(double speed) throws Exception {
		this.sumo.do_job_set(Vehicle.setSpeedMode(this.idCar, 0));
		this.sumo.do_job_set(Vehicle.setSpeed(this.idCar, speed));
	}

	public void pararCarro() throws Exception{
		setSpeed(0);
	}

	public void preparaAbastecimento() throws Exception{
		carStatus = "abastecendo";
		pararCarro();
	}

	public void abastecido(double litros) throws Exception{
		fuelTank += litros;
		carStatus = "rodando";
		setSpeed(speed);
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

	private double[] calculaCoordGeograficas() throws Exception {
		SumoPosition2D sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idCar)); // BOzaço aqui IMPORTANTE TRATAR ISSO

		double x = sumoPosition2D.x; // coordenada X em metros
		double y = sumoPosition2D.y; // coordenada Y em metros

		double raioTerra = 6371000; // raio médio da Terra em metros

		double latRef = -22.986731;
		double lonRef = -43.217054;

		// Conversão de metros para graus
		double lat = latRef + (y / raioTerra) * (180 / Math.PI);
		double lon = lonRef + (x / raioTerra) * (180 / Math.PI) / Math.cos(latRef * Math.PI / 180);

		double[] coordGeo = new double[] { lat, lon };
		return coordGeo;
	}

}