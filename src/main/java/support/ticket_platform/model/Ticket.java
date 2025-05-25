package support.ticket_platform.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Ticket {

    public enum Stato {
    COMPLETATO("COMPLETATO"), 
    IN_CORSO("IN CORSO"), 
    DA_FARE("DA FARE");

    
        private final String displayValue;
        
        private Stato(String displayValue) {
            this.displayValue = displayValue;
        }
        
        public String getDisplayValue() {
            return displayValue;
        }
    }

    // // public enum Stato {
    //     DA_FARE,
    //     IN_CORSO,
    //     COMPLETATO
    // }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(nullable = false)
    @NotBlank(message="Il campo non può essere vuoto")
    private String titolo;

    @Column(length = 2000, nullable = false)
    @Size(max = 2000, message = "La descrizione non può superare 2000 caratteri")
    @NotBlank(message="Il campo non può essere vuoto.")
    private String descrizione;

    private LocalDateTime dataCreazione;

    //DICE A JPA COME SALVARE NEL DB IL CAMPO,LO SALVA COME STRINGA//
    @Enumerated(EnumType.STRING)
    private Stato stato;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable=false)
    @JsonBackReference(value = "cat-ticket")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    @JsonBackReference(value = "user-ticket") 
    private User user;

    @OneToMany(mappedBy = "ticket")
    @JsonBackReference
    private List<Nota> note;

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getNome() {
        return titolo;
    }

    public void setNome(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Nota> getNote() {
        return note;
    }

    public void setNote(List<Nota> note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + Id +
                ", titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", dataCreazione=" + dataCreazione +
                ", stato=" + stato +
                ", categoria=" + (categoria != null ? categoria.getId() : "null") +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }

    
    
}
