package org.springframework.samples.petclinic.visits.web;

import com.fasterxml.jackson.databind.ObjectMapper; // Added for POST request body
import org.junit.jupiter.api.BeforeEach; // Added for setup
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; // Added for capturing arguments
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType; // Added for content type
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections; // Added for Collections.singletonList
import java.util.Date;
import java.util.List; // Added for List import

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat; // Added for Hamcrest assertion
import static org.hamcrest.Matchers.equalTo; // Added for Hamcrest matcher
import static org.hamcrest.Matchers.is; // Added for Hamcrest matcher
import static org.mockito.ArgumentMatchers.any; // Added for any() matcher
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify; // Added for verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // Added for post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // Added for content matching

@ExtendWith(SpringExtension.class)
@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class VisitResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VisitRepository visitRepository;

    @Autowired
    ObjectMapper objectMapper; // Inject ObjectMapper for JSON conversion

    private Visit visit1;
    private Visit visit2;
    private Visit visit3;

    @BeforeEach
    void setup() {
        // Setup common visit objects for tests
        visit1 = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(111)
            .date(new Date())
            .description("Checkup 1")
            .build();
        visit2 = Visit.VisitBuilder.aVisit()
            .id(2)
            .petId(222)
            .date(new Date())
            .description("Checkup 2a")
            .build();
        visit3 = Visit.VisitBuilder.aVisit()
            .id(3)
            .petId(222)
            .date(new Date())
            .description("Checkup 2b")
            .build();
    }


    @Test
    void shouldFetchVisitsByPetIdList() throws Exception {
        // Arrange
        given(visitRepository.findByPetIdIn(asList(111, 222)))
            .willReturn(asList(visit1, visit2, visit3));

        // Act & Assert
        mvc.perform(get("/pets/visits?petId=111,222"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(3))
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[1].id").value(2))
            .andExpect(jsonPath("$.items[2].id").value(3))
            .andExpect(jsonPath("$.items[0].petId").value(111))
            .andExpect(jsonPath("$.items[1].petId").value(222))
            .andExpect(jsonPath("$.items[2].petId").value(222));

        // Verify interaction
        verify(visitRepository).findByPetIdIn(asList(111, 222));
    }

    @Test
    void shouldFetchVisitsForASinglePet() throws Exception {
        // Arrange
        int targetPetId = 222;
        given(visitRepository.findByPetId(targetPetId))
            .willReturn(asList(visit2, visit3)); // Return visits only for pet 222

        // Act & Assert
        mvc.perform(get("/owners/*/pets/{petId}/visits", targetPetId)) // Use path variable
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2)) // Expecting a list directly
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[1].id").value(3))
            .andExpect(jsonPath("$[0].petId").value(targetPetId))
            .andExpect(jsonPath("$[1].petId").value(targetPetId));

        // Verify interaction
        verify(visitRepository).findByPetId(targetPetId);
    }

    @Test
    void shouldCreateVisit() throws Exception {
        // Arrange
        int petIdToAssociate = 333;
        Visit visitToCreate = Visit.VisitBuilder.aVisit()
                // ID is typically null before saving
                .date(new Date())
                .description("New visit description")
                .petId(0) // Pet ID in the request body might be ignored/overwritten by path variable
                .build();

        Visit savedVisit = Visit.VisitBuilder.aVisit()
                .id(99) // Assume DB assigns an ID
                .date(visitToCreate.getDate())
                .description(visitToCreate.getDescription())
                .petId(petIdToAssociate) // Pet ID should be set from path variable
                .build();

        // Mock the save operation
        // Use ArgumentCaptor to check the object passed to save()
        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        given(visitRepository.save(visitCaptor.capture())).willReturn(savedVisit);

        // Act & Assert
        mvc.perform(post("/owners/*/pets/{petId}/visits", petIdToAssociate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visitToCreate))) // Send visit data as JSON
            .andExpect(status().isCreated()) // Expect HTTP 201 Created
            .andExpect(jsonPath("$.id").value(savedVisit.getId()))
            .andExpect(jsonPath("$.petId").value(petIdToAssociate)) // Verify petId is from path
            .andExpect(jsonPath("$.description").value(savedVisit.getDescription()));

        // Verify interaction and captured argument
        verify(visitRepository).save(any(Visit.class));
        Visit capturedVisit = visitCaptor.getValue();
        assertThat(capturedVisit.getPetId(), is(equalTo(petIdToAssociate))); // Check petId was set correctly before save
        assertThat(capturedVisit.getDescription(), is(equalTo(visitToCreate.getDescription())));
    }

     @Test
    void shouldReturnEmptyListForUnknownPetId() throws Exception {
        // Arrange
        int unknownPetId = 999;
        given(visitRepository.findByPetId(unknownPetId)).willReturn(Collections.emptyList());

        // Act & Assert
        mvc.perform(get("/owners/*/pets/{petId}/visits", unknownPetId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0)); // Expect an empty JSON array

        // Verify interaction
        verify(visitRepository).findByPetId(unknownPetId);
    }

     @Test
    void shouldReturnEmptyVisitsForUnknownPetIdList() throws Exception {
        // Arrange
        List<Integer> unknownPetIds = asList(888, 999);
        given(visitRepository.findByPetIdIn(unknownPetIds)).willReturn(Collections.emptyList());

        // Act & Assert
        mvc.perform(get("/pets/visits?petId=888,999"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items.length()").value(0)); // Expect items array to be empty

        // Verify interaction
        verify(visitRepository).findByPetIdIn(unknownPetIds);
    }

    @Test
    void createVisitShouldReturnBadRequestForInvalidData() throws Exception {
         // Arrange - Create a visit object that might fail validation (e.g., missing fields if @NotNull was present, or invalid format)
         // In this case, Visit has @Size(max=8192) on description, let's try invalid petId in path
         int invalidPetId = 0; // @Min(1) constraint on path variable
         Visit visitToCreate = Visit.VisitBuilder.aVisit()
                .date(new Date())
                .description("Valid Description")
                .build();

         // Act & Assert
         mvc.perform(post("/owners/*/pets/{petId}/visits", invalidPetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visitToCreate)))
             .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request due to path variable constraint violation

         // Verify repository was NOT called
         verify(visitRepository, org.mockito.Mockito.never()).save(any(Visit.class));
    }
}