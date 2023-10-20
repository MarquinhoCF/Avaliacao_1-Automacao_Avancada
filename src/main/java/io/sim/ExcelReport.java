package io.sim;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.sim.bank.TransferData;
import io.sim.company.Company;
import io.sim.driver.Driver;
import io.sim.driver.DrivingData;
import io.sim.fuelStation.FuelStation;

public class ExcelReport {
    private static final String fileNameDD = "RelatorioCarros.xlsx";
    private static final String fileNameTD = "RelatorioContasCorrente.xlsx";

    public static void criaPlanilhas(Company company, ArrayList<Driver> drivers, FuelStation fuelStation) {
        criaPlanilhaCar(drivers);
        criaPlanilhaAccount(company, drivers, fuelStation);
    }

    private static void criaPlanilhaCar(ArrayList<Driver> drivers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (Driver driver : drivers) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(driver.getCarID());
                criaCabecalhoCar(sheet);
            }

            // Salve o arquivo Excel após criar todas as abas de planilha.
            try (FileOutputStream outputStream = new FileOutputStream(fileNameDD)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void criaCabecalhoCar(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Timestamp");
        headerRow.createCell(1).setCellValue("ID Car");
        headerRow.createCell(2).setCellValue("ID Route");
        headerRow.createCell(3).setCellValue("Speed");
        headerRow.createCell(4).setCellValue("Distance");
        headerRow.createCell(5).setCellValue("FuelConsumption");
        headerRow.createCell(6).setCellValue("FuelType");
        headerRow.createCell(7).setCellValue("CO2Emission");
        headerRow.createCell(8).setCellValue("Longitude (Lon)");
        headerRow.createCell(9).setCellValue("Latitude (Lat)");
    }

    public static void atualizaPlanilhaCar(DrivingData carReport) {
        synchronized (ExcelReport.class) {
            try (FileInputStream inputStream = new FileInputStream(fileNameDD);
                Workbook workbook = WorkbookFactory.create(inputStream);
                FileOutputStream outputStream = new FileOutputStream(fileNameDD)) {
            
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(carReport.getCarID());    
                
                int lastRowNum = sheet.getLastRowNum();
                Row newRow = sheet.createRow(lastRowNum + 1);

                // Preencha as células da nova linha com os dados da classe TransferData
                newRow.createCell(0).setCellValue(carReport.getTimeStamp());
                newRow.createCell(1).setCellValue(carReport.getCarID());
                newRow.createCell(2).setCellValue(carReport.getRouteIDSUMO());
                newRow.createCell(3).setCellValue(carReport.getSpeed());
                newRow.createCell(4).setCellValue(carReport.getDistance()); 
                newRow.createCell(5).setCellValue(carReport.getFuelConsumption());
                newRow.createCell(6).setCellValue(carReport.getFuelType());
                newRow.createCell(7).setCellValue(carReport.getCo2Emission());
                newRow.createCell(8).setCellValue(carReport.getLonAtual());
                newRow.createCell(9).setCellValue(carReport.getLatAtual());
                
                // Salve as alterações na planilha
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   
    }

    private static void criaPlanilhaAccount(Company company, ArrayList<Driver> drivers, FuelStation fuelStation){
        try (Workbook workbook = new XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet1 = workbook.createSheet(company.getAccountID());
            criaCabecalhoAccount(sheet1);
            org.apache.poi.ss.usermodel.Sheet sheet2 = workbook.createSheet(fuelStation.getFSAccountID());
            criaCabecalhoAccount(sheet2);
            
            for (Driver driver : drivers) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(driver.getID());
                criaCabecalhoAccount(sheet);
            }

            // Salve o arquivo Excel após criar todas as abas de planilha.
            try (FileOutputStream outputStream = new FileOutputStream(fileNameTD)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void criaCabecalhoAccount(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Account ID");
        headerRow.createCell(1).setCellValue("Pagador");
        headerRow.createCell(2).setCellValue("Operacao");
        headerRow.createCell(3).setCellValue("Recebedor");
        headerRow.createCell(4).setCellValue("Valor");
        headerRow.createCell(5).setCellValue("Timestamp");
    }

    public static void atualizaPlanilhaAccount(TransferData transferData) {
        synchronized (ExcelReport.class) {
            try (FileInputStream inputStream = new FileInputStream(fileNameTD);
                Workbook workbook = WorkbookFactory.create(inputStream);
                FileOutputStream outputStream = new FileOutputStream(fileNameTD)) {
            
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(transferData.getAccountID());    
                
                int lastRowNum = sheet.getLastRowNum();
                Row newRow = sheet.createRow(lastRowNum + 1);

                // Preencha as células da nova linha com os dados da classe TransferData
                newRow.createCell(0).setCellValue(transferData.getAccountID());
                newRow.createCell(1).setCellValue(transferData.getPagador());
                newRow.createCell(2).setCellValue(transferData.getOperacao());
                newRow.createCell(3).setCellValue(transferData.getRecebedor());
                newRow.createCell(4).setCellValue(transferData.getQuantia()); 
                newRow.createCell(5).setCellValue(transferData.getTimestamp());
                
                // Salve as alterações na planilha
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }      
    }

}
