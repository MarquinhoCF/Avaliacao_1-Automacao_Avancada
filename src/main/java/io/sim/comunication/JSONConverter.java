package io.sim.comunication;

import org.json.JSONObject;

import io.sim.company.Rota;
import io.sim.driver.DrivingData;
import io.sim.bank.TransferData;

// Classe para tratar as comunicacoes entre classes
public class JSONConverter {

    public JSONConverter() {

    }

    //                              Usadas no envio e recebimento de mensagens via Socket
    //--------------------------------------------------------------------------------------------------------------------------

    public static String criaJSONTamanhoBytes(int numBytes) {
        JSONObject my_json = new JSONObject();
        my_json.put("Num Bytes", numBytes);
        return my_json.toString();
    }

    public static int extraiTamanhoBytes(String numBytesJSON) {
        JSONObject my_json = new JSONObject(numBytesJSON);
        int numBytes = my_json.getInt("Num Bytes");
        return numBytes;
    }

    //                                        Usadas na lógica de pagamentos
    //--------------------------------------------------------------------------------------------------------------------------

    // -> CLIENTE
    // Chamada em BotPayment
    public static String criarJSONLogin(String[] login) {
        JSONObject loginJSONObj = new JSONObject();
        loginJSONObj.put("ID do Pagador", login[0]);
		loginJSONObj.put("Senha do Pagador", login[1]);
        return loginJSONObj.toString();
    }

    // -> SERVIDOR
    // Chamada em AccountManipulator
    public static String[] extraiLogin(String loginJSON) {
        JSONObject loginJSONObj = new JSONObject(loginJSON);
        String[] login = new String[] { loginJSONObj.getString("ID do Pagador"), loginJSONObj.getString("Senha do Pagador") };
        return login;
    }

    // -> SERVIDOR
    // Chamada em AccountManipulator
    public static TransferData extraiTransferData(String transferDataJSON) {
        JSONObject transferDataJSONObj = new JSONObject(transferDataJSON);
		String pagador = transferDataJSONObj.getString("ID do Pagador");
        String operacao = transferDataJSONObj.getString("Operacao");
        String recebedor = transferDataJSONObj.getString("ID do Recebedor");
		double quantia = transferDataJSONObj.getDouble("Quantia");
        TransferData tf = new TransferData(pagador, operacao, recebedor, quantia);
		return tf;
	}

    // -> CLIENTE
    // Chamada em BotPayment
    public static String criaJSONTransferData(TransferData transferData) {
        JSONObject transferDataJSON = new JSONObject();
		transferDataJSON.put("ID do Pagador", transferData.getPagador());
        transferDataJSON.put("Operacao", transferData.getOperacao());
        transferDataJSON.put("ID do Recebedor", transferData.getRecebedor());
		transferDataJSON.put("Quantia", transferData.getQuantia());
		return transferDataJSON.toString();
	}

    // -> SERVIDOR
    // Chamada em AccountManipulator
    public static String criaRespostaTransferencia(boolean sucesso) {
        JSONObject my_json = new JSONObject();
        my_json.put("Resposta", sucesso);
        return my_json.toString();
    }

    // -> CLIENTE
    // Chamada em BotPayment
    public static boolean extraiResposta(String respostaJSON) {
        JSONObject resposta = new JSONObject(respostaJSON);
        return resposta.getBoolean("Resposta");
    }

    //                                    Usadas na lógica de tratamento de rotas
    //--------------------------------------------------------------------------------------------------------------------------

    // -> SERVIDOR
    // CarManipulator
    public static DrivingData extraiDrivingData(String drivingDataJSON) {
		JSONObject drivingDataJSONObj = new JSONObject(drivingDataJSON);
        String carID = drivingDataJSONObj.getString("Car ID");
        String driverID = drivingDataJSONObj.getString("Driver ID");
        String carStatus = drivingDataJSONObj.getString("Car Status");
		double latInicial = drivingDataJSONObj.getDouble("Latitude Inicial");
        double lonInicial = drivingDataJSONObj.getDouble("Longitude Inicial");
        double latAtual = drivingDataJSONObj.getDouble("Latitude Atual");
        double lonAtual = drivingDataJSONObj.getDouble("Longitude Atual");
        long timeStamp = drivingDataJSONObj.getLong("TimeStamp");
        String routeIDSUMO = drivingDataJSONObj.getString("RouteIDSUMO");
        double speed = drivingDataJSONObj.getDouble("Speed");
        double distance = drivingDataJSONObj.getDouble("Distance");
        double fuelConsumption = drivingDataJSONObj.getDouble("FuelConsumption");
        int fuelType = drivingDataJSONObj.getInt("FuelType");
        double Co2Emission = drivingDataJSONObj.getDouble("Co2Emission");

        DrivingData drivingData = new DrivingData(carID, driverID, carStatus, latInicial, lonInicial, latAtual, lonAtual, timeStamp,
                                                    routeIDSUMO, speed, distance, fuelConsumption, fuelType, Co2Emission);

		return drivingData;
	}

    // -> CLIENTE
    // Car
	public static String criarJSONDrivingData(DrivingData drivingData) {
        JSONObject drivingDataJSON = new JSONObject();
        drivingDataJSON.put("Car ID", drivingData.getCarID());
        drivingDataJSON.put("Driver ID", drivingData.getDriverID());
        drivingDataJSON.put("Car Status", drivingData.getCarStatus());
		drivingDataJSON.put("Latitude Inicial", drivingData.getLatInicial());
        drivingDataJSON.put("Longitude Inicial", drivingData.getLonInicial());
        drivingDataJSON.put("Latitude Atual", drivingData.getLatAtual());
        drivingDataJSON.put("Longitude Atual", drivingData.getLonAtual());
		drivingDataJSON.put("TimeStamp", drivingData.getTimeStamp());
        drivingDataJSON.put("RouteIDSUMO", drivingData.getRouteIDSUMO());
        drivingDataJSON.put("Speed", drivingData.getSpeed());
		drivingDataJSON.put("Distance", drivingData.getDistance());
        drivingDataJSON.put("FuelConsumption", drivingData.getFuelConsumption());
        drivingDataJSON.put("FuelType", drivingData.getFuelType());
        drivingDataJSON.put("Co2Emission", drivingData.getCo2Emission());
        return drivingDataJSON.toString();
	}

    // -> SERVIDOR
    // CarManipulator
    public static String criaJSONRota(Rota rota) {
        JSONObject rotaJSON = new JSONObject();
        rotaJSON.put("ID da Rota", rota.getID());
        rotaJSON.put("Edges", rota.getEdges());
        return rotaJSON.toString();
    }

    // -> CLIENTE
    // Car
    public static Rota extraiRota(String rotaJSON) {
        JSONObject rotaJSONObj = new JSONObject(rotaJSON);
		Rota rota = new Rota(rotaJSONObj.getString("ID da Rota"), rotaJSONObj.getString("Edges"));
        return rota;
	}

}
