package pi.tn.workplacedigital_mobile.model;

public class Message {
    private String sender;
    private String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
    public String getSender() {
        return sender;
    }
    public String getContent() {
        return content;
    }
    @Override
    public String toString() {
        // Affichage résumé pour historique
        return sender + ": " + (content.length() > 30 ? content.substring(0,30) + "..." : content);
    }
}
