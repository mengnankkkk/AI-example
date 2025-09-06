package edu.qlu.chatbot.service;

import edu.qlu.chatbot.config.VoiceprintConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * 音频处理服务
 * 负责音频文件的格式转换、参数设置和Base64编码
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Service
public class AudioProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioProcessingService.class);
    
    private final VoiceprintConfig config;
    
    public AudioProcessingService(VoiceprintConfig config) {
        this.config = config;
    }
    
    /**
     * 处理上传的音频文件
     * 将任意格式的音频转换为符合讯飞API要求的格式并进行Base64编码
     * 
     * @param file 上传的音频文件
     * @return Base64编码的音频字符串
     * @throws AudioProcessingException 音频处理异常
     */
    public String processAudioFile(MultipartFile file) throws AudioProcessingException {
        if (file == null || file.isEmpty()) {
            throw new AudioProcessingException("音频文件不能为空");
        }
        
        // 验证文件大小
        validateFileSize(file);
        
        // 验证文件格式
        validateFileFormat(file);
        
        try {
            logger.info("开始处理音频文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
            
            // 读取音频文件
            byte[] audioBytes = file.getBytes();
            
            // 转换音频格式
            byte[] processedAudio = convertAudioFormat(audioBytes, file.getOriginalFilename());
            
            // Base64编码
            String base64Audio = Base64.getEncoder().encodeToString(processedAudio);
            
            logger.info("音频处理完成: 原始大小={} bytes, 处理后大小={} bytes, Base64长度={}", 
                       audioBytes.length, processedAudio.length, base64Audio.length());
            
            return base64Audio;
            
        } catch (Exception e) {
            logger.error("音频处理失败: {}", file.getOriginalFilename(), e);
            throw new AudioProcessingException("音频处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理字节数组音频数据
     * 
     * @param audioBytes 音频字节数组
     * @param fileName 文件名（用于日志）
     * @return Base64编码的音频字符串
     * @throws AudioProcessingException 音频处理异常
     */
    public String processAudioBytes(byte[] audioBytes, String fileName) throws AudioProcessingException {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new AudioProcessingException("音频数据不能为空");
        }
        
        try {
            logger.info("开始处理音频字节数据: {}, 大小: {} bytes", fileName, audioBytes.length);
            
            // 转换音频格式
            byte[] processedAudio = convertAudioFormat(audioBytes, fileName);
            
            // Base64编码
            String base64Audio = Base64.getEncoder().encodeToString(processedAudio);
            
            logger.info("音频处理完成: 原始大小={} bytes, 处理后大小={} bytes", 
                       audioBytes.length, processedAudio.length);
            
            return base64Audio;
            
        } catch (Exception e) {
            logger.error("音频处理失败: {}", fileName, e);
            throw new AudioProcessingException("音频处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证文件大小
     */
    private void validateFileSize(MultipartFile file) throws AudioProcessingException {
        long maxSize = config.getAudio().getMaxFileSizeInBytes();
        if (file.getSize() > maxSize) {
            throw new AudioProcessingException(
                String.format("文件大小超出限制: %d bytes > %d bytes", file.getSize(), maxSize));
        }
    }
    
    /**
     * 验证文件格式
     */
    private void validateFileFormat(MultipartFile file) throws AudioProcessingException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new AudioProcessingException("文件名不能为空");
        }
        
        String[] allowedFormats = config.getAudio().getAllowedFormatsArray();
        String fileExtension = getFileExtension(fileName).toLowerCase();
        
        boolean isValidFormat = Arrays.stream(allowedFormats)
            .anyMatch(format -> format.trim().toLowerCase().equals(fileExtension));
        
        if (!isValidFormat) {
            throw new AudioProcessingException(
                String.format("不支持的音频格式: %s，支持的格式: %s", 
                            fileExtension, String.join(", ", allowedFormats)));
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    /**
     * 转换音频格式
     * 将音频转换为16kHz采样率、16bit位深、单声道的WAV格式
     */
    private byte[] convertAudioFormat(byte[] audioBytes, String fileName) throws Exception {
        // 尝试使用Java Sound API处理音频
        try {
            return convertWithJavaSound(audioBytes);
        } catch (Exception e) {
            logger.warn("Java Sound API处理失败，尝试简单处理: {}", e.getMessage());
            // 如果Java Sound API处理失败，直接返回原始数据
            // 在实际生产环境中，可以集成FFmpeg或其他音频处理库
            return audioBytes;
        }
    }
    
    /**
     * 使用Java Sound API转换音频格式
     */
    private byte[] convertWithJavaSound(byte[] audioBytes) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
        
        // 尝试获取音频输入流
        AudioInputStream originalStream;
        try {
            originalStream = AudioSystem.getAudioInputStream(inputStream);
        } catch (UnsupportedAudioFileException e) {
            logger.warn("不支持的音频文件格式，使用原始数据");
            return audioBytes;
        }
        
        AudioFormat originalFormat = originalStream.getFormat();
        logger.debug("原始音频格式: 采样率={}, 声道={}, 位深={}", 
                    originalFormat.getSampleRate(), originalFormat.getChannels(), originalFormat.getSampleSizeInBits());
        
        // 定义目标格式：16kHz, 16bit, 单声道, PCM编码
        AudioFormat targetFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            config.getAudio().getTargetSampleRate(),  // 16000Hz
            config.getAudio().getTargetBitDepth(),    // 16bit
            config.getAudio().getTargetChannels(),    // 1 channel (mono)
            config.getAudio().getTargetChannels() * config.getAudio().getTargetBitDepth() / 8, // frame size
            config.getAudio().getTargetSampleRate(),  // frame rate
            false  // little endian
        );
        
        logger.debug("目标音频格式: 采样率={}, 声道={}, 位深={}", 
                    targetFormat.getSampleRate(), targetFormat.getChannels(), targetFormat.getSampleSizeInBits());
        
        // 检查是否需要转换
        if (AudioSystem.isConversionSupported(targetFormat, originalFormat)) {
            // 进行格式转换
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
            
            // 读取转换后的音频数据
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = convertedStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            convertedStream.close();
            originalStream.close();
            
            byte[] convertedBytes = outputStream.toByteArray();
            logger.info("音频格式转换成功: 原始大小={} bytes, 转换后大小={} bytes", 
                       audioBytes.length, convertedBytes.length);
            
            return convertedBytes;
        } else {
            logger.warn("不支持的音频格式转换，使用原始数据");
            originalStream.close();
            return audioBytes;
        }
    }
    
    /**
     * 获取音频文件信息
     */
    public AudioFileInfo getAudioFileInfo(MultipartFile file) throws AudioProcessingException {
        try {
            byte[] audioBytes = file.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
            AudioFormat format = audioStream.getFormat();
            
            long frames = audioStream.getFrameLength();
            double durationInSeconds = frames / format.getFrameRate();
            
            audioStream.close();
            
            return new AudioFileInfo(
                file.getOriginalFilename(),
                file.getSize(),
                format.getSampleRate(),
                format.getChannels(),
                format.getSampleSizeInBits(),
                durationInSeconds
            );
        } catch (Exception e) {
            logger.warn("无法获取音频文件信息: {}", e.getMessage());
            return new AudioFileInfo(
                file.getOriginalFilename(),
                file.getSize(),
                0, 0, 0, 0
            );
        }
    }
    
    /**
     * 音频文件信息类
     */
    public static class AudioFileInfo {
        private final String fileName;
        private final long fileSize;
        private final float sampleRate;
        private final int channels;
        private final int bitDepth;
        private final double duration;
        
        public AudioFileInfo(String fileName, long fileSize, float sampleRate, 
                           int channels, int bitDepth, double duration) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.bitDepth = bitDepth;
            this.duration = duration;
        }
        
        // Getter方法
        public String getFileName() { return fileName; }
        public long getFileSize() { return fileSize; }
        public float getSampleRate() { return sampleRate; }
        public int getChannels() { return channels; }
        public int getBitDepth() { return bitDepth; }
        public double getDuration() { return duration; }
        
        @Override
        public String toString() {
            return "AudioFileInfo{" +
                    "fileName='" + fileName + '\'' +
                    ", fileSize=" + fileSize +
                    ", sampleRate=" + sampleRate +
                    ", channels=" + channels +
                    ", bitDepth=" + bitDepth +
                    ", duration=" + duration +
                    '}';
        }
    }
    
    /**
     * 将字节数组转换为Base64字符串
     * 用于测试目的的公共方法
     * 
     * @param bytes 字节数组
     * @return Base64字符串
     */
    public String convertBytesToBase64(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("字节数组不能为空");
        }
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * 验证音频格式
     * 用于测试目的的公共方法
     * 
     * @param file 音频文件
     * @throws AudioProcessingException 格式验证失败
     */
    public void validateAudioFormat(MultipartFile file) throws AudioProcessingException {
        validateFileFormat(file);
    }
    
    /**
     * 音频处理异常类
     */
    public static class AudioProcessingException extends Exception {
        public AudioProcessingException(String message) {
            super(message);
        }
        
        public AudioProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
