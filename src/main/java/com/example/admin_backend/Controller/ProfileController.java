package com.example.admin_backend.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.UserRepository;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Service.ProfileService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private ProfileRepository profileRepository;

    private static final String DEFAULT_PROFILE_PATH = "src/main/resources/static/default.png";

    // Upload Profile Picture
    @PostMapping("/{role}/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable String role,
            @RequestParam("id") int id,
            @RequestParam("file") MultipartFile file) {
        
        System.out.println("Uploading profile picture for " + role + " with ID: " + id);
        
        if (file.isEmpty()) {
            System.err.println("Upload failed: Empty file");
            return new ResponseEntity<>("File cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            byte[] profilePicture = file.getBytes();
            ProfileEntity savedProfile;
            
            switch(role.toLowerCase()) {
                case "user":
                    savedProfile = profileService.saveUserProfilePicture(id, profilePicture);
                    break;
                case "admin":
                    savedProfile = profileService.saveAdminProfilePicture(id, profilePicture);
                    break;
                default:
                    System.err.println("Upload failed: Invalid role - " + role);
                    return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("Profile picture uploaded successfully");
            return ResponseEntity.ok(savedProfile);
        } catch (RuntimeException e) {
            System.err.println("Upload failed: Entity not found - " + e.getMessage());
            return new ResponseEntity<>("Entity not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            System.err.println("Upload failed: File processing error - " + e.getMessage());
            return new ResponseEntity<>("Failed to process file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Profile Picture
    @GetMapping("/{role}/getProfilePicture/{id}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String role, @PathVariable int id) {
        System.out.println("Fetching profile picture for " + role + " with ID: " + id);
        
        try {
            byte[] profilePicture = null;
            
            switch(role.toLowerCase()) {
                case "user":
                    profilePicture = profileService.getUserProfilePicture(id);
                    break;
                case "admin":
                    profilePicture = profileService.getAdminProfilePicture(id);
                    break;
                default:
                    System.err.println("Invalid role requested: " + role);
                    return ResponseEntity.badRequest().build();
            }

            if (profilePicture == null) {
                System.out.println("No profile picture found, attempting to load default");
                Path defaultPath = Paths.get(DEFAULT_PROFILE_PATH);
                if (Files.exists(defaultPath)) {
                    profilePicture = Files.readAllBytes(defaultPath);
                } else {
                    System.err.println("Default profile picture not found");
                    return ResponseEntity.notFound().build();
                }
            }

            System.out.println("Profile picture retrieved successfully");
            return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .header("Cache-Control", "max-age=3600")
                .body(profilePicture);
                
        } catch (IOException e) {
            System.err.println("Error reading profile picture: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete Profile Picture
    @DeleteMapping("/{role}/profilePicture/{id}")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable String role, @PathVariable int id) {
        System.out.println("Deleting profile picture for " + role + " with ID: " + id);
        
        try {
            switch(role.toLowerCase()) {
                case "user":
                    profileService.deleteUserProfilePicture(id);
                    break;
                case "admin":
                    profileService.deleteAdminProfilePicture(id);
                    break;
                default:
                    System.err.println("Delete failed: Invalid role - " + role);
                    return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("Profile picture deleted successfully");
            return new ResponseEntity<>("Profile picture deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("Delete failed: Entity not found - " + e.getMessage());
            return new ResponseEntity<>("Entity not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get Profile Details
    @GetMapping("/{role}/details/{id}")
    public ResponseEntity<?> getProfileDetails(@PathVariable String role, @PathVariable int id) {
        System.out.println("Fetching profile details for " + role + " with ID: " + id);
        
        try {
            ProfileEntity profile;
            switch(role.toLowerCase()) {
                case "user":
                    profile = profileService.getUserProfile(id);
                    break;
                case "admin":
                    profile = profileService.getAdminProfile(id);
                    break;
                default:
                    System.err.println("Invalid role requested: " + role);
                    return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
            }

            if (profile == null) {
                System.err.println("Profile not found");
                return ResponseEntity.notFound().build();
            }

            System.out.println("Profile details retrieved successfully");
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            System.err.println("Error fetching profile details: " + e.getMessage());
            return new ResponseEntity<>("Entity not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}