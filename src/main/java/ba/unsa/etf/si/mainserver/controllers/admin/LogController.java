package ba.unsa.etf.si.mainserver.controllers.admin;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.responses.admin.logs.ActionCollectionResponse;
import ba.unsa.etf.si.mainserver.responses.admin.logs.LogCollectionResponse;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class LogController {
    private final LogServerService logServerService;

    public LogController(LogServerService logServerService) {
        this.logServerService = logServerService;
    }

    @GetMapping("/logs")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<LogCollectionResponse> getLogs(
            @RequestParam(name = "username",required = false) String username,
            @RequestParam(name = "from",required = false) Long from,
            @RequestParam(name = "to",required = false) Long to,
            @RequestParam(name = "action",required = false) String action,
            @RequestParam(name = "object",required = false) String object
    ) {
        return ResponseEntity.ok(logServerService.getLogsFromServer(username,from,to,action,object));
    }

    @GetMapping("/logs/actions")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ActionCollectionResponse> getActions() {
        return ResponseEntity.ok(new ActionCollectionResponse(Arrays.asList(
                Actions.LOGIN_ACTION_NAME,
                Actions.PASSWORD_CHANGE_ACTION_NAME,
                Actions.ADMIN_PASSWORD_CHANGE_ACTION_NAME,
                Actions.ADMIN_REGISTER_USER_ACTION_NAME,
                Actions.REGISTER_USER_ACTION_NAME,
                Actions.ADMIN_CREATE_BUSINESS_ACTION_NAME,
                Actions.ADMIN_TOGGLE_RESTAURANT_ACTION_NAME,
                Actions.ADMIN_CREATE_OFFICE_ACTION_NAME,
                Actions.ADMIN_DELETE_OFFICE_ACTION_NAME,
                Actions.ADMIN_CREATE_CASH_REGISTER_ACTION_NAME,
                Actions.ADMIN_EDIT_CASH_REGISTER_ACTION_NAME,
                Actions.ADMIN_DELETE_CASH_REGISTER_ACTION_NAME,
                Actions.SET_MANAGER_ACTION_NAME,
                Actions.ASSIGN_EMPLOYEE_FOR_OFFICE_ACTION_NAME,
                Actions.UNASSIGN_EMPLOYEE_FOR_OFFICE_ACTION_NAME,
                Actions.SET_MAIN_OFFICE_ACTION_NAME,
                Actions.FIRE_EMPLOYEE_ACTION_NAME,
                Actions.ADD_NOTIFICATION_ACTION_NAME,
                Actions.MARK_NOTIFICATION_ACTION_NAME,
                Actions.DELETE_NOTIFICATION_ACTION_NAME,
                Actions.NOTIFY_ADMIN_OPEN_OFFICE_ACTION_NAME,
                Actions.NOTIFY_ADMIN_CLOSE_OFFICE_ACTION_NAME,
                Actions.ADMIN_READ_NOTIFICATION_ACTION_NAME,
                Actions.RESET_PASSWORD_ACTION_NAME,
                Actions.SAVE_PASSWORD_ACTION_NAME,
                Actions.CREATE_PRODUCT_ACTION_NAME,
                Actions.UPLOAD_IMAGE_ACTION_NAME,
                Actions.UPDATE_PRODUCT_ACTION_NAME,
                Actions.DELETE_PRODUCT_ACTION_NAME,
                Actions.PRODUCTS_TO_OFFICE_ACTION_NAME,
                Actions.ADD_COMMENT_ON_PRODUCT_ACTION_NAME,
                Actions.DELETE_COMMENT_ON_PRODUCT_ACTION_NAME,
                Actions.UPDATE_DISCOUNT_ACTION_NAME,
                Actions.SAVE_QUESTION_ACTION_NAME,
                Actions.DELETE_QUESTION_ACTION_NAME,
                Actions.SAVE_ANSWER_ACTION_NAME,
                Actions.DELETE_ANSWER_ACTION_NAME,
                Actions.SAVE_RECEIPT_ACTION_NAME,
                Actions.UPDATE_RECEIPT_STATUS_ACTION_NAME,
                Actions.UPDATE_USER_PROFILE_ACTION_NAME,
                Actions.UPDATE_USER_ROLES_ACTION_NAME,
                Actions.REGISTER_INCOMING_PRODUCTS_ACTION_NAME
        )));
    }
}
