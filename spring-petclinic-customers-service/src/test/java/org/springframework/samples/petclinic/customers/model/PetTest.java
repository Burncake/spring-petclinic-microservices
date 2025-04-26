package org.springframework.samples.petclinic.customers.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private SimpleDateFormat sdf;


    @BeforeEach
    void setUp() throws ParseException {
        // Use a locale that won't cause date format issues in toString() if system default varies
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        date1 = sdf.parse("2020-01-01");
        date2 = sdf.parse("2021-02-02");

        owner1 = new Owner(); owner1.setFirstName("Owner1"); owner1.setLastName("LN1");
        owner2 = new Owner(); owner2.setFirstName("Owner2"); owner2.setLastName("LN2");

        type1 = new PetType(); type1.setName("Type1"); type1.setId(1);
        type2 = new PetType(); type2.setName("Type2"); type2.setId(2);

        // Create pet1 with null ID initially for some tests
        pet1 = createPet(null, "Pet1", date1, type1, owner1);
        // Create pet2 identical to pet1 for equals testing
        pet2 = createPet(null, "Pet1", date1, type1, owner1);
    }

    private Pet createPet(Integer id, String name, Date birthDate, PetType type, Owner owner) {
        Pet pet = new Pet();
        pet.setId(id); // Allow setting ID here
        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);
        return pet;
    }

    // Optional: Keep if EqualsVerifier dependency was added and works
    /*
    @Test
    void equalsHashCodeContracts() {
         EqualsVerifier.forClass(Pet.class)
              // Relax check for JPA entities if needed, owner/type might be proxies
              // .suppress(Warning.SURROGATE_KEY)
              // .suppress(Warning.STRICT_INHERITANCE) // If Pet might be subclassed
             .verify();
    }
    */

    // Manual equals and hashCode tests
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
         Pet pet3 = createPet(null, "Pet1", date1, type1, owner1); // Identical
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
    void equals_shouldReturnFalseWhenIdDifferentButNotNull() {
         pet1.setId(1); // Set IDs
         pet2.setId(2);
         assertNotEquals(pet1, pet2);
    }

     @Test
     void equals_shouldReturnFalseWhenOneIdIsNull() {
          pet1.setId(1); // Set ID for one
          // pet2 ID is null
          assertNotEquals(pet1, pet2);
          assertNotEquals(pet2, pet1);
     }


    @Test
    void equals_shouldReturnTrueWhenIdSameAndSet() {
         pet1.setId(1); // Set IDs
         pet2.setId(1);
         // Rest of the fields are already equal from setup
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
         // Ensure types themselves are different according to their equals/hashCode
         type2.setId(2); // Make sure IDs differ if type.equals relies on ID
         pet2.setType(type2);
         assertNotEquals(pet1, pet2);
    }
     @Test
    void equals_shouldReturnFalseWhenOwnerDifferent() {
        // Ensure owners themselves are different according to their equals/hashCode,
        // or simply different instances if equals is not overridden for Owner.
         owner2.setId(2); // Give owner2 a different ID if Owner.equals uses it
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
        // pet1 and pet2 are created equal (null IDs)
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

        // Act
        String petString = pet1.toString();
        System.out.println("DEBUG Pet.toString(): " + petString); // Optional: print for debugging format

        // Assert - Use contains, be mindful of potential formatting like brackets by ToStringCreator
        // Also check for the specific date format used by ToStringCreator/Date.toString()
        assertTrue(petString.contains("id = [5]"), "toString should contain id=5");
        assertTrue(petString.contains("name = [Pet1]"), "toString should contain name=Pet1");
        // Date format can be tricky, check contains part of it or use a more robust check if needed
        assertTrue(petString.contains("birthDate = ["), "toString should contain birthDate prefix"); // Check prefix
        // Check for year, month, day might be safer than exact full string
        assertTrue(petString.contains("2020"), "toString should contain birth year");

        assertTrue(petString.contains("type = [Type1]"), "toString should contain type=Type1");
        assertTrue(petString.contains("ownerFirstname = [Owner1]"), "toString should contain ownerFirstname=Owner1");
        assertTrue(petString.contains("ownerLastname = [LN1]"), "toString should contain ownerLastname=LN1");
    }
}