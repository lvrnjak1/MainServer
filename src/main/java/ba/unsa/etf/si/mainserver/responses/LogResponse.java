package ba.unsa.etf.si.mainserver.responses;

import ba.unsa.etf.si.mainserver.models.Log;

import java.time.Instant;

public class LogResponse {
    private String message;
    private Instant time;

    public LogResponse() {
    }

    public LogResponse(Log log) {
        this.message = log.getMessage();
        this.time = log.getCreatedAt().toInstant();
    }

    public LogResponse(String message, Instant time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
