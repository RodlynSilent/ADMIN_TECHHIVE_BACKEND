package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.LeaderboardEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.LeaderboardRepository;
import com.example.admin_backend.Repository.UserRepository;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new leaderboard entry for a user with the given points
    @Transactional
    public void createInitialLeaderboardEntry(int userId, int points) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));

<<<<<<< HEAD
        LeaderboardEntity leaderboardEntry = new LeaderboardEntity();
        leaderboardEntry.setUser(user);
        leaderboardEntry.setPoints(points);
        leaderboardEntry.setAchievedAt(LocalDateTime.now());
        leaderboardEntry.setUserRank(0);  // Rank will be updated later
=======
            // Set initial user points
            user.setPoints(points);
            userRepository.save(user);

            LeaderboardEntity leaderboardEntry = new LeaderboardEntity();
            leaderboardEntry.setUser(user);
            leaderboardEntry.setPoints(points);
            leaderboardEntry.setAchievedAt(LocalDateTime.now());
            leaderboardEntry.setUserRank(0);
>>>>>>> Meow

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks
    }

    // Add points to the leaderboard for a user
    @Transactional
    public LeaderboardEntity addPoints(int userId, int points) {
        LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));

<<<<<<< HEAD
        leaderboardEntry.setPoints(leaderboardEntry.getPoints() + points);
        leaderboardEntry.setAchievedAt(LocalDateTime.now());

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks
=======
            // Update user points
            user.addPoints(points);
            userRepository.save(user);

            LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    LeaderboardEntity newEntry = new LeaderboardEntity();
                    newEntry.setUser(user);
                    newEntry.setPoints(0);
                    newEntry.setUserRank(0);
                    newEntry.setAchievedAt(LocalDateTime.now());
                    return newEntry;
                });

            leaderboardEntry.setPoints(user.getPoints());  // Sync with user points
            leaderboardEntry.setAchievedAt(LocalDateTime.now());
>>>>>>> Meow

        return leaderboardEntry;
    }

    // Get leaderboard rankings (sorted by points and time of achievement)
    public List<LeaderboardEntity> getLeaderboardRankings() {
        return leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc(); // Sorted by points, then by achievedAt
    }

    // Subtract points from the leaderboard for a user
    @Transactional
    public LeaderboardEntity subtractPoints(int userId, int points) {
        LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));

<<<<<<< HEAD
        leaderboardEntry.setPoints(Math.max(leaderboardEntry.getPoints() - points, 0)); // Prevent points from going below 0
        leaderboardEntry.setAchievedAt(LocalDateTime.now());

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks
=======
            // Update user points
            user.subtractPoints(points);
            userRepository.save(user);

            LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    LeaderboardEntity newEntry = new LeaderboardEntity();
                    newEntry.setUser(user);
                    newEntry.setPoints(0);
                    newEntry.setUserRank(0);
                    newEntry.setAchievedAt(LocalDateTime.now());
                    return newEntry;
                });

            leaderboardEntry.setPoints(user.getPoints());  // Sync with user points
            leaderboardEntry.setAchievedAt(LocalDateTime.now());
>>>>>>> Meow

        return leaderboardEntry;
    }

    // Get a specific user's leaderboard entry
    public LeaderboardEntity getLeaderboardEntryByUserId(int userId) {
        return leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));
    }

    // Method to automatically update leaderboard ranks
    @Transactional
    public void updateLeaderboardRanks() {
        List<LeaderboardEntity> leaderboard = leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc();
        int rank = 1;
        for (LeaderboardEntity entry : leaderboard) {
            entry.setUserRank(rank);
            leaderboardRepository.save(entry);
            rank++;
        }
    }

<<<<<<< HEAD
    // Helper method to determine badge based on points
=======
    // Methods for handling likes/dislikes
    @Transactional
    public LeaderboardEntity handleAdminLike(int userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            
            user.addLikePoints();
            userRepository.save(user);

            LeaderboardEntity leaderboard = leaderboardRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found"));
            
            leaderboard.setPoints(user.getPoints());
            leaderboard.setAchievedAt(LocalDateTime.now());
            
            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboard);
            updateLeaderboardRanks();
            return savedEntry;
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle admin like", e);
        }
    }

    @Transactional
    public LeaderboardEntity handleAdminDislike(int userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            
            user.addDislikePoints();
            userRepository.save(user);

            LeaderboardEntity leaderboard = leaderboardRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found"));
            
            leaderboard.setPoints(user.getPoints());
            leaderboard.setAchievedAt(LocalDateTime.now());
            
            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboard);
            updateLeaderboardRanks();
            return savedEntry;
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle admin dislike", e);
        }
    }

    @Transactional
    public LeaderboardEntity removeAdminLike(int userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            
            user.removeLikePoints();
            userRepository.save(user);

            LeaderboardEntity leaderboard = leaderboardRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found"));
            
            leaderboard.setPoints(user.getPoints());
            leaderboard.setAchievedAt(LocalDateTime.now());
            
            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboard);
            updateLeaderboardRanks();
            return savedEntry;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove admin like", e);
        }
    }

    @Transactional
    public LeaderboardEntity removeAdminDislike(int userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            
            user.removeDislikePoints();
            userRepository.save(user);

            LeaderboardEntity leaderboard = leaderboardRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found"));
            
            leaderboard.setPoints(user.getPoints());
            leaderboard.setAchievedAt(LocalDateTime.now());
            
            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboard);
            updateLeaderboardRanks();
            return savedEntry;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove admin dislike", e);
        }
    }

>>>>>>> Meow
    public String getBadge(int points) {
        if (points >= 100) {
            return "Champion";
        } else if (points >= 80) {
            return "Prowler";
        } else {
            return "Cub";
        }
    }
}
