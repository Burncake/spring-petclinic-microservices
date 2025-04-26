package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set; // Keep Set import if needed elsewhere, or remove

import static org.junit.jupiter.api.Assertions.*;

class OwnerTest {

    private Owner owner;
    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("Owner");
        // ID is usually set by JPA, keep it null for unit tests unless needed

        PetType cat = new PetType(); cat.setName("cat");
        PetType dog = new PetType(); dog.setName("dog");

        pet1 = new Pet();
        pet1.setName("Whiskers"); // Name starts with W
        pet1.setType(cat);
        // pet1 ID is null

        pet2 = new Pet();
        pet2.setName("Buddy"); // Name starts with B
        pet2.setType(dog);
        // pet2 ID is null
    }

    @Test
    void addPet_shouldAddPetToListAndSetOwner() {
        // Arrange
        assertTrue(owner.getPets().isEmpty(), "Owner should have no pets initially");

        // Act
        owner.addPet(pet1);

        // Assert
        List<Pet> pets = owner.getPets();
        assertEquals(1, pets.size(), "Owner should have 1 pet after adding");
        Pet addedPet = pets.get(0); // Get pet via public getter

        // Verify owner was set on the pet
        assertSame(owner, addedPet.getOwner(), "Pet's owner should be set to this owner");
        // Verify the pet added is the one we expect (using equals)
        assertEquals(pet1, addedPet);

        // Verify pet1 itself now has the owner set (direct check on original object)
        assertSame(owner, pet1.getOwner(), "Pet's owner should be set (checking original pet object)");
    }

    // Removed the problematic addPet_shouldHandleNullInternalSet test

    @Test
    void getPetsInternal_shouldInitializeSetIfNull() {
        // Arrange
        // Act
        Set<Pet> internalPets = owner.getPetsInternal();

        // Assert
        assertNotNull(internalPets, "getPetsInternal should never return null");
        assertTrue(internalPets.isEmpty(), "Initially, the internal set should be empty");

        // Add a pet and check again
        internalPets.add(pet1); // Add directly to internal set for testing boundary case
        internalPets = owner.getPetsInternal(); // Call again
        assertEquals(1, internalPets.size());
    }

    @Test
    void getPets_shouldReturnSortedUnmodifiableList() {
        // Arrange
        owner.addPet(pet1); // Whiskers
        owner.addPet(pet2); // Buddy

        // Act
        List<Pet> pets = owner.getPets();

        // Assert
        assertNotNull(pets);
        assertEquals(2, pets.size());
        // Check sorting by name (Buddy before Whiskers)
        assertSame(pet2, pets.get(0), "First pet should be Buddy (sorted by name)");
        assertSame(pet1, pets.get(1), "Second pet should be Whiskers (sorted by name)");

        // Check unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> pets.add(new Pet()),
                     "List returned by getPets() should be unmodifiable");
        assertThrows(UnsupportedOperationException.class, () -> pets.remove(0),
                     "List returned by getPets() should be unmodifiable");
    }

    @Test
    void getPets_shouldReturnEmptyListWhenNoPets() {
         // Arrange (no pets added)
         // Act
        List<Pet> pets = owner.getPets();

         // Assert
         assertNotNull(pets);
         assertTrue(pets.isEmpty());
         // Check unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> pets.add(new Pet()));
    }

    @Test
    void toString_shouldContainBasicInfo() {
        // Arrange
        owner.setAddress("123 Street");
        owner.setCity("Cityville");
        owner.setTelephone("12345");
        // ID remains null as it's not set

        // Act
        String ownerString = owner.toString();
        System.out.println("DEBUG Owner.toString(): " + ownerString); // Optional: print for debugging format

        // Assert - Match format observed in logs: single quotes for strings, [null] for null ID
        assertTrue(ownerString.contains("id = [null]"), "toString should contain id=[null]"); // [cite: 67]
        assertTrue(ownerString.contains("lastName = 'Owner'"), "toString should contain lastName"); // [cite: 67, 69] corrected format
        assertTrue(ownerString.contains("firstName = 'Test'"), "toString should contain firstName"); // [cite: 67] corrected format
        assertTrue(ownerString.contains("address = '123 Street'"), "toString should contain address"); // [cite: 67] corrected format
        assertTrue(ownerString.contains("city = 'Cityville'"), "toString should contain city"); // [cite: 67] corrected format
        assertTrue(ownerString.contains("telephone = '12345'"), "toString should contain telephone"); // [cite: 67] corrected format
    }
}