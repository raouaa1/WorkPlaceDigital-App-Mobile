package pi.tn.workplacedigital_mobile.model;

public class Collaborateur {
    private Long id;
    private String nom;
    private String poste;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
}
