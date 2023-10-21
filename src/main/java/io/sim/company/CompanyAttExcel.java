package io.sim.company;

import io.sim.report.ExcelReport;

public class CompanyAttExcel extends Thread{
    Company company;

    public CompanyAttExcel(Company _company) {
        this.company = _company;
    }

    @Override
    public void run() {
        try {
            while (Company.temRotasDisponiveis()) {
                Thread.sleep(10);
                if (company.temReport()) {
                    ExcelReport.atualizaPlanilhaCar(company.pegaComunicacao());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
