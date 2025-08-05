package pi.tn.workplacedigital_mobile.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String message;
    private String dateEnvoi;
    private boolean vue;
    private Long userId;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(String dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isVue() { return vue; }
    public void setVue(boolean vue) { this.vue = vue; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
