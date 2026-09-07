package bugboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attachment")
@Inheritance(strategy = InheritanceType.JOINED)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String nome; // es. "errore.png"
    @Column(nullable = false)
    private String tipo; // es. "image/png"
    // il tipo BYTEA di Postgres = un array di byte in java
    @Column(nullable = false)
    private byte[] contenuto;

    public Attachment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public byte[] getContenuto() { return contenuto; }
    public void setContenuto(byte[] contenuto) { this.contenuto = contenuto; }
}
