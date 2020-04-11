package ba.unsa.etf.si.mainserver.services.transactions;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptItem;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatus;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatusName;
import ba.unsa.etf.si.mainserver.repositories.business.PaymentMethodRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptItemRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptRepository;
import ba.unsa.etf.si.mainserver.repositories.transactions.ReceiptStatusRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public BigDecimal findDailyProfitForCashRegister(CashRegister cashRegister, Date date) {
        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "PAID")
        );

        if(!receiptStatus.isPresent()){
            throw new AppException("Invalid status");
        }

        return receiptRepository.findAllByCashRegisterIdAndStatus_StatusName(cashRegister.getId(),
                receiptStatus.get().getStatusName())
                .stream()
                .filter(receipt -> {
                    //da bude u toku nekog dana
                    Date receiptDate = receipt.getTimestamp();
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
                    return dateFormat.format(receiptDate).equals(dateFormat.format(date));
                })
                .map(Receipt::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal findTotalProfitForCashRegister(CashRegister cashRegister) {
        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "PAID")
        );

        if(!receiptStatus.isPresent()){
            throw new AppException("Invalid status");
        }

        return receiptRepository.findAllByCashRegisterIdAndStatus_StatusName(cashRegister.getId(),
                receiptStatus.get().getStatusName())
                .stream()
                .map(Receipt::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Receipt> findAllByUsername(String username) {
        Optional<ReceiptStatus> receiptStatus = receiptStatusRepository.findByStatusName(
                Enum.valueOf(ReceiptStatusName.class, "PAID")
        );

        if(!receiptStatus.isPresent()){
            throw new AppException("Invalid status");
        }

        return receiptRepository.findAllByUsernameAndStatus_StatusName(username, receiptStatus.get().getStatusName());
    }

    public List<Receipt> findAllByProductAndOffice(Long productId, Long officeId) {
        return receiptItemRepository.findAllByProductId(productId)
        .stream()
        .map(ReceiptItem::getReceipt)
        .filter(receipt -> receipt.getOfficeId().equals(officeId) &&
                receipt.getStatus().getStatusName().toString().equals("PAID"))
        .collect(Collectors.toList());
    }

    public List<Receipt> findAllFilteredByDate(String from, String to, Long businessId) {
        Date dateFrom, dateTo;
        try {
            dateFrom = new SimpleDateFormat("dd.MM.yyyy").parse(from);
            dateTo = new SimpleDateFormat("dd.MM.yyyy").parse(to);
        } catch (ParseException e) {
            throw new AppException("Bad date format");
        }

        return receiptRepository.findAllByBusinessId(businessId)
                .stream()
                .filter(receipt -> isBetween(dateFrom, dateTo, receipt.getTimestamp())
                        && receipt.getStatus().getStatusName().toString().equals("PAID"))
                .collect(Collectors.toList());
    }

    private static boolean isBetween(Date dateFrom, Date dateTo, Date date) {
        date = getZeroTimeDate(date);
        dateFrom = getZeroTimeDate(dateFrom);
        dateTo = getZeroTimeDate(dateTo);
        return (date.after(dateFrom) || date.equals(dateFrom))
                && (date.before(dateTo) || date.equals(dateTo));
    }

    private static Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    public List<Receipt> findAllByBusinessId(Long businessId) {
        return receiptRepository.findAllByBusinessId(businessId);
    }
}
