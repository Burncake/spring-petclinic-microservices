package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.customers.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetResourceUnitTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private PetResource petResource;

    private Owner owner;
    private Pet pet1;
    private PetType catType;
    private PetRequest petRequest;
    private Date birthDate;

    @BeforeEach
    void setUp() throws ParseException {
        owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("Owner");
        // owner.setId(1); // Assuming ID is set

        catType = new PetType();
        catType.setId(1);
        catType.setName("cat");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        birthDate = sdf.parse("2020-01-15");

        pet1 = new Pet();
       // pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(birthDate);
        pet1.setType(catType);
        pet1.setOwner(owner); // Associate pet with owner


        petRequest = new PetRequest(0, birthDate, "Fluffy", catType.getId()); // id 0 for creation
    }

    @Test
    void getPetTypes_shouldReturnListOfPetTypes() {
        // Arrange
        PetType dogType = new PetType();
        dogType.setId(2);
        dogType.setName("dog");
        List<PetType> types = Arrays.asList(catType, dogType);
        when(petRepository.findPetTypes()).thenReturn(types);

        // Act
        List<PetType> result = petResource.getPetTypes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(types, result);
        verify(petRepository, times(1)).findPetTypes();
    }

    @Test
    void processCreationForm_shouldAddPetToOwnerAndSave_whenOwnerFound() {
        // Arrange
        int ownerId = 1;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(petRepository.findPetTypeById(petRequest.typeId())).thenReturn(Optional.of(catType));

        // Capture the Pet object passed to save
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet savedPet = invocation.getArgument(0);
            // Simulate ID generation on save
            // savedPet.setId(99);
            return savedPet;
        });

        // Act
        Pet result = petResource.processCreationForm(petRequest, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(petRequest.name(), result.getName());
        assertEquals(petRequest.birthDate(), result.getBirthDate());
        assertEquals(catType, result.getType());
        assertEquals(owner, result.getOwner()); // Check if owner is set
        assertTrue(owner.getPetsInternal().contains(result)); // Check if pet was added to owner's internal list

        verify(ownerRepository, times(1)).findById(ownerId);
        verify(petRepository, times(1)).findPetTypeById(petRequest.typeId());
        verify(petRepository, times(1)).save(any(Pet.class));
    }
     @Test
    void processCreationForm_shouldAddPetToOwnerAndSave_whenPetTypeNotFound() {
        // Arrange
        int ownerId = 1;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        // Pet type not found
        when(petRepository.findPetTypeById(petRequest.typeId())).thenReturn(Optional.empty());

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pet result = petResource.processCreationForm(petRequest, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(petRequest.name(), result.getName());
        assertEquals(petRequest.birthDate(), result.getBirthDate());
        assertNull(result.getType()); // Type should be null as it wasn't found
        assertEquals(owner, result.getOwner());
        assertTrue(owner.getPetsInternal().contains(result));

        verify(ownerRepository, times(1)).findById(ownerId);
        verify(petRepository, times(1)).findPetTypeById(petRequest.typeId());
        verify(petRepository, times(1)).save(any(Pet.class));
    }


    @Test
    void processCreationForm_shouldThrowNotFound_whenOwnerNotFound() {
        // Arrange
        int ownerId = 99;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            petResource.processCreationForm(petRequest, ownerId);
        });

        assertEquals("Owner " + ownerId + " not found", exception.getMessage());
        verify(ownerRepository, times(1)).findById(ownerId);
        verify(petRepository, never()).findPetTypeById(anyInt());
        verify(petRepository, never()).save(any(Pet.class));
    }


    @Test
    void processUpdateForm_shouldUpdateAndSavePet_whenFound() {
        // Arrange
        int petId = 1;
        PetRequest updateRequest = new PetRequest(petId, birthDate, "Updated Name", catType.getId());
        // pet1 already has id=1 and owner set up
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet1));
        when(petRepository.findPetTypeById(updateRequest.typeId())).thenReturn(Optional.of(catType));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved pet

        // Act
        petResource.processUpdateForm(updateRequest);

        // Assert
        verify(petRepository, times(1)).findById(petId);
        verify(petRepository, times(1)).findPetTypeById(updateRequest.typeId());
        verify(petRepository, times(1)).save(pet1); // Verify save was called with the updated pet1

        // Verify the pet object was updated before save
        assertEquals(updateRequest.name(), pet1.getName());
        assertEquals(updateRequest.birthDate(), pet1.getBirthDate());
        assertEquals(catType, pet1.getType());
    }

    @Test
    void processUpdateForm_shouldThrowNotFound_whenPetNotFound() {
        // Arrange
        int petId = 99;
         PetRequest updateRequest = new PetRequest(petId, birthDate, "Updated Name", catType.getId());
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            petResource.processUpdateForm(updateRequest);
        });

        assertEquals("Pet " + petId + " not found", exception.getMessage());
        verify(petRepository, times(1)).findById(petId);
        verify(petRepository, never()).findPetTypeById(anyInt());
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void findPet_shouldReturnPetDetails_whenFound() {
        // Arrange
        int petId = 1;
       // Pet pet = setupPet(); // Reuse setup from original test if needed, or use pet1
        // pet1 setup in @BeforeEach includes owner
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet1));

        // Act
        PetDetails result = petResource.findPet(petId);

        // Assert
        assertNotNull(result);
        assertEquals(pet1.getId(), result.id());
        assertEquals(pet1.getName(), result.name());
        assertEquals(pet1.getBirthDate(), result.birthDate());
        assertEquals(pet1.getType(), result.type());
        assertEquals(owner.getFirstName() + " " + owner.getLastName(), result.owner()); // Verify owner name format

        verify(petRepository, times(1)).findById(petId);
    }

    @Test
    void findPet_shouldThrowNotFound_whenPetNotFound() {
        // Arrange
        int petId = 99;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            petResource.findPet(petId);
        });

        assertEquals("Pet " + petId + " not found", exception.getMessage());
        verify(petRepository, times(1)).findById(petId);
    }

     // Test private findPetById implicitly tested via findPet and processUpdateForm,
     // but we can add a specific test for its exception throwing if desired.
     @Test
     void findPetById_privateMethod_shouldThrowNotFound() {
         // Arrange
         int petId = 99;
         when(petRepository.findById(petId)).thenReturn(Optional.empty());

         // Act & Assert
         ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
             // Need reflection to test private method directly, which is discouraged.
             // Better to rely on public methods' tests (findPet, processUpdateForm)
             // that call this private method.
             // If testing is crucial, consider making it package-private or protected.
             // Assuming the calls from public methods cover this sufficiently.
             petResource.findPet(petId); // This will call findPetById internally
         });
          assertEquals("Pet " + petId + " not found", exception.getMessage());
     }
}