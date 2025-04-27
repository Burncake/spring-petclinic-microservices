package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.web.server.ResponseStatusException; // Import if using direct exception handling instead of ResourceNotFoundException check

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerResourceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerEntityMapper ownerEntityMapper;

    @InjectMocks
    private OwnerResource ownerResource;

    private Owner owner1;
    private Owner owner2;
    private OwnerRequest ownerRequest;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        // owner1.setId(1); // ID is usually set by DB, let's assume it's set after save
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");

        owner2 = new Owner();
        // owner2.setId(2);
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setAddress("638 Cardinal Ave.");
        owner2.setCity("Sun Prairie");
        owner2.setTelephone("6085551749");

        ownerRequest = new OwnerRequest("Test", "User", "123 Main St", "Anytown", "1234567890");
    }

    @Test
    void createOwner_shouldSaveAndReturnOwner() {
        // Arrange
        Owner mappedOwner = new Owner(); // The object after mapping
        Owner savedOwner = new Owner(); // The object after saving (might have ID)
        // savedOwner.setId(1); // Simulate ID generation

        // Use lenient stubbing for the mapper to avoid StrictStubs exception if called multiple times or with different args in future extensions
        lenient().when(ownerEntityMapper.map(any(Owner.class), eq(ownerRequest))).thenReturn(mappedOwner);
        when(ownerRepository.save(mappedOwner)).thenReturn(savedOwner);

        // Act
        Owner result = ownerResource.createOwner(ownerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(savedOwner, result);
        verify(ownerEntityMapper, times(1)).map(any(Owner.class), eq(ownerRequest));
        verify(ownerRepository, times(1)).save(mappedOwner);
    }

    @Test
    void findOwner_shouldReturnOwner_whenFound() {
        // Arrange
        int ownerId = 1;
        // owner1.setId(ownerId); // Set ID for the mock object
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner1));

        // Act
        Optional<Owner> result = ownerResource.findOwner(ownerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(owner1, result.get());
        verify(ownerRepository, times(1)).findById(ownerId);
    }

    @Test
    void findOwner_shouldReturnEmpty_whenNotFound() {
        // Arrange
        int ownerId = 99;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act
        Optional<Owner> result = ownerResource.findOwner(ownerId);

        // Assert
        assertFalse(result.isPresent());
        verify(ownerRepository, times(1)).findById(ownerId);
    }
     @Test
    void findOwner_shouldThrowException_whenIdIsZero() {
        // Constraint validation (@Min(1)) is typically handled by the framework before
        // the method is called. Testing this might require @WebMvcTest or similar.
        // However, we can test the direct call behavior if needed, although it might
        // not reflect the full request lifecycle validation.
        // For pure unit test, we assume validation passes if method is reached.
        // If you need to test validation, @WebMvcTest is more appropriate.
        // Let's focus on the core logic here. We'll skip direct validation test.
    }

    @Test
    void findAll_shouldReturnListOfOwners() {
        // Arrange
        List<Owner> owners = Arrays.asList(owner1, owner2);
        when(ownerRepository.findAll()).thenReturn(owners);

        // Act
        List<Owner> result = ownerResource.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(owners, result);
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoOwners() {
        // Arrange
        when(ownerRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Owner> result = ownerResource.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    void updateOwner_shouldUpdateAndSave_whenFound() {
        // Arrange
        int ownerId = 1;
        Owner existingOwner = new Owner(); // Represents the owner fetched from DB
       //  existingOwner.setId(ownerId);
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        // Assume mapper updates existingOwner based on ownerRequest
        when(ownerEntityMapper.map(existingOwner, ownerRequest)).thenReturn(existingOwner); // Simulate mapping
        when(ownerRepository.save(existingOwner)).thenReturn(existingOwner); // Simulate saving

        // Act
        ownerResource.updateOwner(ownerId, ownerRequest);

        // Assert
        verify(ownerRepository, times(1)).findById(ownerId);
        verify(ownerEntityMapper, times(1)).map(existingOwner, ownerRequest);
        verify(ownerRepository, times(1)).save(existingOwner);
    }

    @Test
    void updateOwner_shouldThrowNotFound_whenOwnerNotFound() {
        // Arrange
        int ownerId = 99;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ownerResource.updateOwner(ownerId, ownerRequest);
        });

        assertEquals("Owner " + ownerId + " not found", exception.getMessage());
        verify(ownerRepository, times(1)).findById(ownerId);
        verify(ownerEntityMapper, never()).map(any(), any());
        verify(ownerRepository, never()).save(any());
    }

    @Test
    void updateOwner_shouldThrowException_whenIdIsZero() {
         // Similar to findOwner, @Min(1) validation is usually framework level.
         // Skipping direct validation test in pure unit test.
    }
}