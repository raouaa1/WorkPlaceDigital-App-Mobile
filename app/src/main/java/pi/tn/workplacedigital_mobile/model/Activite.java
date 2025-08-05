package pi.tn.workplacedigital_mobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Activite {

    private Long id;
    private String nom;

    @SerializedName("dateActivite")
    private Date dateActivite;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateActivite() {
        return dateActivite;
    }

    public void setDateActivite(Date dateActivite) {
        this.dateActivite = dateActivite;
    }
}
