package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {

    private Pet pet1;
    private Pet pet2;
    private Owner owner1;
    private Owner owner2;
    private PetType type1;
    private PetType type2;
    private Date date1;
    private Date date2;

     @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date1 = sdf.parse("2020-01-01");
        date2 = sdf.parse("2021-02-02");

        owner1 = new Owner(); owner1.setFirstName("Owner1"); // Set distinguishing info
        owner2 = new Owner(); owner2.setFirstName("Owner2");

        type1 = new PetType(); type1.setName("Type1"); type1.setId(1);
        type2 = new PetType(); type2.setName("Type2"); type2.setId(2);

        pet1 = createPet(1, "Pet1", date1, type1, owner1);
        pet2 = createPet(1, "Pet1", date1, type1, owner1); // Identical to pet1 for equals testing
    }

    private Pet createPet(Integer id, String name, Date birthDate, PetType type, Owner owner) {
        Pet pet = new Pet();
       // pet.setId(id); // Let ID be null initially unless testing specific ID equality
        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);
        return pet;
    }

    // If you add the equalsverifier dependency, this is a great way to test:
    // <dependency>
    //     <groupId>nl.jqno.equalsverifier</groupId>
    //     <artifactId>equalsverifier</artifactId>
    //     <version>3.15.2</version> //     <scope>test</scope>
    // </dependency>
    /*
    @Test
    void equalsHashCodeContracts() {
        // Note: EqualsVerifier might struggle with JPA entities, especially lazy loading or complex relationships.
        // If using bidirectional relationships (like Owner<->Pet), you might need specific configuration.
        // Here, Pet -> Owner is unidirectional from Pet's equals perspective (Owner reference).
        // Pet -> PetType is also unidirectional.
         EqualsVerifier.forClass(Pet.class)
             // Suppress warnings related to JPA/Mutability if necessary
             // .suppress(Warning.NONFINAL_FIELDS, Warning.SURROGATE_KEY)
             .verify();
    }
    */

     // Manual equals and hashCode tests if EqualsVerifier is not used or has issues
    @Test
    void equals_shouldBeReflexive() {
        assertEquals(pet1, pet1);
    }

    @Test
    void equals_shouldBeSymmetric() {
         assertEquals(pet1, pet2);
         assertEquals(pet2, pet1);
    }

    @Test
    void equals_shouldBeTransitive() {
         Pet pet3 = createPet(1, "Pet1", date1, type1, owner1); // Identical to pet1 and pet2
         assertEquals(pet1, pet2);
         assertEquals(pet2, pet3);
         assertEquals(pet1, pet3);
    }

     @Test
    void equals_shouldBeConsistent() {
         assertEquals(pet1, pet2);
         // No changes made...
         assertEquals(pet1, pet2);
    }

     @Test
    void equals_shouldReturnFalseForNull() {
         assertNotEquals(pet1, null);
    }

     @Test
    void equals_shouldReturnFalseForDifferentClass() {
         assertNotEquals(pet1, new Object());
    }

     @Test
    void equals_shouldReturnFalseWhenIdDifferent() {
         pet1.setId(1); // Now set IDs
         pet2.setId(2);
         assertNotEquals(pet1, pet2);
    }
      @Test
    void equals_shouldReturnTrueWhenIdSameAndSet() {
         pet1.setId(1); // Now set IDs
         pet2.setId(1);
         assertEquals(pet1, pet2);
    }

     @Test
    void equals_shouldReturnFalseWhenNameDifferent() {
         pet2.setName("DifferentName");
         assertNotEquals(pet1, pet2);
    }
     @Test
    void equals_shouldReturnFalseWhenBirthDateDifferent() {
         pet2.setBirthDate(date2);
         assertNotEquals(pet1, pet2);
    }
     @Test
    void equals_shouldReturnFalseWhenTypeDifferent() {
         pet2.setType(type2);
         assertNotEquals(pet1, pet2);
    }
     @Test
    void equals_shouldReturnFalseWhenOwnerDifferent() {
        // Need to ensure Owner has a proper equals/hashCode or rely on object identity difference
         pet2.setOwner(owner2);
         assertNotEquals(pet1, pet2);
    }
     @Test
    void hashCode_shouldBeConsistent() {
         int initialHashCode = pet1.hashCode();
         // No changes made...
         assertEquals(initialHashCode, pet1.hashCode());
    }

     @Test
    void hashCode_shouldBeSameForEqualObjects() {
        // pet1 and pet2 are created equal initially
         assertEquals(pet1.hashCode(), pet2.hashCode());
         // Set IDs to be equal
         pet1.setId(1);
         pet2.setId(1);
          assertEquals(pet1.hashCode(), pet2.hashCode());
    }

    @Test
    void toString_shouldContainBasicInfo() {
        // Arrange
        pet1.setId(5); // Set id for test
        // owner1 and type1 already set in setup

        // Act
        String petString = pet1.toString();

        // Assert
        assertTrue(petString.contains("id=5"));
        assertTrue(petString.contains("name=Pet1"));
        assertTrue(petString.contains("birthDate=" + date1)); // Date format might vary
        assertTrue(petString.contains("type=Type1"));
        assertTrue(petString.contains("ownerFirstname=Owner1"));
        assertTrue(petString.contains("ownerLastname=null")); // Owner last name was not set
    }
}