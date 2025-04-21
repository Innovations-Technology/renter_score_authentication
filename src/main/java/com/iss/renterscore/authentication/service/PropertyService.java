package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.repos.PropertyRepo;
import com.iss.renterscore.authentication.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepo propertyRepo;
    private final UserRepo userRepo;
    private final FileService fileService;



    public Optional<List<Property>> getAllProperties() {
        return Optional.of(propertyRepo.findAll());
    }

    public Optional<List<Property>> getAllProperties(CustomUserDetails userDetails) {
        Users user = userRepo.existsByEmail(userDetails.getUsername());
        return Optional.of(propertyRepo.findAllByUser(user));
    }

    public synchronized Optional<ApiResponse> createProperty(CustomUserDetails userDetails, PropertyRequest request, List<MultipartFile> files) throws IOException {
        ApiResponse response = new ApiResponse("Property created successfully.", true);

        if (Boolean.TRUE.equals(existProperty(request.getUnitNo(), request.getBlockNo(), request.getPostalCode()))) {
            throw new ResourceAlreadyInUseException("Block No & Unit No", "Postal Code", request.getUnitNo());
        }
        Property property = new Property();
        Users user = userRepo.existsByEmail(userDetails.getUsername());
        if (!files.isEmpty()) {
            String heroImageUrl = fileService.saveImageFiles(userDetails, files.get(0), ImageType.HEROES);
            property.setHeroImage(heroImageUrl);
            List<PropertyImage> savedImages = savedDetailImages(userDetails, property, files);
            property.getImages().addAll(savedImages);
        }
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAmenities(request.getAmenities());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setAvailableDate(request.getAvailableDate());
        property.setPropertyStatus(request.getPropertyStatus());
        property.setPrice(request.getPrice());
        property.setCurrency("SGD");
        property.setSize(request.getSize());
        property.setRentType(request.getRentType());
        property.setPropertyType(request.getPropertyType());
        Address address = new Address(request.getBlockNo(), request.getUnitNo(), request.getStreet(), request.getPostalCode(), request.getRegions());
        property.setAddress(address);

        property.setUser(user);

        try {
            propertyRepo.save(property);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Create property failed with "+ e.getMessage(), false);
        }
        return Optional.of(response);
    }

    private List<PropertyImage> savedDetailImages(CustomUserDetails userDetails, Property property, List<MultipartFile> images) throws IOException {
        List<PropertyImage> savedImages = new ArrayList<>();
        if (images.isEmpty() || images.size() <= 1) return savedImages;

        List<MultipartFile> detailImages = images.subList(1, images.size());
        for (MultipartFile file: detailImages) {
            if (file.isEmpty()) continue;

            String imageUrl = fileService.saveImageFiles(userDetails, file, ImageType.DETAILS);
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImage(imageUrl);
            propertyImage.setProperty(property);

            savedImages.add(propertyImage);
        }
        return savedImages;
    }


    private Boolean existProperty(String unitNo, String blockNo, String postalCode) {
        Property property = propertyRepo.findByAddress_UnitNoAndAddress_BlockNoAndAddress_PostalCode(unitNo, blockNo, postalCode);
        return property != null;
    }
    public synchronized Optional<Property> updateProperty(UserDetails userDetails, Long id, PropertyRequest request) {
        Property property = propertyRepo.getReferenceById(id);

        property.setPropertyStatus(request.getPropertyStatus());
        property.setPropertyType(request.getPropertyType());
        return Optional.of(propertyRepo.save(property));
    }




}
