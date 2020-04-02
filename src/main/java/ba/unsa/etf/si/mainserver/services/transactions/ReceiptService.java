package ba.unsa.etf.si.mainserver.services.transactions;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatus;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatusName;
import ba.unsa.etf.si.mainserver.repositories.business.PaymentMethodRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptItemRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ReceiptStatusRepository receiptStatusRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public ReceiptService(ReceiptRepository receiptRepository,
                          ReceiptItemRepository receiptItemRepository,
                          ReceiptStatusRepository receiptStatusRepository,
                          PaymentMethodRepository paymentMethodRepository) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.receiptStatusRepository = receiptStatusRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public void markPositiveDeleted(String receiptId) {
        receiptId = receiptId.substring(1, receiptId.length()); //obrisi minus
        Optional<Receipt> optionalReceipt = receiptRepository.findByReceiptId(receiptId);
        if(!optionalReceipt.isPresent()){
            throw new AppException("This receipt can't be deleted because it doesn't exist");
        }

        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "DELETED")
        );

        if(!receiptStatus.isPresent()){
            throw new ResourceNotFoundException("Invalid status");
        }

        optionalReceipt.get().setStatus(receiptStatus.get());
        receiptRepository.save(optionalReceipt.get());
    }
}
