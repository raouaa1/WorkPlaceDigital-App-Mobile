package pi.tn.workplacedigital_mobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Signalement {

    private Long id;

    private String motif;

    @SerializedName("dateSignalement")
    private Date dateSignalement;  // Optionnel, backend gère date si null

    public Signalement() {}

    public Signalement(String motif) {
        this.motif = motif;
    }

    // getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public Date getDateSignalement() { return dateSignalement; }
    public void setDateSignalement(Date dateSignalement) { this.dateSignalement = dateSignalement; }
}
