package ba.unsa.etf.si.mainserver;

import ba.unsa.etf.si.mainserver.models.PDV;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.Table;
import ba.unsa.etf.si.mainserver.models.pr.Answer;
import ba.unsa.etf.si.mainserver.models.pr.Question;
import ba.unsa.etf.si.mainserver.models.pr.QuestionAuthor;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatusName;
import ba.unsa.etf.si.mainserver.repositories.PDVRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.repositories.business.TableRepository;
import ba.unsa.etf.si.mainserver.repositories.pr.QuestionRepository;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.business.TableService;
import ba.unsa.etf.si.mainserver.services.pr.QuestionService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
class MainServerApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private PDVRepository pdvRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private OfficeService officeService;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private OfficeRepository officeRepository;

    @Test
    public void initDataTest() {
        Assert.assertEquals("root", userService.findUserById(1L).getUsername());
        Assert.assertEquals("ROLE_ADMIN", userService.findUserById(1L).getRoles().iterator().next().getName().name());

        List<Double> pdvData = pdvRepository.findAll()
                .stream()
                .map(PDV::getPdvRate)
                .collect(Collectors.toList());
        boolean rightPDVData =  pdvData.contains(17.0) && pdvData.size() == 2 && pdvData.contains(0.0);
        Assert.assertTrue(rightPDVData);

        List<String> receipStatuses = Stream.of(ReceiptStatusName.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        Assert.assertTrue(receipStatuses.contains("CANCELED"));
        Assert.assertTrue(receipStatuses.contains("PAID"));
        Assert.assertTrue(receipStatuses.contains("INSUFFICIENT_FUNDS"));
        Assert.assertTrue(receipStatuses.contains("PENDING"));
        Assert.assertTrue(receipStatuses.contains("DELETED"));

    }

    @Test
    public void addingAnswerToQuestionTest(){
        Question question = new Question(1L,"Prvo pitanje?", new Date(), new Date(), new QuestionAuthor(), null);
        Mockito.when(questionRepository.findById(1L)).thenReturn(java.util.Optional.of(question));
        questionService.saveAnswer(1L, new Answer("Tacan odgovor"));
        Assert.assertNotNull(question.getAnswer());
        Assert.assertEquals(question.getAnswer().getText(), "Tacan odgovor");
    }

    @Test
    public void changingBusinessWorkingHoursTest(){
        Office office = new Office();
        office.setId(1L);
        Mockito.when(officeRepository.findById(1L)).thenReturn(java.util.Optional.of(office));
        try {
            Date date1 = new SimpleDateFormat("HH:mm").parse("07:00");
            Date date2 = new SimpleDateFormat("HH:mm").parse("15:00");
            officeService.changeWorkHours(office, date1, date2);
            Assert.assertEquals(officeService.findById(1L).get().getStringStart(), "07:00");
            Assert.assertEquals(officeService.findById(1L).get().getStringEnd(), "15:00");

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
