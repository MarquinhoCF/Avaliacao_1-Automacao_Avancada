package io.sim.bank;

class Register {
    private String usuario;
    private String operacao;
    private String usuario2;
    private double quantia;

    public Register(String _usuario, String _operacao, String _usuario2, double _quantia) {
        this.usuario = _usuario;
        this.operacao = _operacao;
        this.usuario2 = _usuario2;
        this.quantia = _quantia;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getUsuario2() {
        return usuario2;
    }

    public double getQuantia() {
        return quantia;
    }

    public String getDescricao() {
        if (operacao.equals("Pagamento")){
            return usuario + " transferiu R$" + quantia + " para " + usuario2;
        } else {
            return usuario + " recebeu R$" + quantia + " de " + usuario2;
        }
    }
    
}
