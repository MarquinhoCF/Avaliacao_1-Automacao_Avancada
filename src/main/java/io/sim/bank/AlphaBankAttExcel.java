package io.sim.bank;

import io.sim.ExcelReport;

public class AlphaBankAttExcel extends Thread {
    AlphaBank alphaBank;

    public AlphaBankAttExcel(AlphaBank _alphaBank) {
        this.alphaBank = _alphaBank;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(10);
                if (alphaBank.temRegistro()) {
                    TransferData registro = alphaBank.pegaRegistro();
                    ExcelReport.atualizaPlanilhaAccount(registro);
                    
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
