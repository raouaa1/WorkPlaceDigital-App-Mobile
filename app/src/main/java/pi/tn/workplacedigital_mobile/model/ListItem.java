package pi.tn.workplacedigital_mobile.model;

import pi.tn.workplacedigital_mobile.model.User;

/**
 * Classe mixte pour gérer une liste dans RecyclerView
 * avec deux types d'items :
 * - TYPE_TITLE : titre de section
 * - TYPE_USER : utilisateur avec statut estAmi
 */
public class ListItem {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_USER = 1;

    private int type;
    private String title; // pour TYPE_TITLE
    private User user;    // pour TYPE_USER
    private boolean isAmi; // true si user est ami avec currentUser

    public ListItem(int type, String title, User user) {
        this.type = type;
        this.title = title;
        this.user = user;
    }

    public ListItem(int type, String title, User user, boolean isAmi) {
        this.type = type;
        this.title = title;
        this.user = user;
        this.isAmi = isAmi;
    }

    // Getters et setters
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAmi() {
        return isAmi;
    }
    public void setAmi(boolean ami) {
        isAmi = ami;
    }
}
