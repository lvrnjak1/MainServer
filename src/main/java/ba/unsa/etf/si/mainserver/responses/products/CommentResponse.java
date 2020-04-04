package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Comment;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String text;
    private ProductResponse product;

    public CommentResponse (Comment comment){
        this.id = comment.getId();
        this.firstName = comment.getFirstName();
        this.lastName = comment.getLastName();
        this.email = comment.getEmail();
        this.text = comment.getText();
        this.product = new ProductResponse(comment.getProduct());
    }
}
