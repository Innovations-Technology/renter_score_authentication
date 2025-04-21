package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.UpdatePasswordException;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.Property;
import com.iss.renterscore.authentication.model.PropertyDto;
import com.iss.renterscore.authentication.model.PropertyImage;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.payloads.UpdateRequest;
import com.iss.renterscore.authentication.service.CurrentUser;
import com.iss.renterscore.authentication.service.PropertyService;
import com.iss.renterscore.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    private final UserService userService;
    private final PropertyService propertyService;
    private final ApplicationEventPublisher applicationEventPublisher;

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

    @GetMapping("/created-properties")
    public ResponseEntity<?> getAllProperties(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) return ResponseEntity.ok(null);

        List<Property> properties = propertyService.getAllProperties(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Property with user ", currentUser.getEmail(), "Not found!"));

        return ResponseEntity.ok(properties.stream().map(PropertyDto::new).toList());

    }

    private void setImageContextPath(Property property) {

        if (property.getHeroImage() != null && !property.getHeroImage().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/" + property.getHeroImage()).toUriString();
            property.setHeroImage(imageUrl);
        }

        for (PropertyImage propertyImage : property.getImages()) {
            if (propertyImage.getImage() != null && !propertyImage.getImage().isEmpty()) {
                String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/" + propertyImage.getImage()).toUriString();
                propertyImage.setImage(imageUrl);
            }
        }

    }

    @PostMapping("/create-property")
    public ResponseEntity<?> createProperty(@CurrentUser CustomUserDetails currentUser, @RequestPart("property") PropertyRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        ApiResponse response = null;
        try {
            response = propertyService.createProperty(currentUser, request, files)
                    .orElseThrow(() -> new UpdatePasswordException("---Empty---", "No such user present"));
        } catch (IOException e) {
            throw new ResourceNotFoundException("User email", currentUser.getEmail(), "Not found!" );
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-property")
    public ResponseEntity<?> updateProperty(@CurrentUser CustomUserDetails currentUser, @RequestPart("property") UpdateRequest request, @RequestPart(value = "file", required = false) MultipartFile file) {
        ApiResponse response = null;
        try {
            response = userService.updateProfile(currentUser, request, file)
                    .orElseThrow(() -> new UpdatePasswordException("---Empty---", "No such user present"));
        } catch (IOException e) {
            throw new ResourceNotFoundException("User email", currentUser.getEmail(), "Not found!" );
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/delete-property")
    public ResponseEntity<?> deleteProperty(@CurrentUser CustomUserDetails currentUser, @RequestBody Long id) {
        ApiResponse response = null;
      /*  try {
            response = userService.findByEmail()
                    .orElseThrow(() -> new UpdatePasswordException("---Empty---", "No such user present"));
        } catch (IOException e) {
            throw new ResourceNotFoundException("User email", currentUser.getEmail(), "Not found!" );
        }*/
        return ResponseEntity.ok(response);
    }

}
