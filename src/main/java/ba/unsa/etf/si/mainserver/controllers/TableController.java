package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.Table;
import ba.unsa.etf.si.mainserver.requests.business.TableRequest;
import ba.unsa.etf.si.mainserver.responses.business.TableResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.business.TableService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TableController {
    private final TableService tableService;
    private final OfficeService officeService;
    private final BusinessService businessService;

    public TableController(TableService tableService, OfficeService officeService, BusinessService businessService) {
        this.tableService = tableService;
        this.officeService = officeService;
        this.businessService = businessService;
    }

    @GetMapping("/offices/{officeId}/tables")
    @Secured({"ROLE_OFFICEMAN", "ROLE_MERCHANT"})
    public List<TableResponse> getTablesForOffice(@PathVariable Long officeId,
                                                  @CurrentUser UserPrincipal userPrincipal){
        return  getAllTablesForOfficeAndBusiness(officeService.findByIdOrThrow(officeId),
                businessService.findBusinessOfCurrentUser(userPrincipal))
                .stream()
                .map(TableService::mapTableToTableResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/business/{businessId}/offices/{officeId}/tables")
    @Secured("ROLE_ADMIN")
    public List<TableResponse> getTablesForBusinessAndOffice(@PathVariable Long businessId,
                                                             @PathVariable Long officeId){
        return getAllTablesForOfficeAndBusiness(officeService.findByIdOrThrow(officeId),
                    businessService.findBusinessById(businessId))
                .stream()
                .map(TableService::mapTableToTableResponse)
                .collect(Collectors.toList());
    }

    private List<Table> getAllTablesForOfficeAndBusiness(Office office, Business business){
        officeService.validateBusiness(office, business); //check if ids match
        return tableService.getAllByOfficeId(office.getId());
    }

    @PostMapping("/business/{businessId}/offices/{officeId}/tables")
    @Secured("ROLE_ADMIN")
    public List<TableResponse> addTable(@PathVariable Long officeId,
                                        @PathVariable Long businessId,
                                        @RequestBody TableRequest tableRequest){
        officeService.validateBusiness(officeService.findByIdOrThrow(officeId),
                businessService.findBusinessById(businessId));

       tableService.save(tableService.getFromTableRequestAndOfficeId(tableRequest, officeId)); //will check unique table number

        return getTablesForBusinessAndOffice(businessId, officeId);
    }

    @DeleteMapping("/business/{businessId}/offices/{officeId}/tables/{tableId}")
    @Secured("ROLE_ADMIN")
    public List<TableResponse> deleteTable(@PathVariable Long officeId,
                                @PathVariable Long businessId,
                                @PathVariable Long tableId){
        officeService.validateBusiness(officeService.findByIdOrThrow(officeId),
                businessService.findBusinessById(businessId));

        if(!tableService.isInOffice(tableId, officeId)){
           throw new UnauthorizedException("Table with id " + tableId + " doesn't exist in this office");
        }

        tableService.delete(tableId);

        return getTablesForBusinessAndOffice(businessId, officeId);
    }
}
