package com.example.admin_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.util.Iterator;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.UserRepository;
import com.example.admin_backend.Repository.AdminRepository;

@Service
public class ProfileService {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    // User Profile Methods
    @Transactional
    public ProfileEntity saveUserProfilePicture(int userId, byte[] profilePicture) {
        System.out.println("Saving profile picture for user ID: " + userId);
        
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
        ProfileEntity profile = profileRepository.findByUser(user);
        if (profile == null) {
            System.out.println("Creating new profile for user");
            profile = new ProfileEntity();
            profile.setUser(user);
        }
        
        profile.setProfilePicture(profilePicture);
        ProfileEntity savedProfile = profileRepository.save(profile);
        System.out.println("Profile picture saved successfully");
        return savedProfile;
    }

    @Transactional
    public void deleteUserProfilePicture(int userId) {
        System.out.println("Deleting profile picture for user ID: " + userId);
        
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
        ProfileEntity profile = profileRepository.findByUser(user);
        if (profile != null) {
            profile.setProfilePicture(null);
            profileRepository.save(profile);
            System.out.println("Profile picture deleted successfully");
        } else {
            System.out.println("No profile found to delete picture from");
        }
    }

    public byte[] getUserProfilePicture(int userId) {
        System.out.println("Retrieving profile picture for user ID: " + userId);
        
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
        ProfileEntity profile = profileRepository.findByUser(user);
        return profile != null ? profile.getProfilePicture() : null;
    }

    public ProfileEntity getUserProfile(int userId) {
        System.out.println("Retrieving profile for user ID: " + userId);
        
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
        return profileRepository.findByUser(user);
    }

    private byte[] compressImage(byte[] originalImage) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalImage));
        
        // Create a new buffered image with RGB color model
        BufferedImage compressedImage = new BufferedImage(
            image.getWidth(), 
            image.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Draw the original image onto the new image
        compressedImage.createGraphics().drawImage(image, 0, 0, null);
        
        // Write to ByteArrayOutputStream with compression
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(0.5f); // Adjust quality: 1.0 = best, 0.0 = worst
        
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(compressedImage, null, null), params);
        
        return outputStream.toByteArray();
    }

    // Admin Profile Methods
    @Transactional
    public ProfileEntity saveAdminProfilePicture(int adminId, byte[] profilePicture) throws IOException {
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
            
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile == null) {
            profile = new ProfileEntity();
            profile.setAdmin(admin);
        }
        
        // Compress the image before saving
        byte[] compressedImage = compressImage(profilePicture);
        profile.setProfilePicture(compressedImage);
        
        return profileRepository.save(profile);
    }


    @Transactional
    public void deleteAdminProfilePicture(int adminId) {
        System.out.println("Deleting profile picture for admin ID: " + adminId);
        
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
            
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile != null) {
            profile.setProfilePicture(null);
            profileRepository.save(profile);
            System.out.println("Profile picture deleted successfully");
        } else {
            System.out.println("No profile found to delete picture from");
        }
    }

    public byte[] getAdminProfilePicture(int adminId) {
        System.out.println("Retrieving profile picture for admin ID: " + adminId);
        
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
            
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        return profile != null ? profile.getProfilePicture() : null;
    }

    public ProfileEntity getAdminProfile(int adminId) {
        System.out.println("Retrieving profile for admin ID: " + adminId);
        
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
            
        return profileRepository.findByAdmin(admin);
    }

    // Utility Methods
    public boolean isUserProfile(ProfileEntity profile) {
        return profile != null && profile.getUser() != null;
    }

    public boolean isAdminProfile(ProfileEntity profile) {
        return profile != null && profile.getAdmin() != null;
    }
}