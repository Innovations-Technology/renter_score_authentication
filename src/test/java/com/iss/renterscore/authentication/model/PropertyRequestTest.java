package com.iss.renterscore.authentication.model;

import com.iss.renterscore.authentication.payloads.PropertyRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyRequestTest {
    @Test
    void testPropertyRequestCreationAndGetters() {

        PropertyRequest request = getRequest();

        // Assert
        assertEquals("Luxury Apartment", request.getTitle());
        assertEquals("Spacious 3-bedroom apartment with sea view", request.getDescription());
        assertEquals(PropertyType.HDB, request.getPropertyType());
        assertEquals("Pool,Gym,Parking", request.getAmenities());
        assertEquals("12A", request.getBlockNo());
        assertEquals("15-03", request.getUnitNo());
        assertEquals("Beach Road", request.getStreet());
        assertEquals("123456", request.getPostalCode());
        assertEquals(3, request.getBedrooms());
        assertEquals(2, request.getBathrooms());
        assertEquals(Regions.CENTRAL_SINGAPORE, request.getRegions());
        assertEquals("01/12/2023", request.getAvailableDate());
        assertEquals(PropertyStatus.FULLY_FINISHED, request.getPropertyStatus());
        assertEquals(3500, request.getPrice());
        assertEquals("1200 sqft", request.getSize());
        assertEquals(RentType.MONTHLY, request.getRentType());
        assertEquals(PropertyState.AVAILABLE, request.getPropertyState());
        assertTrue(request.getIsHeroImageChanged());
    }

    private static PropertyRequest getRequest() {
        PropertyRequest request = new PropertyRequest();

        // Set values
        request.setTitle("Luxury Apartment");
        request.setDescription("Spacious 3-bedroom apartment with sea view");
        request.setPropertyType(PropertyType.HDB);
        request.setAmenities("Pool,Gym,Parking");
        request.setBlockNo("12A");
        request.setUnitNo("15-03");
        request.setStreet("Beach Road");
        request.setPostalCode("123456");
        request.setBedrooms(3);
        request.setBathrooms(2);
        request.setRegions(Regions.CENTRAL_SINGAPORE);
        request.setAvailableDate("01/12/2023");
        request.setPropertyStatus(PropertyStatus.FULLY_FINISHED);
        request.setPrice(3500);
        request.setSize("1200 sqft");
        request.setRentType(RentType.MONTHLY);
        request.setPropertyState(PropertyState.AVAILABLE);
        request.setIsHeroImageChanged(true);
        return request;
    }

    @Test
    void testSettersWithNullValues() {
        PropertyRequest request = getPropertyRequest();

        // Assert
        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getAmenities());
        assertNull(request.getBlockNo());
        assertNull(request.getUnitNo());
        assertNull(request.getStreet());
        assertNull(request.getPostalCode());
        assertNull(request.getBedrooms());
        assertNull(request.getBathrooms());
        assertNull(request.getRegions());
        assertNull(request.getAvailableDate());
        assertNull(request.getPropertyStatus());
        assertNull(request.getPrice());
        assertNull(request.getSize());
        assertNull(request.getRentType());
        assertNull(request.getPropertyState());
        assertNull(request.getIsHeroImageChanged());
    }

    private static PropertyRequest getPropertyRequest() {
        PropertyRequest request = new PropertyRequest();

        // Set null values
        request.setTitle(null);
        request.setDescription(null);
        request.setAmenities(null);
        request.setBlockNo(null);
        request.setUnitNo(null);
        request.setStreet(null);
        request.setPostalCode(null);
        request.setBedrooms(null);
        request.setBathrooms(null);
        request.setRegions(null);
        request.setAvailableDate(null);
        request.setPropertyStatus(null);
        request.setPrice(null);
        request.setSize(null);
        request.setRentType(null);
        request.setPropertyState(null);
        request.setIsHeroImageChanged(null);
        return request;
    }

    @Test
    void testEnumFieldHandling() {
        // Arrange
        PropertyRequest request = new PropertyRequest();

        // Set enum values
        request.setPropertyType(PropertyType.CONDO);
        request.setRegions(Regions.NORTH_EAST);
        request.setPropertyStatus(PropertyStatus.FULLY_FINISHED);
        request.setRentType(RentType.MONTHLY);
        request.setPropertyState(PropertyState.OCCUPIED);

        // Assert
        assertEquals(PropertyType.CONDO, request.getPropertyType());
        assertEquals(Regions.NORTH_EAST, request.getRegions());
        assertEquals(PropertyStatus.FULLY_FINISHED, request.getPropertyStatus());
        assertEquals(RentType.MONTHLY, request.getRentType());
        assertEquals(PropertyState.OCCUPIED, request.getPropertyState());
    }

    @Test
    void testNumericFieldBoundaries() {
        // Arrange
        PropertyRequest request = new PropertyRequest();

        // Set boundary values
        request.setBedrooms(0);
        request.setBathrooms(10);
        request.setPrice(0);
        request.setPrice(1000000);

        // Assert
        assertEquals(0, request.getBedrooms());
        assertEquals(10, request.getBathrooms());
        assertEquals(1000000, request.getPrice());
    }

    @Test
    void testBooleanField() {
        // Arrange
        PropertyRequest request = new PropertyRequest();

        // Set boolean values
        request.setIsHeroImageChanged(true);


        // Assert
        assertTrue(request.getIsHeroImageChanged());

        request.setIsHeroImageChanged(false);
        assertFalse(request.getIsHeroImageChanged());
    }

    @Test
    void testToString() {
        // Arrange
        PropertyRequest request = new PropertyRequest();
        request.setTitle("Test Property");
        request.setPrice(1000);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("Test Property"));
        assertTrue(toString.contains("1000"));
    }
}
