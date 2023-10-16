package io.sim.bank;

class Register {
    private String usuario;
    private String recebedor;
    private double quantia;

    public Register(String _usuario, String _operacao, String _recebedor, double _quantia) {
        this.usuario = _usuario;
        this.recebedor = _recebedor;
        this.quantia = _quantia;
    }

    public String getDescricao() {
        return usuario + " transferiu R$" + quantia + " para " + recebedor;
    }
    
}
