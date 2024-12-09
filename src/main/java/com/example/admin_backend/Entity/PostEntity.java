package com.example.admin_backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblpost")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;
    
    @Column(columnDefinition = "varchar(500)")
    private String content;
    
    private LocalDateTime timestamp;
    
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "admin_id")
    private Integer adminId;
    
    @Column(name = "superuser_id")
    private Integer superUserId;
    
    @Column(name = "user_role")
    private String userRole;
    
    @Column(name = "isverified")
    private Boolean isVerified = false;
    
    @Column(name = "is_visible")
    private Boolean isVisible = true;
    
    private Integer likes;
    private Integer dislikes;
    
    @Column(name = "fullname")
    private String fullname;
    
    @Column(name = "idnumber")
    private String idnumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProfileEntity profile;
    
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    
   @ElementCollection
@CollectionTable(name = "post_liked_by", joinColumns = @JoinColumn(name = "post_id"))
@Column(name = "user_id")
private Set<Integer> likedBy = new HashSet<>();

@ElementCollection
@CollectionTable(name = "post_disliked_by", joinColumns = @JoinColumn(name = "post_id"))
@Column(name = "user_id")
private Set<Integer> dislikedBy = new HashSet<>();
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    @Column(name = "is_submitted_report")
    private Boolean isSubmittedReport;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "reportid")
    private Integer reportId;
    
    @Column(name = "last_modified_by")
    private Integer lastModifiedBy;
    
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public Integer getSuperUserId() { return superUserId; }
    public void setSuperUserId(Integer superUserId) { this.superUserId = superUserId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public ProfileEntity getProfile() { return profile; }
    public void setProfile(ProfileEntity profile) { this.profile = profile; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Set<Integer> getLikedBy() { return likedBy; }
    public void setLikedBy(Set<Integer> likedBy) { this.likedBy = likedBy; }

    public Set<Integer> getDislikedBy() { return dislikedBy; }
    public void setDislikedBy(Set<Integer> dislikedBy) { this.dislikedBy = dislikedBy; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Boolean getIsSubmittedReport() { return isSubmittedReport; }
    public void setIsSubmittedReport(Boolean submittedReport) { isSubmittedReport = submittedReport; }
    public void setIsVerified(Boolean verified) {
        this.isVerified = verified;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public Integer getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(Integer lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    @Override
    public String toString() {
        return "PostEntity{" +
                "postId=" + postId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                ", isVerified=" + isVerified +
                ", isVisible=" + isVisible +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", fullName='" + fullName + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", profile='" + profile + '\'' +
                ", image='" + (image != null ? "image present" : "no image") + '\'' +
                ", likedBy=" + likedBy +
                ", dislikedBy=" + dislikedBy +
                ", isDeleted=" + isDeleted +
                ", isSubmittedReport=" + isSubmittedReport + 
                ", status='" + status + '\'' +
                '}';
    }
}