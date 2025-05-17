package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.CreationException;
import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.payloads.RentRequest;
import com.iss.renterscore.authentication.repos.BookmarkRepo;
import com.iss.renterscore.authentication.repos.PropertyRepo;
import com.iss.renterscore.authentication.repos.RentPropertyRepo;
import com.iss.renterscore.authentication.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.iss.renterscore.authentication.utils.Utils.convertDate;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepo propertyRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final BookmarkRepo bookmarkRepo;
    private final RentPropertyRepo rentPropertyRepo;

    // get all properties with bookmark status
    public List<PropertyDto> getAllProperties(CustomUserDetails userDetails) {
        List<PropertyDto> propertyDtos;
        List<Property> properties = propertyRepo.findAllOrderByModifiedDate();

        if (userDetails != null) {
            Users users = userRepo.existsByEmail(userDetails.getUsername());
            Set<Long> bookmarkedIds = bookmarkRepo.findAllByUser(users)
                    .stream()
                    .map(bookmark -> bookmark.getProperty().getId())
                    .collect(Collectors.toSet());
            propertyDtos = properties.stream()
                    .map( property -> new PropertyDto(property, bookmarkedIds.contains(property.getId()))
                    ).toList();
        } else {
            propertyDtos = properties.stream().map(p -> new PropertyDto(p, false)).toList();
        }
        return propertyDtos;
    }

    // get all created properties
    public Optional<List<PropertyDto>> getAllCreatedProperties(CustomUserDetails userDetails) {
        Users user = userRepo.existsByEmail(userDetails.getUsername());

        List<Property> properties = propertyRepo.findAllByUser(user);
        return Optional.of(properties.stream().map(p -> new PropertyDto(p, false)).toList());
    }

    // bookmark items
    public Optional<List<PropertyDto>> getAllBookmarkedProperties(CustomUserDetails userDetails) {
        Users users = userRepo.existsByEmail(userDetails.getUsername());
        List<Bookmark> bookmarks = bookmarkRepo.findAllByUser(users);
        if (bookmarks.isEmpty()) return Optional.empty();
        List<PropertyDto> propertyDtos = bookmarks.stream()
                .map(bookmark -> new PropertyDto(bookmark.getProperty(), true))
                .toList();
        return Optional.of(propertyDtos);
    }

    public Optional<PropertyDto> getPropertyDetails(CustomUserDetails userDetails, Long propertyId) {
        Property property = propertyRepo.getReferenceById(propertyId);
        PropertyDto propertyDto = new PropertyDto(property, false);

        if (userDetails != null) {
            Users users = userRepo.existsByEmail(userDetails.getUsername());
            Bookmark bookmark = bookmarkRepo.findByUserIdAndPropertyId(users, property);
            if (bookmark != null) {
                propertyDto = new PropertyDto(property, true);
            }
        }
        return Optional.of(propertyDto);
    }

    public Optional<Property> getUpdatePropertyDetails(Long propertyId) {
        return propertyRepo.findById(propertyId);
    }

    public synchronized Optional<ApiResponse> createProperty(CustomUserDetails userDetails, PropertyRequest request, List<MultipartFile> files) throws IOException {
        ApiResponse response = new ApiResponse("Property created successfully.", true);

        if (Boolean.TRUE.equals(existProperty(request.getUnitNo(), request.getBlockNo(), request.getPostalCode()))) {
            throw new ResourceAlreadyInUseException("Block No & Unit No", "Postal Code", request.getUnitNo());
        }
        Property property = new Property();
        Users user = userRepo.existsByEmail(userDetails.getUsername());
        if (files != null && !files.isEmpty()) {
            if (Boolean.TRUE.equals(request.getIsHeroImageChanged())) {
                String heroImageUrl = fileService.saveImageFiles(userDetails, files.get(0), ImageType.HEROES);
                property.setHeroImage(heroImageUrl);
                files.remove(0);
            }
            List<PropertyImage> savedImages = savedDetailImages(userDetails, property, files);
            property.getImages().addAll(savedImages);
        }
        updateObject(request, property);

        property.setUser(user);

        try {
            propertyRepo.save(property);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CreationException("Property" + property.getTitle(), e.getMessage());
        }
        return Optional.of(response);
    }

    private List<PropertyImage> savedDetailImages(CustomUserDetails userDetails, Property property, List<MultipartFile> images) throws IOException {
        List<PropertyImage> savedImages = new ArrayList<>();
        if (images.isEmpty()) return savedImages;

        for (MultipartFile file : images) {
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

    public synchronized Optional<ApiResponse> updateProperty(CustomUserDetails userDetails, Long propertyId, PropertyRequest request, List<MultipartFile> files) throws IOException {

        ApiResponse response = new ApiResponse("Property updated successfully.", true);
        Property property = propertyRepo.getReferenceById(propertyId);

        if (files != null && !files.isEmpty()) {
            String heroImageUrl = fileService.saveImageFiles(userDetails, files.get(0), ImageType.HEROES);
            property.setHeroImage(heroImageUrl);
            List<PropertyImage> savedImages = savedDetailImages(userDetails, property, files);
            property.getImages().addAll(savedImages);
        }

        updateObject(request, property);
        try {
            propertyRepo.save(property);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Update property failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public void updateObject(PropertyRequest request, Property property) {
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAmenities(request.getAmenities());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setAvailableDate(request.getAvailableDate());
        property.setPropertyStatus(request.getPropertyStatus());
        property.setPrice(request.getPrice());
        property.setCurrency("SGD");
        property.setPropertyState(request.getPropertyState());
        property.setSize(request.getSize());
        property.setRentType(request.getRentType());
        property.setPropertyType(request.getPropertyType());
        Address address = new Address(request.getBlockNo(), request.getUnitNo(), request.getStreet(), request.getPostalCode(), request.getRegions());
        property.setAddress(address);
    }

    public synchronized Optional<ApiResponse> deleteProperty(Long propertyId) {

        ApiResponse response = new ApiResponse("Property deleted successfully.", true);
        Property property = propertyRepo.getReferenceById(propertyId);
        if (property.getPropertyState().equals(PropertyState.OCCUPIED)) {
            response = new ApiResponse("Occupied property cannot be deleted.", false);
            return Optional.of(response);
        }
        try {
            propertyRepo.deleteById(propertyId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Delete property failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public synchronized Optional<ApiResponse> bookmarkProperty(Long userId, Long propertyId) {

        ApiResponse response = new ApiResponse("Bookmarked successfully.", true);
        Users users = userRepo.getReferenceById(userId);
        Property property = propertyRepo.getReferenceById(propertyId);
        Bookmark isExist = bookmarkRepo.findByUserIdAndPropertyId(users, property);
        try {
            if (isExist == null) {
                Bookmark bookmark = new Bookmark();
                bookmark.setUsers(users);
                bookmark.setProperty(property);
                bookmarkRepo.save(bookmark);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Bookmark is failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public synchronized Optional<ApiResponse> removeBookmark(Long userId, Long propertyId) {

        ApiResponse response = new ApiResponse("Removed bookmark successfully.", true);
        Users users = userRepo.getReferenceById(userId);
        Property property = propertyRepo.getReferenceById(propertyId);
        try {
            Bookmark bookmark = bookmarkRepo.findByUserIdAndPropertyId(users, property);
            if (bookmark != null) {
                bookmarkRepo.delete(bookmark);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Removed bookmark is failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public synchronized Optional<ApiResponse> requestRentProperty(CustomUserDetails userDetails, RentRequest request) {

        ApiResponse response = new ApiResponse("Sent rent request successfully.", true);
        Users users = userRepo.getReferenceById(userDetails.getId());
        Property property = propertyRepo.getReferenceById(request.getPropertyId());
        if (Boolean.TRUE.equals(existRentProperty(users, property, convertDate(request.getStartDate()), convertDate(request.getEndDate())))) {
            throw new ResourceAlreadyInUseException("Property & Date", "Postal Code", request.getPropertyId());
        }

        RentProperty rentProperty = new RentProperty();
        rentProperty.setTenant(users);

        getUpdatedObject(request, rentProperty, property);

        try {
            rentPropertyRepo.save(rentProperty);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Rent request is failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    private Boolean existRentProperty(Users users, Property property, LocalDate startDate, LocalDate endDate) {
        RentProperty rentProperty = rentPropertyRepo.findByTenantAndPropertyAndStartDateAndEndDate(users, property, startDate, endDate);
        return rentProperty != null;
    }

    public synchronized Optional<ApiResponse> updateRentRequest(Long requestId, RentRequest request) {

        ApiResponse response = new ApiResponse("Update rent request successfully.", true);
        RentProperty rentProperty = rentPropertyRepo.getReferenceById(requestId);
        Property property = propertyRepo.getReferenceById(request.getPropertyId());

        getUpdatedObject(request, rentProperty, property);
        try {
            if (rentProperty.getRentStatus() == RentStatus.CANCELLED) {
                rentPropertyRepo.delete(rentProperty);
                response = new ApiResponse("Request cancelled successfully.", true);
            } else {
                rentPropertyRepo.save(rentProperty);
                if (request.getRentStatus() == RentStatus.CONFIRMED) {
                    property.setPropertyState(PropertyState.OCCUPIED);
                    propertyRepo.save(property);
                } else {
                    property.setPropertyState(PropertyState.AVAILABLE);
                    propertyRepo.save(property);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Update rent request is failed with " + e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public void getUpdatedObject(RentRequest request, RentProperty rentProperty, Property property) {
        rentProperty.setProperty(property);
        rentProperty.setOwner(property.getUser());
        rentProperty.setRentStatus(request.getRentStatus());
        rentProperty.setStartDate(convertDate(request.getStartDate()));
        rentProperty.setEndDate(convertDate(request.getEndDate()));
    }

    public Optional<List<RentPropertyDto>> getRequestedItems(CustomUserDetails userDetails) {

        List<RentProperty> rentProperties;
        Users users = userRepo.getReferenceById(userDetails.getId());
        if (users.getPropertyRole() == PropertyRole.ROLE_TENANT) {
            rentProperties = rentPropertyRepo.getAllRentPropertyByTenant(users);
        } else {
            rentProperties = rentPropertyRepo.getAllRentPropertyByOwner(users);
        }
        return Optional.of(rentProperties.stream().map(RentPropertyDto::new).toList());

    }

}
