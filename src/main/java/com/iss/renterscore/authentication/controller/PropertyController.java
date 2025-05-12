package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.*;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.Property;
import com.iss.renterscore.authentication.model.PropertyDto;
import com.iss.renterscore.authentication.model.RentPropertyDto;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.payloads.RentRequest;
import com.iss.renterscore.authentication.service.CurrentUser;
import com.iss.renterscore.authentication.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "https://renterscore.live", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyDto>> getAllProperties(@CurrentUser CustomUserDetails currentUser) {
        List<PropertyDto> properties = propertyService.getAllProperties(currentUser);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<PropertyDto> getPropertyDetails(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {

        PropertyDto properties = propertyService.getPropertyDetails(currentUser, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id ", propertyId + "", "Not found!"));

        return ResponseEntity.ok(properties);
    }

    @GetMapping("/update-details/{id}")
    public ResponseEntity<PropertyDto> getUpdatePropertyDetails(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        Property properties = propertyService.getUpdatePropertyDetails(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id ", propertyId + "", "Not found!"));

        return ResponseEntity.ok(new PropertyDto(properties, false));
    }

    @GetMapping("/created-properties")
    public ResponseEntity<List<PropertyDto>> getAllCreatedProperties(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        List<PropertyDto> properties = propertyService.getAllCreatedProperties(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Property with user ", currentUser.getEmail(), "Not found!"));

        return ResponseEntity.ok(properties);

    }

    @PostMapping("/create-property")
    public ResponseEntity<ApiResponse> createProperty(@CurrentUser CustomUserDetails currentUser, @RequestPart("property") PropertyRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        ApiResponse response;
        try {
            response = propertyService.createProperty(currentUser, request, files)
                    .orElseThrow(() -> new DataAlreadyExistException("Property values :" + request.getBlockNo() + request.getUnitNo() + request.getPostalCode(), "Property already exist"));
        } catch (IOException e) {
            throw new AppException("User " + currentUser.getEmail() + "Can't create property!", e );
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-property")
    public ResponseEntity<ApiResponse> updateProperty(@CurrentUser CustomUserDetails currentUser,  @RequestPart("id") Long propertyId, @RequestPart("property") PropertyRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        ApiResponse response;
        try {
            response = propertyService.updateProperty(currentUser, propertyId, request, files)
                    .orElseThrow(() -> new DataAlreadyExistException("Property values :" + request.getBlockNo() + request.getUnitNo() + request.getPostalCode(), "Property already exist"));
        } catch (IOException e) {
            throw new AppException("User " + currentUser.getEmail() + "Can't update property!", e );
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete-property/{id}")
    public ResponseEntity<ApiResponse> deleteProperty(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        ApiResponse response = propertyService.deleteProperty(propertyId)
                .orElseThrow(() -> new ResourceAlreadyInUseException("Property with id" + propertyId, "Property is being used!", ""));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookmarked-items")
    public ResponseEntity<List<PropertyDto>> getAllBookmarkedProperties(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        List<PropertyDto> properties = propertyService.getAllBookmarkedProperties(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Property with user ", currentUser.getEmail(), "Not found!"));

        return ResponseEntity.ok(properties);
    }

    @PostMapping("/bookmark/{id}")
    public ResponseEntity<ApiResponse> bookmarkProperty(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        ApiResponse response = propertyService.bookmarkProperty(currentUser.getId(), propertyId)
                .orElseThrow(() -> new ResourceAlreadyInUseException("Property with id" + propertyId, "Property does not exist!", ""));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-bookmark/{id}")
    public ResponseEntity<ApiResponse> unBookmarkProperty(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        ApiResponse response = propertyService.removeBookmark(currentUser.getId(), propertyId)
                .orElseThrow(() -> new ResourceAlreadyInUseException("Property with id" + propertyId, "Property does not exist!", ""));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rent-request")
    public ResponseEntity<ApiResponse> requestRentProperty(@CurrentUser CustomUserDetails currentUser, @RequestBody RentRequest request) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        ApiResponse response = propertyService.requestRentProperty(currentUser, request)
                .orElseThrow(() -> new DataAlreadyExistException("Property values :" + request.getPropertyId(), "Property already exist"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-rent-request/{id}")
    public ResponseEntity<ApiResponse> updateRentRequest(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long requestId, @RequestBody RentRequest request) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        ApiResponse response = propertyService.updateRentRequest(currentUser, requestId, request)
                .orElseThrow(() -> new DataAlreadyExistException("Property values :" + request.getPropertyId(), "Property already exist"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requested-items")
    public ResponseEntity<List<RentPropertyDto>> getRequestedItems(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");

        List<RentPropertyDto> propertyDtos = propertyService.getRequestedItems(currentUser)
                .orElseThrow(() -> new AppException(" Property doesn't already exist ", null));
        return ResponseEntity.ok(propertyDtos);
    }

}
