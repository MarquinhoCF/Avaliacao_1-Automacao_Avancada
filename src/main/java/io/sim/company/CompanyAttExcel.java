package io.sim.company;

import io.sim.report.ExcelReport;

/**
 *      A classe CompanyAttExcel é responsável por atualizar relatórios no formato Excel, com base nas informações de comunicação 
 * recebidas pela classe Company.
 */
public class CompanyAttExcel extends Thread {
    Company company; // Uma instância da classe Company

    public CompanyAttExcel(Company _company) {
        this.company = _company;
    }

    @Override
    public void run() {
        try {
            // Loop principal que verifica a disponibilidade de rotas
            while (Company.temRotasDisponiveis()) {
                Thread.sleep(10); // Aguarda por um curto período (10 milissegundos) para evitar uso intensivo da CPU
                if (company.temReport()) {
                    // Se a instância da classe Company possui relatórios para serem atualizados
                    ExcelReport.atualizaPlanilhaCar(company.pegaComunicacao());  // Atualiza o relatório no Excel
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
