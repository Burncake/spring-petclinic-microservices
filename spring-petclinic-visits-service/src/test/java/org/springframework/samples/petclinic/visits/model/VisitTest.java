package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Visit}.
 */
class VisitTest {

    @Test
    void testVisitCreationWithBuilder() {
        // Arrange
        Integer expectedId = 1;
        Date expectedDate = new Date();
        String expectedDescription = "Regular checkup";
        int expectedPetId = 5;

        // Act
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(expectedId)
            .date(expectedDate)
            .description(expectedDescription)
            .petId(expectedPetId)
            .build();

        // Assert
        assertEquals(expectedId, visit.getId(), "ID should match");
        // Use getTime() for accurate Date comparison as direct equals can be tricky
        assertEquals(expectedDate.getTime(), visit.getDate().getTime(), "Date should match");
        assertEquals(expectedDescription, visit.getDescription(), "Description should match");
        assertEquals(expectedPetId, visit.getPetId(), "Pet ID should match");
    }

    @Test
    void testSettersAndGetters() {
         // Arrange
        Visit visit = new Visit();
        Integer newId = 2;
        Date newDate = new Date(System.currentTimeMillis() + 10000); // Slightly different date
        String newDescription = "Vaccination";
        int newPetId = 10;

        // Act
        visit.setId(newId);
        visit.setDate(newDate);
        visit.setDescription(newDescription);
        visit.setPetId(newPetId);

        // Assert
        assertEquals(newId, visit.getId(), "ID should be updatable via setter");
        assertEquals(newDate.getTime(), visit.getDate().getTime(), "Date should be updatable via setter");
        assertEquals(newDescription, visit.getDescription(), "Description should be updatable via setter");
        assertEquals(newPetId, visit.getPetId(), "Pet ID should be updatable via setter");
    }

     @Test
    void testDefaultDate() {
        // Arrange & Act
        Visit visit = new Visit(); // Date should be set to 'now' by default

        // Assert
        assertNotNull(visit.getDate(), "Default date should not be null");
        // Check if the default date is close to the current time
        assertTrue(Math.abs(System.currentTimeMillis() - visit.getDate().getTime()) < 1000,
                   "Default date should be close to current time");
    }

    // Note: equals() and hashCode() are not explicitly implemented in Visit.java,
    // so testing them would rely on default Object behavior (identity comparison).
    // toString() is also not implemented. No specific tests added for these.

    // Note: We are testing setId here because the class provides it.
    // However, in a real JPA context with @GeneratedValue, setting the ID manually
    // before saving might be ignored or overwritten by the persistence provider.
    // This test validates the *class* behavior, not the JPA behavior.
}