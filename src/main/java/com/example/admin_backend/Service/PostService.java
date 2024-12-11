package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.*;
import com.example.admin_backend.Repository.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperUserRepository superUserRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
<<<<<<< HEAD
    @SuppressWarnings("unused")
=======
    private ReportRepository reportRepository;

    @Autowired
>>>>>>> Meow
    private LeaderboardService leaderboardService;

    // Get all posts
    public List<PostEntity> getAllPosts() {
        return postRepository.findByIsDeletedFalseOrderByTimestampDesc();
    }

    // Get only visible posts
    public List<PostEntity> getAllVisiblePosts() {
        return postRepository.findByIsDeletedFalseAndVisibleTrue();
    }

    // Get post by ID
<<<<<<< HEAD
    public Optional<PostEntity> getPostById(int postId) {
        return postRepository.findByPostIdAndIsDeletedFalse(postId);
=======
   // In PostService.java
public PostEntity getPostById(int postId) {
    try {
        System.out.println("Fetching post with ID: " + postId);
        return postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
    } catch (Exception e) {
        System.err.println("Error fetching post by ID: " + e.getMessage());
        throw new RuntimeException("Error fetching post", e);
>>>>>>> Meow
    }
}

    // Create new post
<<<<<<< HEAD
    @Transactional
    public PostEntity createPost(PostEntity post) {
        if (post.getContent() == null && post.getImage() == null) {
            throw new IllegalArgumentException("Post must have either content or an image");
        }

        post.setTimestamp(LocalDateTime.now());
        post.setDeleted(false);
        post.setLikedBy(new HashSet<>());
        post.setDislikedBy(new HashSet<>());
        
        switch (post.getUserRole().toUpperCase()) {
            case "USER":
                return createUserPost(post);
            case "ADMIN":
                return createAdminPost(post);
            case "SUPERUSER":
                return createSuperUserPost(post);
            default:
                throw new IllegalArgumentException("Invalid user role");
=======
   @Transactional
public PostEntity createPost(PostEntity post) {
    try {
        System.out.println("Creating new post");
        if (post.getContent() == null && post.getImage() == null) {
            throw new IllegalArgumentException("Post must have either content or an image");
>>>>>>> Meow
        }

        post.setTimestamp(LocalDateTime.now());
        post.setDeleted(false);
post.setLikedBy(new HashSet<String>());  // Initialize empty Set<String>
post.setDislikedBy(new HashSet<String>()); // Initialize empty Set<String>        post.setLikes(0);
        post.setDislikes(0);

        if (Boolean.TRUE.equals(post.getIsSubmittedReport())) {
            post.setStatus(ReportStatus.PENDING.toString());
        } else {
            post.setStatus(null);
        }

        PostEntity savedPost;
        switch (post.getUserRole().toUpperCase()) {
            case "USER":
                savedPost = createUserPost(post);
                break;
            case "ADMIN":
                savedPost = createAdminPost(post);
                break;
            case "SUPERUSER":
                savedPost = createSuperUserPost(post);
                break;
            default:
                throw new IllegalArgumentException("Invalid user role");
        }
        System.out.println("Post created successfully with ID: " + savedPost.getPostId());
        return savedPost;
    } catch (Exception e) {
        System.err.println("Error creating post: " + e.getMessage());
        throw e;
    }
}

    private PostEntity createUserPost(PostEntity post) {
        if (post.getUserId() == 0) {
            throw new IllegalArgumentException("User ID must be provided");
        }

        UserEntity user = userRepository.findById(post.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

<<<<<<< HEAD
       // Ensure user points are not null and handle it
       user.setPoints(Math.max(user.getPoints(), 0)); // Reset points to default if less than 0
    
       post.setFullName(user.getFullName());
       post.setProfile(profileRepository.findByUser(user)); // Fetch profile
       post.setTimestamp(LocalDateTime.now());
       post.setIsSubmittedReport(post.getIsSubmittedReport()); 
       post.setDeleted(false);
=======
        user.setPoints(Math.max(user.getPoints(), 0));
        
        post.setFullName(user.getFullName());
        post.setIdnumber(user.getIdNumber());
        post.setProfile(profileRepository.findByUser(user));
        post.setTimestamp(LocalDateTime.now());
        post.setIsSubmittedReport(post.getIsSubmittedReport()); 
        post.setDeleted(false);
>>>>>>> Meow
        
        return postRepository.save(post);
    }

    private PostEntity createAdminPost(PostEntity post) {
        AdminEntity admin = adminRepository.findById(post.getAdminId())
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        post.setFullName(admin.getFullName());
        post.setIdnumber(admin.getIdNumber());
        post.setProfile(profileRepository.findByAdmin(admin));
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    private PostEntity createSuperUserPost(PostEntity post) {
        SuperUserEntity superuser = superUserRepository.findById(post.getSuperUserId())
            .orElseThrow(() -> new RuntimeException("Superuser not found"));

        post.setFullName(superuser.getFullName());
        post.setIdnumber(superuser.getIdNumber());
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    // Update post
    @Transactional
    public PostEntity updatePost(int postId, PostEntity postDetails) {
        PostEntity existingPost = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        // Update basic fields
        if (postDetails.getContent() != null) {
            existingPost.setContent(postDetails.getContent());
        }
        if (postDetails.getImage() != null) {
            existingPost.setImage(postDetails.getImage());
        }

        existingPost.setLastModifiedAt(LocalDateTime.now());

        // Update role-specific fields
        switch (existingPost.getUserRole().toUpperCase()) {
            case "USER":
                updateUserPost(existingPost, postDetails);
                break;
            case "ADMIN":
                updateAdminPost(existingPost, postDetails);
                break;
            case "SUPERUSER":
                updateSuperUserPost(existingPost, postDetails);
                break;
        }

        return postRepository.save(existingPost);
    }

    private void updateUserPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVisible(details.isVisible());
    }

    private void updateAdminPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVerified(true);
        existingPost.setVisible(details.isVisible());
        if (details.getAdminNotes() != null) {
            existingPost.setAdminNotes(details.getAdminNotes());
        }
    }

    private void updateSuperUserPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVerified(true);
        existingPost.setVisible(details.isVisible());
        if (details.getAdminNotes() != null) {
            existingPost.setAdminNotes(details.getAdminNotes());
        }
    }

<<<<<<< HEAD
    // Like/Dislike functionality
    @Transactional
    public PostEntity toggleLike(int postId, int userId, String userRole) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAdminOrSuperuser = "ADMIN".equalsIgnoreCase(userRole) || 
                                   "SUPERUSER".equalsIgnoreCase(userRole);

        if (post.getLikedBy() == null) {
            post.setLikedBy(new HashSet<>());
        }
        if (post.getDislikedBy() == null) {
            post.setDislikedBy(new HashSet<>());
        }

        boolean isOwnPost = userId == post.getUserId();
        boolean hasLiked = post.getLikedBy().contains(userId);
        boolean hasDisliked = post.getDislikedBy().contains(userId);

        if (hasLiked) {
            // Remove like
            post.getLikedBy().remove(userId);
            post.setLikes(post.getLikes() - 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), -1);
            }
        } else {
            // Handle dislike if exists
            if (hasDisliked) {
                post.getDislikedBy().remove(userId);
                post.setDislikes(post.getDislikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), 1);
                }
            }
            // Add like
            post.getLikedBy().add(userId);
            post.setLikes(post.getLikes() + 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), 1);
            }
        }

        return postRepository.save(post);
    }

    @Transactional
    public PostEntity toggleDislike(int postId, int userId, String userRole) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAdminOrSuperuser = "ADMIN".equalsIgnoreCase(userRole) || 
                                   "SUPERUSER".equalsIgnoreCase(userRole);

        if (post.getLikedBy() == null) {
            post.setLikedBy(new HashSet<>());
        }
        if (post.getDislikedBy() == null) {
            post.setDislikedBy(new HashSet<>());
        }

        boolean isOwnPost = userId == post.getUserId();
        boolean hasLiked = post.getLikedBy().contains(userId);
        boolean hasDisliked = post.getDislikedBy().contains(userId);

        if (hasDisliked) {
            // Remove dislike
            post.getDislikedBy().remove(userId);
            post.setDislikes(post.getDislikes() - 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), 1);
            }
        } else {
            // Handle like if exists
            if (hasLiked) {
                post.getLikedBy().remove(userId);
                post.setLikes(post.getLikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), -1);
                }
            }
            // Add dislike
            post.getDislikedBy().add(userId);
            post.setDislikes(post.getDislikes() + 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), -1);
            }

            // Check for auto-deletion threshold for user posts
            if ("USER".equalsIgnoreCase(post.getUserRole()) && post.getDislikes() >= 50) {
                softDeletePost(postId);
                return post;
            }
        }

        return postRepository.save(post);
    }

    // Post visibility
=======
    // Like/Dislike handling
  @Transactional
public PostEntity handleLike(Integer postId, Integer userId, String userRole) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new NoSuchElementException("Post not found"));

    String userIdentifier = userId + "_" + userRole.toUpperCase();

    // Check if this is the post owner's reaction on their own report post
    boolean isOwnerReaction = Boolean.TRUE.equals(post.getIsSubmittedReport()) && 
                            post.getUserId() != null && 
                            post.getUserId().equals(userId) && 
                            "USER".equalsIgnoreCase(userRole);

    if (post.getLikedBy().contains(userIdentifier)) {
        // Remove like
        post.getLikedBy().remove(userIdentifier);
        post.setLikes(post.getLikes() - 1);
        
        // Only deduct points if it's not the owner's reaction on their report post
        if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
            handlePointsDeduction(post.getUserId(), userRole);
        }
    } else {
        // Remove dislike if exists
        if (post.getDislikedBy().contains(userIdentifier)) {
            post.getDislikedBy().remove(userIdentifier);
            post.setDislikes(post.getDislikes() - 1);
            if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
                handlePointsAddition(post.getUserId(), userRole);
            }
        }
        
        // Add like
        post.getLikedBy().add(userIdentifier);
        post.setLikes(post.getLikes() + 1);
        
        // Only add points if it's not the owner's reaction on their report post
        if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
            handlePointsAddition(post.getUserId(), userRole);
        }
    }

    return postRepository.save(post);
}

@Transactional
public PostEntity handleDislike(Integer postId, Integer userId, String userRole) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new NoSuchElementException("Post not found"));

    String userIdentifier = userId + "_" + userRole.toUpperCase();

    // Check if this is the post owner's reaction on their own report post
    boolean isOwnerReaction = Boolean.TRUE.equals(post.getIsSubmittedReport()) && 
                            post.getUserId() != null && 
                            post.getUserId().equals(userId) && 
                            "USER".equalsIgnoreCase(userRole);

    if (post.getDislikedBy().contains(userIdentifier)) {
        // Remove dislike
        post.getDislikedBy().remove(userIdentifier);
        post.setDislikes(post.getDislikes() - 1);
        
        // Only add points if it's not the owner's reaction on their report post
        if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
            handlePointsAddition(post.getUserId(), userRole);
        }
    } else {
        // Remove like if exists
        if (post.getLikedBy().contains(userIdentifier)) {
            post.getLikedBy().remove(userIdentifier);
            post.setLikes(post.getLikes() - 1);
            if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
                handlePointsDeduction(post.getUserId(), userRole);
            }
        }
        
        // Add dislike
        post.getDislikedBy().add(userIdentifier);
        post.setDislikes(post.getDislikes() + 1);
        
        // Only deduct points if it's not the owner's reaction on their report post
        if (Boolean.TRUE.equals(post.getIsSubmittedReport()) && !isOwnerReaction) {
            handlePointsDeduction(post.getUserId(), userRole);
        }
    }

    return postRepository.save(post);
}


    private void handlePointsAddition(Integer userId, String userRole) {
        int points = getPointsByRole(userRole);
        if (points > 0) {
            leaderboardService.addPoints(userId, points);
        }
    }

    private void handlePointsDeduction(Integer userId, String userRole) {
        int points = getPointsByRole(userRole);
        if (points > 0) {
            leaderboardService.subtractPoints(userId, points);
        }
    }

    private int getPointsByRole(String userRole) {
        switch (userRole.toUpperCase()) {
            case "SUPERUSER": return 5;
            case "ADMIN": return 3;
            case "USER": return 1;
            default: return 0;
        }
    }

    // Visibility and Delete operations
>>>>>>> Meow
    public PostEntity updateVisibility(int postId, boolean newVisibility) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setVisible(newVisibility);
        post.setLastModifiedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public void softDeletePost(int postId) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeleted(true);
        post.setLastModifiedAt(LocalDateTime.now());
        postRepository.save(post);
    }

<<<<<<< HEAD
    // Comments
    public List<CommentEntity> getCommentsByPostId(int postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    public CommentEntity addComment(CommentEntity comment, int postId) {
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }
    
    // Helper methods
    private void updateUserPoints(int userId, int pointChange) {
        if ("USER".equalsIgnoreCase(userRepository.findById(userId)
                .map(UserEntity::getRole)  // Fixed: using getRole() instead of getUserRole()
                .orElse(null))) {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPoints(Math.max(0, user.getPoints() + pointChange));
            userRepository.save(user);
        }
    }

    private boolean isValidUserRole(String userRole) {
        return userRole != null && 
               (userRole.equalsIgnoreCase("USER") || 
                userRole.equalsIgnoreCase("ADMIN") || 
                userRole.equalsIgnoreCase("SUPERUSER"));
=======
    // Comment operations
   public List<CommentEntity> getCommentsByPostId(int postId) {
    try {
        System.out.println("Fetching comments for post ID: " + postId);
        // Only get non-deleted comments
        List<CommentEntity> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
        System.out.println("Found " + comments.size() + " active comments");
        
        // Sort comments by timestamp, newest first
        comments.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        return comments;
    } catch (Exception e) {
        System.err.println("Error fetching comments: " + e.getMessage());
        throw e;
    }
}

   public CommentEntity addComment(CommentEntity comment, int postId) {
    try {
        System.out.println("Adding comment to post ID: " + postId);
        
        // Set post ID
        comment.setPostId(postId);
        
        // Set timestamp
        comment.setTimestamp(LocalDateTime.now());
        
        // If it's an admin comment, fetch and set admin details
        if (comment.getAdminId() != null) {
            AdminEntity admin = adminRepository.findById(comment.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
            comment.setFullName(admin.getFullName());
            comment.setIdNumber(admin.getIdNumber());
        }
        // If it's a superuser comment, fetch and set superuser details
        else if (comment.getSuperUserId() != null) {
            SuperUserEntity superUser = superUserRepository.findById(comment.getSuperUserId())
                .orElseThrow(() -> new RuntimeException("SuperUser not found"));
            comment.setFullName(superUser.getFullName());
            comment.setIdNumber(superUser.getIdNumber());
        }
        // If it's a user comment, fetch and set user details
        else if (comment.getUserId() != null) {
            UserEntity user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            comment.setFullName(user.getFullName());
            comment.setIdNumber(user.getIdNumber());
        }

        comment.setDeleted(false);
        comment.setVisible(true);
        
        CommentEntity savedComment = commentRepository.save(comment);
        System.out.println("Comment added successfully with details: " + savedComment);
        return savedComment;
    } catch (Exception e) {
        System.err.println("Error adding comment: " + e.getMessage());
        throw e;
    }
}

    // Report post operations
    public List<PostEntity> getAllReportPosts() {
        try {
            System.out.println("Fetching all report posts");
            List<PostEntity> reportPosts = postRepository.findByIsSubmittedReportTrueAndIsDeletedFalseOrderByTimestampDesc();
            System.out.println("Found " + reportPosts.size() + " report posts");
            return reportPosts;
        } catch (Exception e) {
            System.err.println("Error fetching report posts: " + e.getMessage());
            throw new RuntimeException("Error fetching report posts", e);
        }
    }

    public List<PostEntity> getReportPostsByStatus(String status) {
        try {
            System.out.println("Fetching report posts with status: " + status);
            List<PostEntity> reportPosts = postRepository.findByIsSubmittedReportTrueAndStatusAndIsDeletedFalse(status);
            System.out.println("Found " + reportPosts.size() + " report posts with status: " + status);
            return reportPosts;
        } catch (Exception e) {
            System.err.println("Error fetching report posts by status: " + e.getMessage());
            throw new RuntimeException("Error fetching report posts by status", e);
        }
    }

    @Transactional
    public void updatePostStatusFromReport(int postId, ReportStatus reportStatus) {
        try {
            System.out.println("Updating status for report post ID: " + postId + " to " + reportStatus);
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
                
            if (Boolean.TRUE.equals(post.getIsSubmittedReport())) {
                post.setStatus(reportStatus.toString());
                post.setLastModifiedAt(LocalDateTime.now());
                postRepository.save(post);
                System.out.println("Report post status updated successfully");
            }
        } catch (Exception e) {
            System.err.println("Error updating report post status: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void syncPostWithReport(int postId) {
        try {
            System.out.println("Syncing post ID: " + postId + " with report");
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
                
            if (Boolean.TRUE.equals(post.getIsSubmittedReport())) {
                reportRepository.findAll().stream()
                    .filter(report -> report.getPostId() != null && report.getPostId().equals(postId))
                    .findFirst()
                    .ifPresent(report -> {
                        post.setStatus(report.getStatus().toString());
                        post.setLastModifiedAt(LocalDateTime.now());
                        postRepository.save(post);
                    });
                System.out.println("Post synced with report successfully");
            }
        } catch (Exception e) {
            System.err.println("Error syncing post with report: " + e.getMessage());
            throw e;
        }
>>>>>>> Meow
    }
}