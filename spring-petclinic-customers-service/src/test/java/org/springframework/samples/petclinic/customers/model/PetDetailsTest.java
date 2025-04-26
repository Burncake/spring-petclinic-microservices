package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PetDetailsTest {

    @Test
    void constructor_shouldMapPetDataCorrectly() throws ParseException {
        // Arrange
        Owner owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("Owner");
        // owner.setId(1);

        PetType type = new PetType();
        type.setId(5);
        type.setName("Hamster");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = sdf.parse("2022-03-15");

        Pet pet = new Pet();
        pet.setId(10);
        pet.setName("Fuzzy");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        // Act
        PetDetails details = new PetDetails(pet);

        // Assert
        assertEquals(10L, details.id()); // Ensure long type matches
        assertEquals("Fuzzy", details.name());
        assertEquals("Test Owner", details.owner()); // Check concatenated name
        assertEquals(birthDate, details.birthDate());
        assertEquals(type, details.type());
    }
}