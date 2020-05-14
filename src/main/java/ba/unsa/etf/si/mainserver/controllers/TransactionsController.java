package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.transactions.*;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.PaymentMethodRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptItemRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptStatusRepository;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.PayServerInfoRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.PayServerStatusRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.ReceiptFilterRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.ReceiptRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.PayServerInfoResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.ReceiptResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.ReceiptResponseLite;
import ba.unsa.etf.si.mainserver.responses.transactions.ReceiptStatusResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.OfficeInventoryService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import ba.unsa.etf.si.mainserver.services.transactions.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionsController {
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductService productService;
    private final BusinessService businessService;
    private final OfficeService officeService;
    private final ReceiptStatusRepository receiptStatusRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ReceiptService receiptService;
    private final OfficeInventoryService officeInventoryService;
    private final CashRegisterService cashRegisterService;
    private final LogServerService logServerService;


    public TransactionsController(ReceiptRepository receiptRepository,
                                  ReceiptItemRepository receiptItemRepository,
                                  ProductService productService,
                                  BusinessService businessService, OfficeService officeService,
                                  ReceiptStatusRepository receiptStatusRepository,
                                  CashRegisterRepository cashRegisterRepository,
                                  PaymentMethodRepository paymentMethodRepository,
                                  ReceiptService receiptService,
                                  OfficeInventoryService officeInventoryService,
                                  CashRegisterService cashRegisterService,
                                  LogServerService logServerService) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.productService = productService;
        this.businessService = businessService;
        this.officeService = officeService;
        this.receiptStatusRepository = receiptStatusRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.receiptService = receiptService;
        this.officeInventoryService = officeInventoryService;
        this.cashRegisterService = cashRegisterService;
        this.logServerService =logServerService;
    }


    //ruta na koju cash server salje racun
    @PostMapping("/receipts")
    @Secured("ROLE_OFFICEMAN")
    public ResponseEntity<ApiResponse> saveReceipt(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody ReceiptRequest receiptRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findOfficeById(receiptRequest.getOfficeId(), receiptRequest.getBusinessId());
        if(!business.getId().equals(receiptRequest.getBusinessId())){
            throw new UnauthorizedException("Not your business");
        }
        CashRegister cashRegister = cashRegisterService.findCashRegisterById(receiptRequest.getCashRegisterId(), office.getId(),business.getId());

        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, receiptRequest.getStatus())
        );

        if(!receiptStatus.isPresent()){
            throw new ResourceNotFoundException("Invalid status");
        }

        if(receiptStatus.get().getStatusName().toString().equals("DELETED")){
            //u pitanju je storniranje računa
            //samo oznaciti pozitivni pandan sa deleted
            receiptService.markPositiveDeleted(receiptRequest.getReceiptId());
        }

        Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findByMethodName(
                Enum.valueOf(PaymentMethodName.class, receiptRequest.getPaymentMethod())
        );

        if(!paymentMethod.isPresent()){
            throw new ResourceNotFoundException("Invalid payment method");
        }

        Receipt receipt = new Receipt(receiptRequest.getReceiptId(),
                receiptRequest.getCashRegisterId(),
                receiptRequest.getOfficeId(),
                receiptRequest.getBusinessId(),
                receiptRequest.getUsername(),
                receiptRequest.getTotalPrice(),
                receiptStatus.get(),
                paymentMethod.get(),
                new Date(receiptRequest.getTimestamp()),
                receiptRequest.getReceiptItems().stream().map(
                        receiptItemRequest -> {
                            Optional<Product> product = productService.findById(receiptItemRequest.getId());
                            if(!product.isPresent()){
                                throw new ResourceNotFoundException("This product doesn't exist");
                            }
                            ReceiptItem receiptItem =
                                    new ReceiptItem(product.get(), receiptItemRequest.getQuantity());
                            receiptItemRepository.save(receiptItem);
                           return receiptItem;
                        }
                ).collect(Collectors.toSet()));

        //update zaliha samo kad nije pending i canceled i insuff
        if(receiptStatus.get().getStatusName().toString().equals("DELETED") ||
                receiptStatus.get().getStatusName().toString().equals("PAID") ) {
            officeInventoryService.processTransaction(office.getId(), receipt.getReceiptItems());
        }
        receiptRepository.save(receipt);
        return ResponseEntity.ok(new ApiResponse("Receipt successfully sent", 200));
    }

    //ruta na koju cash server polla status pojedinog racuna
    @GetMapping("/receipts/{receiptId}")
    @Secured("ROLE_OFFICEMAN")
    public ReceiptStatusResponse getReceiptStatus(@CurrentUser UserPrincipal userPrincipal,
                                                                  @PathVariable String receiptId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Optional<Receipt> optionalReceipt = receiptRepository.findByReceiptId(receiptId);
        if(!optionalReceipt.isPresent()){
            throw new ResourceNotFoundException("Receipt doesn't exist");
        }
        if(!business.getId().equals(optionalReceipt.get().getBusinessId())){
            throw new UnauthorizedException("Not your business");
        }

        return new ReceiptStatusResponse(optionalReceipt.get().getStatus(),
                optionalReceipt.get().getReceiptId());
    }

    //ruta na koju pay server trazi info o racunu sa neke kase office i businessa
    //secured
    @PostMapping("/receipts/info")
    public PayServerInfoResponse getReceiptInfo(@RequestBody PayServerInfoRequest payServerInfoRequest){
        Business business = businessService.findByName(payServerInfoRequest.getBusinessName());

        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "PENDING")
        );

        if(!receiptStatus.isPresent()){
            throw new AppException("Invalid status");
        }

        Optional<Receipt> receiptOptional = receiptRepository.findByBusinessIdAndCashRegisterIdAndOfficeIdAndStatus_StatusName(
                business.getId(),
                payServerInfoRequest.getCashRegisterId(),
                payServerInfoRequest.getOfficeId(),
                receiptStatus.get().getStatusName());

        if(!receiptOptional.isPresent()){
            throw new AppException("Receipt doesn't exist");
        }

        if(!receiptOptional.get().getStatus().getStatusName().toString().equals("PENDING")){
            throw new BadParameterValueException("This receipt is not pending payment");
        }

        return new PayServerInfoResponse(receiptOptional.get());
    }

    //ruta na koju pay server updatuje status o racunu
    @PutMapping("/receipts/{receiptId}")
    public ResponseEntity<ApiResponse> updateReceiptStatus(@PathVariable String receiptId,
                                                           @RequestBody PayServerStatusRequest receiptStatusRequest){
        Optional<Receipt> receiptOptional = receiptRepository.findByReceiptId(receiptId);
        if(!receiptOptional.isPresent()){
            throw new BadParameterValueException("Receipt doesn't exist");
        }

        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, receiptStatusRequest.getStatus())
        );

        if(!receiptStatus.isPresent()){
            throw new ResourceNotFoundException("Invalid status");
        }

        receiptOptional.get().setStatus(receiptStatus.get());
        receiptRepository.save(receiptOptional.get());

        if(receiptStatus.get().getStatusName().toString().equals("PAID")){
            officeInventoryService.processTransaction(receiptOptional.get().getOfficeId(),
                    receiptOptional.get().getReceiptItems());
        }

        //broadcast notifikacije koju cash server osluskuje
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "Pay Server",
                                "receipt_status_update",
                                String.format("{\"receiptId\":\"%s\", \"status\":\"%s\"}", receiptOptional.get().getReceiptId(),
                                        receiptStatus.get().getStatusName().toString())
                        )
                )
                ,
                "cash_server"
        );


        return ResponseEntity.ok(new ApiResponse("Status successfully changed", 200));
    }

    @GetMapping("/offices/{officeId}/products/{productId}/receipts")
    @Secured("ROLE_MERCHANT")
    public List<ReceiptResponseLite> getAllReceiptsByProduct(@PathVariable Long productId,
                                                             @PathVariable Long officeId,
                                                             @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(officeId);
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        Optional<Product> productOptional = productService.findById(productId);
        if(!productOptional.isPresent()){
            throw new ResourceNotFoundException("This product doesn't exist");
        }

        Optional<OfficeInventory> officeInventoryOptional = officeInventoryService.findByProductAndOffice(
                productOptional.get(), officeOptional.get()
        );

        if(!officeInventoryOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't stock this product");
        }

       return receiptService.findAllByProductAndOffice(productId, officeId)
               .stream()
               .map(receipt -> {
                   ReceiptItem receiptItem = receipt.getReceiptItems()
                           .stream()
                           .filter(receiptItem1 -> receiptItem1.getProductId().equals(productId))
                           .findAny()
                           .orElse(null);
                   return new ReceiptResponseLite(
                           receipt.getReceiptId(),
                           receipt.getCashRegisterId(),
                           receipt.getTimestamp().getTime(),
                           receipt.getUsername(),
                           receiptItem.getPrice(),
                          receipt.getTotalPrice(),
                          receiptItem.getQuantity()
                   );
               })
               .collect(Collectors.toList());
    }

    @PostMapping("/receipts/filtered")
    @Secured("ROLE_MERCHANT")
    public List<ReceiptResponse> getAllReceiptsFiltered(@RequestBody ReceiptFilterRequest receiptFilterRequest,
                                                        @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return receiptService.findAllFilteredByDate(receiptFilterRequest.getFrom(), receiptFilterRequest.getTo(),
                business.getId())
                .stream()
                .map(ReceiptResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/business/{businessId}/transactions")
    @Secured("ROLE_ADMIN")
    public List<ReceiptResponse> getAllTransactionsForBusiness(@PathVariable Long businessId){
        Business business = businessService.findBusinessById(businessId);
        return receiptService.findAllByBusinessId(businessId)
                .stream()
                .filter(receipt -> receipt.getStatus().getStatusName().toString().equals("PAID"))
                .map(ReceiptResponse::new)
                .collect(Collectors.toList());
    }
}
