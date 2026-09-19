package model;

/**
 * Modello dati che rappresenta un file allegato associato a una Issue.
 * Contiene i metadati del file e il suo contenuto binario grezzo, pronto per essere inviato al server
 * o renderizzato graficamente nell'interfaccia JavaFX.
 */
@SuppressWarnings("unused")
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
