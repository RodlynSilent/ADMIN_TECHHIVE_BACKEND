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
    private ReportRepository reportRepository;

    @Autowired
    @SuppressWarnings("unused")
    private LeaderboardService leaderboardService;

    // Get all posts
    public List<PostEntity> getAllPosts() {
        try {
            System.out.println("Fetching all posts");
            List<PostEntity> posts = postRepository.findByIsDeletedFalseOrderByTimestampDesc();
            System.out.println("Found " + posts.size() + " posts");
            return posts;
        } catch (Exception e) {
            System.err.println("Error fetching all posts: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching all posts", e);
        }
    }

    // Get only visible posts
 public List<PostEntity> getAllVisiblePosts() {
    try {
        System.out.println("PostService: Starting to fetch visible posts");
        List<PostEntity> posts = postRepository.findByIsDeletedFalseAndIsVisibleTrue();
        System.out.println("PostService: SQL query executed successfully");
        System.out.println("PostService: Number of posts found: " + posts.size());
        posts.forEach(post -> System.out.println("Post ID: " + post.getPostId() + ", isVisible: " + post.isVisible()));
        return posts;
    } catch (Exception e) {
        System.err.println("PostService Error in getAllVisiblePosts: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error fetching visible posts", e);
    }
}

    // Get post by ID
    public Optional<PostEntity> getPostById(int postId) {
        try {
            System.out.println("Fetching post with ID: " + postId);
            Optional<PostEntity> post = postRepository.findByPostIdAndIsDeletedFalse(postId);
            System.out.println("Post found: " + post.isPresent());
            return post;
        } catch (Exception e) {
            System.err.println("Error fetching post by ID: " + e.getMessage());
            throw new RuntimeException("Error fetching post", e);
        }
    }

    // Create new post
    @Transactional
    public PostEntity createPost(PostEntity post) {
        try {
            System.out.println("Creating new post");
            if (post.getContent() == null && post.getImage() == null) {
                throw new IllegalArgumentException("Post must have either content or an image");
            }

            post.setTimestamp(LocalDateTime.now());
            post.setDeleted(false);
            post.setLikedBy(new HashSet<>());
            post.setDislikedBy(new HashSet<>());

            // Set initial status only for report posts
            if (Boolean.TRUE.equals(post.getIsSubmittedReport())) {
                post.setStatus(ReportStatus.PENDING.toString());
            } else {
                post.setStatus(null); // Ensure non-report posts have no status
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

        user.setPoints(Math.max(user.getPoints(), 0));
        
        post.setFullName(user.getFullName());
        post.setProfile(profileRepository.findByUser(user));
        post.setTimestamp(LocalDateTime.now());
        post.setIsSubmittedReport(post.getIsSubmittedReport()); 
        post.setDeleted(false);
        
        return postRepository.save(post);
    }

    private PostEntity createAdminPost(PostEntity post) {
        AdminEntity admin = adminRepository.findById(post.getAdminId())
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        post.setFullName(admin.getFullName());
        post.setProfile(profileRepository.findByAdmin(admin));
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    private PostEntity createSuperUserPost(PostEntity post) {
        SuperUserEntity superuser = superUserRepository.findById(post.getSuperUserId())
            .orElseThrow(() -> new RuntimeException("Superuser not found"));

        post.setFullName(superuser.getFullName());
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    // Update post
    @Transactional
    public PostEntity updatePost(int postId, PostEntity postDetails) {
        try {
            System.out.println("Updating post with ID: " + postId);
            PostEntity existingPost = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

            if (postDetails.getContent() != null) {
                existingPost.setContent(postDetails.getContent());
            }
            if (postDetails.getImage() != null) {
                existingPost.setImage(postDetails.getImage());
            }

            existingPost.setLastModifiedAt(LocalDateTime.now());

            if (Boolean.TRUE.equals(existingPost.getIsSubmittedReport()) && postDetails.getStatus() != null) {
                existingPost.setStatus(postDetails.getStatus());
            }

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

            PostEntity updatedPost = postRepository.save(existingPost);
            System.out.println("Post updated successfully");
            return updatedPost;
        } catch (Exception e) {
            System.err.println("Error updating post: " + e.getMessage());
            throw e;
        }
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

    // Post visibility
    public PostEntity updateVisibility(int postId, boolean newVisibility) {
        try {
            System.out.println("Updating visibility for post ID: " + postId);
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setVisible(newVisibility);
            post.setLastModifiedAt(LocalDateTime.now());
            PostEntity updatedPost = postRepository.save(post);
            System.out.println("Visibility updated successfully");
            return updatedPost;
        } catch (Exception e) {
            System.err.println("Error updating visibility: " + e.getMessage());
            throw e;
        }
    }

    // Soft delete
    @Transactional
    public void softDeletePost(int postId) {
        try {
            System.out.println("Soft deleting post ID: " + postId);
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setDeleted(true);
            post.setLastModifiedAt(LocalDateTime.now());
            postRepository.save(post);
            System.out.println("Post soft deleted successfully");
        } catch (Exception e) {
            System.err.println("Error soft deleting post: " + e.getMessage());
            throw e;
        }
    }

    // Comments
    public List<CommentEntity> getCommentsByPostId(int postId) {
        try {
            System.out.println("Fetching comments for post ID: " + postId);
            List<CommentEntity> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
            System.out.println("Found " + comments.size() + " comments");
            return comments;
        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
            throw e;
        }
    }

    public CommentEntity addComment(CommentEntity comment, int postId) {
        try {
            System.out.println("Adding comment to post ID: " + postId);
            comment.setPostId(postId);
            CommentEntity savedComment = commentRepository.save(comment);
            System.out.println("Comment added successfully");
            return savedComment;
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
            throw e;
        }
    }

    // Like/Dislike functionality
    @Transactional
    public PostEntity toggleLike(int postId, int userId, String userRole) {
        try {
            System.out.println("Toggling like for post ID: " + postId + " by user ID: " + userId);
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
                post.getLikedBy().remove(userId);
                post.setLikes(post.getLikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), -1);
                }
            } else {
                if (hasDisliked) {
                    post.getDislikedBy().remove(userId);
                    post.setDislikes(post.getDislikes() - 1);
                    if (!isOwnPost && !isAdminOrSuperuser) {
                        updateUserPoints(post.getUserId(), 1);
                    }
                }
                post.getLikedBy().add(userId);
                post.setLikes(post.getLikes() + 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), 1);
                }
            }

            PostEntity updatedPost = postRepository.save(post);
            System.out.println("Like toggled successfully");
            return updatedPost;
        } catch (Exception e) {
            System.err.println("Error toggling like: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public PostEntity toggleDislike(int postId, int userId, String userRole) {
        try {
            System.out.println("Toggling dislike for post ID: " + postId + " by user ID: " + userId);
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
                post.getDislikedBy().remove(userId);
                post.setDislikes(post.getDislikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), 1);
                }
            } else {
                if (hasLiked) {
                    post.getLikedBy().remove(userId);
                    post.setLikes(post.getLikes() - 1);
                    if (!isOwnPost && !isAdminOrSuperuser) {
                        updateUserPoints(post.getUserId(), -1);
                    }
                }
                post.getDislikedBy().add(userId);
                post.setDislikes(post.getDislikes() + 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), -1);
                }

                if ("USER".equalsIgnoreCase(post.getUserRole()) && post.getDislikes() >= 50) {
                    softDeletePost(postId);
                    return post;
                }
            }

            PostEntity updatedPost = postRepository.save(post);
            System.out.println("Dislike toggled successfully");
            return updatedPost;
        } catch (Exception e) {
            System.err.println("Error toggling dislike: " + e.getMessage());
            throw e;
        }
    }

    // Helper methods
    private void updateUserPoints(int userId, int pointChange) {
        try {
            System.out.println("Updating points for user ID: " + userId + " by " + pointChange + " points");
            if ("USER".equalsIgnoreCase(userRepository.findById(userId)
                    .map(UserEntity::getRole)
                    .orElse(null))) {
                UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                user.setPoints(Math.max(0, user.getPoints() + pointChange));
                userRepository.save(user);
                System.out.println("User points updated successfully");
            }
        } catch (Exception e) {
            System.err.println("Error updating user points: " + e.getMessage());
            throw e;
        }
    }

    // Report post methods
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
    }

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
}