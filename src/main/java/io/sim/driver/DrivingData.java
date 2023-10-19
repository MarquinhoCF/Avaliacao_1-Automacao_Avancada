package io.sim.driver;

//import java.io.Serializable;

/**Armazena dados do veiculo
 * Funcao organizacional, para ser usada no relatorio via Excel.
 */

//public class DrivingData implements Serializable{
public class DrivingData {

		/* Informações adicionais para lógica de tratamento de rotas */
	
	private String carID;
	private String driverID;
	private String carStatus;
	private double latInicial;
	private double lonInicial;
	private double latAtual;
	private double lonAtual;

		/* SUMO's data */

	private long timeStamp; 			// System.currentTimeMillis() IMP# precisa ser em nanosegundos
	private String routeIDSUMO; 		// this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto))
	private double speed; 				// in m/s for the last time step
	private double distance;
	private double fuelConsumption; 	// in mg/s for the last time step
	private int fuelType; 				// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double co2Emission; 		// in mg/s for the last time step

	public DrivingData(String carID, String driverID, String carStatus, double latInicial, double lonInicial, double latAtual,
		double lonAtual, long timeStamp, String routeIDSUMO, double speed, double distance, double fuelConsumption, int fuelType,
		double co2Emission) {
		
		this.carID = carID;
		this.driverID = driverID;
		this.carStatus = carStatus;
		this.latInicial = latInicial;
		this.lonInicial = lonInicial;
		this.latAtual = latAtual;
		this.lonAtual = lonAtual;

		this.timeStamp = timeStamp;
		this.routeIDSUMO = routeIDSUMO;
		this.speed = speed;
		this.distance = distance;
		this.fuelConsumption = fuelConsumption;
		this.fuelType = fuelType;
		this.co2Emission = co2Emission;
	}

	public String getCarID() {
		return this.carID;
	}

	public String getDriverID() {
		return this.driverID;
	}

	public String getCarStatus() {
		return this.carStatus;
	}

	public double getLatInicial() {
		return this.latInicial;
	}

	public double getLonInicial() {
		return this.lonInicial;
	}

	public double getLatAtual() {
		return this.latAtual;
	}
	public double getLonAtual() {
		return this.lonAtual;
	}


	/* SUMO's data getters e setters */

	public long getTimeStamp() {
		return timeStamp;
	}

	public String getRouteIDSUMO() {
		return routeIDSUMO;
	}

	public double getSpeed() {
		return speed;
	}

	public double getDistance() {
		return distance;
	}

	public double getFuelConsumption() {
		return fuelConsumption;
	}

	public int getFuelType() {
		return fuelType;
	}

	public double getCo2Emission() {
		return co2Emission;
	}

}