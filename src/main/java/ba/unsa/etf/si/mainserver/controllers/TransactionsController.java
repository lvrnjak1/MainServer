package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.transactions.*;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.PaymentMethodRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptItemRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptStatusRepository;
import ba.unsa.etf.si.mainserver.requests.transactions.PayServerInfoRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.PayServerStatusRequest;
import ba.unsa.etf.si.mainserver.requests.transactions.ReceiptRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.PayServerInfoResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.ReceiptStatusResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.OfficeInventoryService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import ba.unsa.etf.si.mainserver.services.transactions.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receipts")
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

    public TransactionsController(ReceiptRepository receiptRepository,
                                  ReceiptItemRepository receiptItemRepository,
                                  ProductService productService,
                                  BusinessService businessService, OfficeService officeService,
                                  ReceiptStatusRepository receiptStatusRepository,
                                  CashRegisterRepository cashRegisterRepository,
                                  PaymentMethodRepository paymentMethodRepository,
                                  ReceiptService receiptService,
                                  OfficeInventoryService officeInventoryService) {
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
    }


    //ruta na koju cash server salje racun
    @PostMapping
    @Secured("ROLE_OFFICEMAN")
    public ResponseEntity<ApiResponse> saveReceipt(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody ReceiptRequest receiptRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(receiptRequest.getOfficeId());
        if(!business.getId().equals(receiptRequest.getBusinessId())){
            throw new UnauthorizedException("Not your business");
        }

        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        if(!officeOptional.get().getBusiness().getId().equals(business.getId())){
            throw new UnauthorizedException("Not your office");
        }

        Optional<CashRegister> optionalCashRegister =
                cashRegisterRepository.findById(receiptRequest.getCashRegisterId());

        if(!optionalCashRegister.isPresent()){
            throw new ResourceNotFoundException("Cash register doesn't exist");
        }

        if(!optionalCashRegister.get().getOffice().getId().equals(officeOptional.get().getId())){
            throw new UnauthorizedException("Not your cash register");
        }

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
            officeInventoryService.processTransaction(officeOptional.get().getId(), receipt.getReceiptItems());
        }
        receiptRepository.save(receipt);
        return ResponseEntity.ok(new ApiResponse("Receipt successfully sent", 200));
    }

    //ruta na koju cash server polla status pojedinog racuna
    @GetMapping("/{receiptId}")
    @Secured("ROLE_OFFICEMAN")
    public ReceiptStatusResponse getReceiptStatus(@CurrentUser UserPrincipal userPrincipal,
                                                                  @PathVariable String receiptId){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
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
    @PostMapping("/info")
    public PayServerInfoResponse getReceiptInfo(@RequestBody PayServerInfoRequest payServerInfoRequest){
        Optional<Business> businessOptional = businessService.findByName(payServerInfoRequest.getBusinessName());

        if(!businessOptional.isPresent()){
            throw new BadParameterValueException("Business doesn't exist");
        }

        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "PENDING")
        );

        if(!receiptStatus.isPresent()){
            throw new AppException("Invalid status");
        }

        Optional<Receipt> receiptOptional = receiptRepository.findByBusinessIdAndCashRegisterIdAndOfficeIdAndStatus_StatusName(
                businessOptional.get().getId(),
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
    @PutMapping("/{receiptId}")
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
        return ResponseEntity.ok(new ApiResponse("Status successfully changed", 200));
    }

}
