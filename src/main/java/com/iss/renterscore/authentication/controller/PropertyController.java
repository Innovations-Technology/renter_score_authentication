package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.AppException;
import com.iss.renterscore.authentication.exceptions.DataAlreadyExistException;
import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.Property;
import com.iss.renterscore.authentication.model.PropertyDto;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.service.CurrentUser;
import com.iss.renterscore.authentication.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    private final PropertyService propertyService;

    @GetMapping(value = "/hello-auth")
    public ResponseEntity<String> helloGreeting() {

        return ResponseEntity.ok()
                .body("<h1>Welcome to Renter Score Application</h1>" +
                        "<br /><br /><p><h3>This application is to enhance the Properties' solutions. </h3></p>");
    }

    @GetMapping("/properties")
    public ResponseEntity<?> getAllProperties() {

        List<Property> properties = propertyService.getAllProperties()
                .orElseThrow(() -> new ResourceNotFoundException(" All Property ", "", "Not found!"));

        return ResponseEntity.ok(properties.stream().map(PropertyDto::new).toList());
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getPropertyDetails(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        if (currentUser == null) return ResponseEntity.ok(null);

        Property properties = propertyService.getPropertyDetails(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id ", propertyId + "", "Not found!"));

        return ResponseEntity.ok(new PropertyDto(properties));
    }

    @GetMapping("/created-properties")
    public ResponseEntity<?> getAllProperties(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) return ResponseEntity.ok(null);

        List<Property> properties = propertyService.getAllProperties(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Property with user ", currentUser.getEmail(), "Not found!"));

        return ResponseEntity.ok(properties.stream().map(PropertyDto::new).toList());

    }

    @PostMapping("/create-property")
    public ResponseEntity<?> createProperty(@CurrentUser CustomUserDetails currentUser, @RequestPart("property") PropertyRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
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
    public ResponseEntity<?> updateProperty(@CurrentUser CustomUserDetails currentUser,  @RequestPart("id") Long propertyId, @RequestPart("property") PropertyRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
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
    public ResponseEntity<?> deleteProperty(@CurrentUser CustomUserDetails currentUser, @PathVariable("id") Long propertyId) {
        ApiResponse response = propertyService.deleteProperty(currentUser, propertyId)
                .orElseThrow(() -> new ResourceAlreadyInUseException("Property with id" + propertyId, "Property is being used!", ""));
        return ResponseEntity.ok(response);
    }

}
