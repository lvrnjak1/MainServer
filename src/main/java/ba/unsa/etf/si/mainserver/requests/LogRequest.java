package ba.unsa.etf.si.mainserver.requests;

public class LogRequest {
    private String message;

    public LogRequest() {
    }

    public LogRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
