package com.victor.lamontagne_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.service.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JourneyControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JourneyService journeyService;
    private JourneyController journeyController;

    private static final String BASE_URL = "/api/journey";
    private static final String USER_ID = "user123";
    private static final String JOURNEY_ID = "journey123";

    @BeforeEach
    void setUp() {
        journeyService = mock(JourneyService.class);
        journeyController = new JourneyController(journeyService);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(journeyController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAllJourneys_ShouldReturnJourneyList() throws Exception {
        // Given
        List<JourneyDTO> mockJourneys = Arrays.asList(
                createJourneyDTO("Journey 1"),
                createJourneyDTO("Journey 2")
        );
        when(journeyService.getAllJourneys(USER_ID)).thenReturn(mockJourneys);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/user/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Journey 1"))
                .andExpect(jsonPath("$[1].title").value("Journey 2"));
    }

    @Test
    void getJourneyById_ShouldReturnJourney() throws Exception {
        // Given
        JourneyDTO mockJourney = createJourneyDTO("Test Journey");
        when(journeyService.getJourneyById(eq(JOURNEY_ID), eq(USER_ID))).thenReturn(mockJourney);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", JOURNEY_ID)
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Journey"));
    }

    @Test
    void createJourney_ShouldCreateAndReturnJourney() throws Exception {
        // Given
        JourneyDTO inputJourney = createJourneyDTO("New Journey");
        JourneyDTO createdJourney = createJourneyDTO("New Journey");
        createdJourney.setId(JOURNEY_ID);

        MockMultipartFile journeyFile = new MockMultipartFile(
                "journeyData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(inputJourney)
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "files",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(journeyService.createJourney(any(JourneyDTO.class), any(MultipartFile[].class), eq(USER_ID)))
                .thenReturn(createdJourney);

        // When & Then
        mockMvc.perform(multipart(BASE_URL)
                        .file(journeyFile)
                        .file(imageFile)
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(JOURNEY_ID))
                .andExpect(jsonPath("$.title").value("New Journey"));
    }

    @Test
    void updateJourney_ShouldUpdateAndReturnJourney() throws Exception {
        // Given
        JourneyDTO updateJourney = createJourneyDTO("Updated Journey");
        updateJourney.setId(JOURNEY_ID);

        MockMultipartFile journeyFile = new MockMultipartFile(
                "journeyData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateJourney)
        );

        MockMultipartFile[] files = new MockMultipartFile[0]; // Assuming no files are being uploaded

        when(journeyService.updateJourney(
                any(String.class),
                any(JourneyDTO.class),
                any(MultipartFile[].class),
                any(String.class)
        )).thenReturn(updateJourney);

        // When & Then
        mockMvc.perform(multipart(HttpMethod.PUT, BASE_URL)
                        .file(journeyFile)
                        .file("files", new byte[0]) // Include the files parameter
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(JOURNEY_ID));
    }

    @Test
    void deleteJourney_ShouldDeleteSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", JOURNEY_ID)
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        verify(journeyService).deleteJourney(JOURNEY_ID, USER_ID);
    }

    @Test
    void deleteFiles_ShouldDeleteFilesSuccessfully() throws Exception {
        // Given
        List<String> fileUrls = Arrays.asList(
                "https://cloudinary.com/file1.jpg",
                "https://cloudinary.com/file2.jpg"
        );

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}/files", JOURNEY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fileUrls)))
                .andExpect(status().isOk());

        verify(journeyService).deleteFiles(JOURNEY_ID, fileUrls);
    }

    private JourneyDTO createJourneyDTO(String title) {
        JourneyDTO journey = new JourneyDTO();
        journey.setTitle(title);
        return journey;
    }
}