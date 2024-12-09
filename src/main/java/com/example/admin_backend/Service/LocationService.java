package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.LocationEntity;
import com.example.admin_backend.Entity.BuildingEntity;
import com.example.admin_backend.Repository.LocationRepository;
import com.example.admin_backend.Repository.UserRepository;
import com.example.admin_backend.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildingService buildingService;

    private static final double DISTANCE_THRESHOLD = 500.0; // 500 meters threshold
    
    private static final String DEFAULT_BUILDING = "CIT-U College Library";
    
    // Save the user's location by passing the user's idNumber manually
    public LocationEntity saveUserLocation(Double latitude, Double longitude, String idNumber, String buildingName) {
        // Fetch the user by idNumber
        UserEntity user = userRepository.findByIdNumber(idNumber);
        
        if (user == null) {
            throw new RuntimeException("User not found in the system");
        }
    
        // Even if latitude and longitude are null, we still save the location with just the building name
        LocationEntity locationEntity = new LocationEntity(user, latitude, longitude, buildingName);
    
        // Save and return the LocationEntity
        return locationRepository.save(locationEntity);
    }
    
    // Method to find the nearest building based on latitude and longitude
    public String findNearestBuilding(Double userLat, Double userLng) {
        // Return library if no coordinates provided
        if (userLat == null || userLng == null) {
            return DEFAULT_BUILDING;
        }
        
        List<BuildingEntity> buildings = buildingService.getAllBuildings();
        String nearestBuilding = DEFAULT_BUILDING;  // Default to library
        double shortestDistance = Double.MAX_VALUE;

        for (BuildingEntity building : buildings) {
            // Skip buildings with null coordinates
            if (building.getLatitude() == null || building.getLongitude() == null) {
                continue;
            }

            double distance = calculateDistance(userLat, userLng, building.getLatitude(), building.getLongitude());
            // Debug logging
            System.out.println("Building: " + building.getBuildingName() + ", Distance: " + distance + " meters");
            
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestBuilding = building.getBuildingName();
            }
        }

        // Fallback to library if distance exceeds threshold
        if (shortestDistance > DISTANCE_THRESHOLD) {
            System.out.println("Location not precisely detected. Falling back to library.");
            return DEFAULT_BUILDING;
        }

        return nearestBuilding;
    }

    // Haversine formula to calculate distance between two lat/lng coordinates in meters
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Convert to meters
    }

    // Method to get all locations
    public List<LocationEntity> getAllLocations() {
        return locationRepository.findAll();
    }

    // Method to get location by ID
    public LocationEntity getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    // Method to update location by ID
    public LocationEntity updateLocation(Long id, Double latitude, Double longitude) {
        LocationEntity location = locationRepository.findById(id).orElse(null);
        if (location != null) {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            
            // Update the building name based on the new coordinates
            String nearestBuilding = findNearestBuilding(latitude, longitude);
            location.setBuildingName(nearestBuilding);
            
            return locationRepository.save(location);
        }
        return null;
    }

    // Method to delete location by ID
    public boolean deleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
