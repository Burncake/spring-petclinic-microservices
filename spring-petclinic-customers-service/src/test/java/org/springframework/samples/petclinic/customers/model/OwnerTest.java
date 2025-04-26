package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwnerTest {

    private Owner owner;
    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setUp() {
        owner = new Owner();
       // owner.setId(1);
        owner.setFirstName("Test");
        owner.setLastName("Owner");

        PetType cat = new PetType(); cat.setName("cat");
        PetType dog = new PetType(); dog.setName("dog");

        pet1 = new Pet();
       // pet1.setId(1);
        pet1.setName("Whiskers"); // Name starts with W
        pet1.setType(cat);

        pet2 = new Pet();
       // pet2.setId(2);
        pet2.setName("Buddy"); // Name starts with B
        pet2.setType(dog);
    }

    @Test
    void addPet_shouldAddPetToInternalSetAndSetOwner() {
        // Arrange
        assertTrue(owner.getPetsInternal().isEmpty(), "Internal pet set should be empty initially");

        // Act
        owner.addPet(pet1);

        // Assert
        assertEquals(1, owner.getPetsInternal().size(), "Internal pet set should have 1 pet");
        assertTrue(owner.getPetsInternal().contains(pet1), "Internal set should contain the added pet");
        assertSame(owner, pet1.getOwner(), "Pet's owner should be set to this owner");
    }

    @Test
    void addPet_shouldHandleNullInternalSet() {
        // Arrange
        // Force internal set to null (though constructor should prevent this)
        // This test case might be redundant if getPetsInternal() always initializes.
        // We can test getPetsInternal directly.

        // Act
        owner.addPet(pet1); // This implicitly calls getPetsInternal()

        // Assert (same as previous test)
        assertEquals(1, owner.getPetsInternal().size());
        assertTrue(owner.getPetsInternal().contains(pet1));
        assertSame(owner, pet1.getOwner());
    }

    @Test
    void getPetsInternal_shouldInitializeSetIfNull() {
        // Arrange
        // Ensure pets set is null initially (might require reflection or specific constructor if possible,
        // otherwise trust the implementation detail or test through addPet)
        // Let's assume the default state is null or an empty set is created.

        // Act
        Set<Pet> internalPets = owner.getPetsInternal();

        // Assert
        assertNotNull(internalPets, "getPetsInternal should never return null");
        assertTrue(internalPets.isEmpty(), "Initially, the internal set should be empty");

        // Add a pet and check again
        owner.getPetsInternal().add(pet1); // Add directly to internal set for testing
        internalPets = owner.getPetsInternal();
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
        assertThrows(UnsupportedOperationException.class, () -> {
            pets.add(new Pet());
        }, "List returned by getPets() should be unmodifiable");
         assertThrows(UnsupportedOperationException.class, () -> {
            pets.remove(0);
        }, "List returned by getPets() should be unmodifiable");
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
        assertThrows(UnsupportedOperationException.class, () -> {
            pets.add(new Pet());
        });
    }

    @Test
    void toString_shouldContainBasicInfo() {
         // Arrange
         owner.setAddress("123 Street");
         owner.setCity("Cityville");
         owner.setTelephone("12345");
        // owner.setId(99); // Set ID for toString

        // Act
        String ownerString = owner.toString();

        // Assert
        assertTrue(ownerString.contains("id=null")); // ID is null as not set by JPA here
        assertTrue(ownerString.contains("lastName=Owner"));
        assertTrue(ownerString.contains("firstName=Test"));
        assertTrue(ownerString.contains("address=123 Street"));
        assertTrue(ownerString.contains("city=Cityville"));
        assertTrue(ownerString.contains("telephone=12345"));
    }
}