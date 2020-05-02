package ba.unsa.etf.si.mainserver.services.admin.logs;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.admin.logs.LogCollectionResponse;
import ba.unsa.etf.si.mainserver.responses.admin.logs.LogResponse;
import ba.unsa.etf.si.mainserver.responses.admin.logs.SimpleActionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class LogServerService {

    private final RestTemplate restTemplate;

    @Value("${app.logServer}")
    private String logServerUrl;

    @Value("${app.pass}")
    private String pass;

    public LogServerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public LogCollectionResponse getLogsFromServer(String username, Long from, Long to, String action, String object) {
        StringBuilder url = new StringBuilder(logServerUrl + "/logs");
        ArrayList<String> entries = new ArrayList<>();
        if (username != null) {
            entries.add("username=" + username);
        }
        if (from != null) {
            entries.add("from=" + from);
        }
        if (to != null) {
            entries.add("to=" + to);
        }
        if (action != null) {
            entries.add("action=" + action);
        }
        if (object != null) {
            entries.add("object=" + object);
        }
        if (entries.size() != 0) {
            url.append("?");
            for (String entry : entries) {
                url.append(entry).append("&");
            }
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("pass", pass);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<LogCollectionResponse> response = null;
        try{
            response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    request,
                    LogCollectionResponse.class
            );
        }catch (HttpServerErrorException ignored){
            throw new AppException("Cannot establish connection to server");
        }


        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            throw new AppException("Cannot establish connection to server");
        }
        return response.getBody();
    }

    public void documentAction(String username, String actionName, String actionObject, String actionDescription) {
        if (logServerUrl.contains("localhost")) {
            return;
        }
        String url = logServerUrl + "/logs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("pass", pass);

        LogResponse requestBody = new LogResponse(
                username,
                LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset()),
                new SimpleActionResponse(actionName,actionObject,actionDescription)
        );

        HttpEntity<LogResponse> entity = new HttpEntity<>(requestBody, headers);
        System.out.println(restTemplate.postForObject(url, entity, String.class));
    }

    public void broadcastNotification(NotificationRequest notification, String receiver) {
        if (logServerUrl.contains("localhost")) {
            return;
        }
        String url = logServerUrl + "/notify/" + receiver;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("pass", pass);

        HttpEntity<NotificationRequest> entity = new HttpEntity<>(notification, headers);
        System.out.println(restTemplate.postForObject(url, entity, String.class));
    }
}
