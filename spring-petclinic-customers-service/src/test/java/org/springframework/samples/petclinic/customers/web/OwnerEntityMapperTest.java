package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.junit.jupiter.api.Assertions.*;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void map_shouldTransferDataFromRequestToOwner() {
        // Arrange
        OwnerRequest request = new OwnerRequest(
            "John",
            "Doe",
            "123 Elm Street",
            "Springfield",
            "555-1234"
        );
        Owner owner = new Owner(); // Target object

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertSame(owner, result, "Mapper should return the same owner instance");
        assertEquals("John", owner.getFirstName());
        assertEquals("Doe", owner.getLastName());
        assertEquals("123 Elm Street", owner.getAddress());
        assertEquals("Springfield", owner.getCity());
        assertEquals("555-1234", owner.getTelephone());
    }

     @Test
    void map_shouldOverwriteExistingDataInOwner() {
        // Arrange
         OwnerRequest request = new OwnerRequest(
            "Jane",
            "Smith",
            "456 Oak Avenue",
            "Shelbyville",
            "555-5678"
        );
        Owner owner = new Owner();
        owner.setFirstName("OldFirst");
        owner.setLastName("OldLast");
        owner.setAddress("Old Address");
        owner.setCity("Old City");
        owner.setTelephone("Old Phone");


        // Act
        mapper.map(owner, request);

        // Assert
        assertEquals("Jane", owner.getFirstName());
        assertEquals("Smith", owner.getLastName());
        assertEquals("456 Oak Avenue", owner.getAddress());
        assertEquals("Shelbyville", owner.getCity());
        assertEquals("555-5678", owner.getTelephone());
    }
}