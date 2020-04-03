package ba.unsa.etf.si.mainserver.requests.pr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    @NotBlank
    private String text;
}
