package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.Table;
import ba.unsa.etf.si.mainserver.repositories.business.TableRepository;
import ba.unsa.etf.si.mainserver.requests.business.TableRequest;
import ba.unsa.etf.si.mainserver.responses.business.TableResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    private final TableRepository tableRepository;
    private final OfficeService officeService;

    public TableService(TableRepository tableRepository, OfficeService officeService) {
        this.tableRepository = tableRepository;
        this.officeService = officeService;
    }

    public Table findById(Long tableId){
        return tableRepository
                .findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("This table doesn't exist"));
    }

    public boolean isInOffice(Long tableId, Long officeId){
        return tableRepository.findAllByOffice_Id(officeId)
                .stream()
                .anyMatch(table -> table.getId().equals(tableId));
    }

    public boolean isUniqueTableNameInOffice(String tableName, Office office) {
        return !tableRepository.findByTableNameInOfficeAndOffice(tableName, office).isPresent();
    }

    public Table save(Table table){
        if(!isUniqueTableNameInOffice(table.getTableNameInOffice(), table.getOffice())){
            throw new BadParameterValueException("Table with this name already exists in office " +
                    table.getOffice().getId());
        }
        return tableRepository.save(table);
    }

    public List<Table> getAllByOfficeId(Long officeId){
        return tableRepository.findAllByOffice_Id(officeId);
    }

    public static TableResponse mapTableToTableResponse(Table table){
        return new TableResponse(table.getId(), table.getTableNameInOffice());
    }

    public Table getFromTableRequestAndOfficeId(TableRequest tableRequest, Long officeId){
        Office office = officeService.findByIdOrThrow(officeId);
        return new Table(tableRequest.getTableName(), office);
    }

    public void delete(Long tableId) {
        tableRepository.delete(findById(tableId));
    }
}
