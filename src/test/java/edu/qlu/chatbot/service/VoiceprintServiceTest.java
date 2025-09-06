package edu.qlu.chatbot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import edu.qlu.chatbot.config.VoiceprintConfig;
import edu.qlu.chatbot.mapper.UserMapper;
import edu.qlu.chatbot.mapper.VoiceprintMapper;
import edu.qlu.chatbot.mapper.VoiceprintIdentificationLogMapper;
import edu.qlu.chatbot.model.User;
import edu.qlu.chatbot.model.Voiceprint;
import edu.qlu.chatbot.model.VoiceprintEnrollResponse;
import edu.qlu.chatbot.model.VoiceprintIdentificationResponse;
import edu.qlu.chatbot.service.IFlytekVoiceprintClient;
import edu.qlu.chatbot.service.IFlytekApiException;

@ExtendWith(MockitoExtension.class)
class VoiceprintServiceTest {

    @Mock
    private VoiceprintConfig voiceprintConfig;

    @Mock
    private IFlytekVoiceprintClient iFlytekVoiceprintClient;

    @Mock
    private AudioProcessingService audioProcessingService;

    @Mock
    private VoiceprintMapper voiceprintMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private VoiceprintIdentificationLogMapper logMapper;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private HttpServletRequest mockRequest;

    @InjectMocks
    private VoiceprintService voiceprintService;

    private User testUser;
    private Voiceprint testVoiceprint;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setIsActive(true);
        
        testVoiceprint = new Voiceprint();
        testVoiceprint.setId(1L);
        testVoiceprint.setUserId(1L);
        testVoiceprint.setIflytekFeatureId("test_feature_id");
        testVoiceprint.setAudioFileName("test.wav");
    }

    @Test
    void testEnrollVoiceprint_Success() throws Exception {
        // Arrange
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(mockFile.getOriginalFilename()).thenReturn("test.wav");
        when(audioProcessingService.processAudioFile(any())).thenReturn("base64audio");
        when(voiceprintMapper.existsByUserId(1L)).thenReturn(false);
        when(iFlytekVoiceprintClient.addAudioFeature(any(), any(), any(), any()))
            .thenAnswer(invocation -> {
                String featureId = invocation.getArgument(1);
                return Map.of("featureId", featureId);
            });
        when(voiceprintMapper.insert(any(Voiceprint.class))).thenReturn(1);

        // Act
        VoiceprintEnrollResponse response = voiceprintService.enrollVoiceprint(1L, mockFile, "test info");

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test.wav", response.getAudioFileName());
        verify(userMapper).findById(1L);
        verify(iFlytekVoiceprintClient).addAudioFeature(any(), any(), any(), any());
        verify(voiceprintMapper).insert(any(Voiceprint.class));
    }

    @Test
    void testIdentifyVoiceprint_Success() throws Exception {
        // Arrange
        when(audioProcessingService.processAudioFile(any())).thenReturn("base64audio");
        when(iFlytekVoiceprintClient.searchByAudioFeature(any(), any(), anyInt()))
            .thenReturn(Map.of("scoreList", List.of(Map.of("featureId", "test_feature_id", "score", 0.95))));
        when(voiceprintMapper.findByFeatureIdWithUser("test_feature_id"))
            .thenReturn(testVoiceprint);
        when(voiceprintMapper.updateIdentificationStats(any(), any())).thenReturn(1);
        when(mockRequest.getHeader("User-Agent")).thenReturn("Test Agent");
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(mockRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        testVoiceprint.setUser(testUser);

        // Act
        VoiceprintIdentificationResponse response = voiceprintService.identifyVoiceprint(mockFile, mockRequest);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getResults());
        assertEquals(1, response.getResults().size());
        verify(iFlytekVoiceprintClient).searchByAudioFeature(any(), any(), anyInt());
        verify(voiceprintMapper).findByFeatureIdWithUser("test_feature_id");
    }

    @Test
    void testDeleteVoiceprint_Success() throws Exception {
        // Arrange
        when(voiceprintMapper.findActiveByUserId(1L)).thenReturn(Arrays.asList(testVoiceprint));
        when(iFlytekVoiceprintClient.deleteAudioFeature(any(), any())).thenReturn(Map.of("result", "success"));
        when(voiceprintMapper.softDeleteByFeatureId(any())).thenReturn(1);

        // Act
        boolean result = voiceprintService.deleteUserVoiceprint(1L);

        // Assert
        assertTrue(result);
        verify(voiceprintMapper).findActiveByUserId(1L);
        verify(iFlytekVoiceprintClient).deleteAudioFeature(any(), any());
        verify(voiceprintMapper).softDeleteByFeatureId(any());
    }

    @Test
    void testGetUserVoiceprints_Success() {
        // Arrange
        when(voiceprintMapper.findActiveByUserId(1L)).thenReturn(Arrays.asList(testVoiceprint));

        // Act
        List<Voiceprint> voiceprints = voiceprintService.getUserVoiceprints(1L);

        // Assert
        assertNotNull(voiceprints);
        assertEquals(1, voiceprints.size());
        assertEquals(testVoiceprint.getIflytekFeatureId(), voiceprints.get(0).getIflytekFeatureId());
        verify(voiceprintMapper).findActiveByUserId(1L);
    }

    @Test
    void testEnrollVoiceprint_UserNotFound() throws Exception {
        // Arrange
        when(userMapper.findById(1L)).thenReturn(null);

        // Act
        VoiceprintEnrollResponse response = voiceprintService.enrollVoiceprint(1L, mockFile, "test info");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertTrue(response.getMessage().contains("用户不存在"));
        verify(userMapper).findById(1L);
        verify(iFlytekVoiceprintClient, never()).addAudioFeature(any(), any(), any(), any());
    }
}
