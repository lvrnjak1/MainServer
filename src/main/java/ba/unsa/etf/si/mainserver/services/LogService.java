package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.models.Log;
import ba.unsa.etf.si.mainserver.repositories.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<Log> getAll() {
        return logRepository.findAll();
    }

    public Log save(Log log) {
        return logRepository.save(log);
    }
}
