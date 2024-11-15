package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Repository.CommentRepository;
import com.example.admin_backend.Repository.PostRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public List<CommentEntity> getAllComments() {
    return commentRepository.findAll();
}


   public CommentEntity addComment(CommentEntity comment) {
    comment.setTimestamp(LocalDateTime.now());

   if (comment.getAdminId() == null && comment.getSuperUserId() == null) {
    throw new IllegalArgumentException("Either Admin or SuperUser must be set");
}


    return commentRepository.save(comment);
}



  public boolean softDeleteComment(int commentId, int adminOrSuperUserId) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
        
        if (commentOpt.isPresent()) {
            CommentEntity comment = commentOpt.get();
            
            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
            if (postOpt.isPresent()) {
                PostEntity post = postOpt.get();
                
                // Check if the admin or superuser is the comment owner or the post owner
                if ((comment.getAdminId() != null && comment.getAdminId().equals(adminOrSuperUserId)) ||
                    (comment.getSuperUserId() != null && comment.getSuperUserId().equals(adminOrSuperUserId)) ||
                    post.getAdminId() == adminOrSuperUserId) {
                    comment.setDeleted(true);
                    commentRepository.save(comment);
                    return true;
            }
        }
    }
    
    return false;
}

public boolean updateCommentVisibility(int commentId, boolean visible) {
    Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
    if (commentOpt.isPresent()) {
        CommentEntity comment = commentOpt.get();
        comment.setVisible(visible);  // Set the visible field
        commentRepository.save(comment);  // Save the updated entity
        return true;
    }
    return false;
}



}
