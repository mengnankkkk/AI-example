package edu.qlu.chatbot.controller;

import edu.qlu.chatbot.model.*;
import edu.qlu.chatbot.service.VoiceprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * VoiceprintController 测试类
 */
@ExtendWith(MockitoExtension.class)
class VoiceprintControllerTest {

    @Mock
    private VoiceprintService voiceprintService;

    @InjectMocks
    private VoiceprintController voiceprintController;

    private MockMvc mockMvc;
    private MockMultipartFile mockAudioFile;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(voiceprintController).build();
        mockAudioFile = new MockMultipartFile(
            "file", "test.wav", "audio/wav", "mock audio data".getBytes()
        );
    }

    @Test
    void testEnrollVoiceprint_Success() throws Exception {
        // Arrange
        VoiceprintEnrollResponse mockResponse = new VoiceprintEnrollResponse();
        mockResponse.setStatus("success");
        mockResponse.setMessage("声纹注册成功");
        mockResponse.setFeatureId("test_feature_id");
        mockResponse.setUserId(1L);
        mockResponse.setUsername("testuser");
        mockResponse.setAudioFileName("test.wav");

        when(voiceprintService.enrollVoiceprint(eq(1L), any(), eq("test info")))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/voiceprint/enroll")
                .file(mockAudioFile)
                .param("userId", "1")
                .param("featureInfo", "test info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetUserVoiceprints_Success() throws Exception {
        // Arrange
        Voiceprint voiceprint = new Voiceprint();
        voiceprint.setId(1L);
        voiceprint.setUserId(1L);
        voiceprint.setAudioFileName("test.wav");
        voiceprint.setCreatedAt(LocalDateTime.now());

        when(voiceprintService.getUserVoiceprints(1L))
            .thenReturn(Arrays.asList(voiceprint));

        // Act & Assert
        mockMvc.perform(get("/api/v1/voiceprint/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voiceprints").isArray())
                .andExpect(jsonPath("$.voiceprints[0].id").value(1));
    }

    @Test
    void testDeleteUserVoiceprint_Success() throws Exception {
        // Arrange
        when(voiceprintService.deleteUserVoiceprint(1L)).thenReturn(true);

        // Act & Assert - This will test the actual controller logic
        mockMvc.perform(delete("/api/v1/voiceprint/user/1"))
                .andExpect(status().isOk());
    }
}
