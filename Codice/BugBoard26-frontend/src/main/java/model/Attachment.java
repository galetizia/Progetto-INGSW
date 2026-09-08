package model;

public class Attachment {

    private int id;
    private String nome;
    private String tipo;
    private byte[] contenuto;

    public Attachment() {}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getTipo() {return tipo;}
    public void setTipo(String tipo) {this.tipo = tipo;}
    public byte[] getContenuto() {return contenuto;}
    public void setContenuto(byte[] contenuto) {this.contenuto = contenuto;}
}
