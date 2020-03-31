package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptItemRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptRepository;
import ba.unsa.etf.si.mainserver.requests.transactions.ReceiptRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.PayServerRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.PayServerResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
public class TransactionsController {
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductService productService;
    private final BusinessService businessService;

    public TransactionsController(ReceiptRepository receiptRepository,
                                  ReceiptItemRepository receiptItemRepository,
                                  ProductService productService,
                                  BusinessService businessService) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.productService = productService;
        this.businessService = businessService;
    }


    //ruta na koju cash server salje racun
    @PostMapping
    @Secured("ROLE_OFFICEMAN")
    public ResponseEntity<ApiResponse> saveReceipt(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody ReceiptRequest receiptRequest){
        return null;
    }

    //ruta na koju cash server polla status pojedinog racuna
    @GetMapping("/{receiptId}")
    @Secured("ROLE_OFFICEMAN")
    public ResponseEntity<String> getReceiptStatus(@CurrentUser UserPrincipal userPrincipal,
                                                   @PathVariable String receiptId){
        return null;
    }

    //ruta na koju pay server trazi info o racunu sa neke kase office i businessa
    //TODO kreirati ove response i request tako da odgovara pay serveru i meni
    @PostMapping
    public PayServerResponse getReceiptInfo(@RequestBody PayServerRequest payServerRequest){
        return null;
    }

    //ruta na koju pay server updatuje status o racunu
    @PostMapping("/{receiptId}")
    public ResponseEntity<ApiResponse> updateReceiptStatus(@PathVariable String receiptId){
        return null;
    }

}
