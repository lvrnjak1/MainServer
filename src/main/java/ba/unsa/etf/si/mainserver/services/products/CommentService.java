package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.products.Comment;
import ba.unsa.etf.si.mainserver.repositories.products.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment save(Comment comment) { return commentRepository.save(comment); }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> findByProductId(Long productId) { return commentRepository.findByProductId(productId); }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
}
