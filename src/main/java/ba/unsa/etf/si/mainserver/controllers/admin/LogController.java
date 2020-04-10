package ba.unsa.etf.si.mainserver.controllers.admin;

import ba.unsa.etf.si.mainserver.responses.admin.logs.LogCollectionResponse;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class LogController {
    private final LogServerService logServerService;

    public LogController(LogServerService logServerService) {
        this.logServerService = logServerService;
    }

    @GetMapping("/logs")
    public ResponseEntity<LogCollectionResponse> getLogs(
            @RequestParam(name = "username",required = false) String username,
            @RequestParam(name = "from",required = false) Long from,
            @RequestParam(name = "to",required = false) Long to,
            @RequestParam(name = "action",required = false) String action,
            @RequestParam(name = "object",required = false) String object
    ) {
        return ResponseEntity.ok(logServerService.getLogsFromServer(username,from,to,action,object));
    }
}
