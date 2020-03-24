package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.models.Log;
import ba.unsa.etf.si.mainserver.requests.LogRequest;
import ba.unsa.etf.si.mainserver.responses.LogResponse;
import ba.unsa.etf.si.mainserver.services.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public List<LogResponse> getLogs() {
        return logService.getAll().stream().map(LogResponse::new).collect(Collectors.toList());
    }

    @PostMapping("/logs")
    public LogResponse postLog(@RequestBody LogRequest logRequest) {
        Log log = new Log();
        log.setMessage(logRequest.getMessage());
        log = logService.save(log);
        return new LogResponse(logRequest.getMessage(), log.getCreatedAt().toInstant());
    }

}
