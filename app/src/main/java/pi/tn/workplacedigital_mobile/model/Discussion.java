package pi.tn.workplacedigital_mobile.model;

public class Discussion {

    private Long id;         // Identifiant unique de la discussion
    private String title;    // Titre ou nom de la discussion (ex : nom du contact ou groupe)
    private String lastMessage; // (Optionnel) Dernier message résumé
    private String lastMessageTimestamp; // (Optionnel) Date/heure du dernier message

    public Discussion() {
    }

    public Discussion(Long id, String title, String lastMessage, String lastMessageTimestamp) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
