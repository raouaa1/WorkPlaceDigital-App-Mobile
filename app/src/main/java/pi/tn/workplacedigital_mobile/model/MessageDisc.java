package pi.tn.workplacedigital_mobile.model;

public class MessageDisc {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long discussionId; // <-- à ajouter si tu utilises discussionId
    private String content;
    private String timestamp;

    // Constructeur vide
    public MessageDisc() {
    }

    // Constructeur complet
    public MessageDisc(Long id, Long senderId, Long receiverId, Long discussionId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.discussionId = discussionId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getDiscussionId() { return discussionId; }
    public void setDiscussionId(Long discussionId) { this.discussionId = discussionId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
