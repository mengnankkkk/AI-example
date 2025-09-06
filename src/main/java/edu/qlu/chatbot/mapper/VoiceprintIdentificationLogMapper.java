package edu.qlu.chatbot.mapper;

import edu.qlu.chatbot.model.VoiceprintIdentificationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 声纹识别日志数据访问接口
 * 使用MyBatis进行数据库操作
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Mapper
public interface VoiceprintIdentificationLogMapper {
    
    /**
     * 根据ID查询识别日志
     * 
     * @param id 日志ID
     * @return 识别日志对象，如果不存在则返回null
     */
    VoiceprintIdentificationLog findById(@Param("id") Long id);
    
    /**
     * 根据请求ID查询识别日志
     * 
     * @param requestId 请求ID
     * @return 识别日志对象，如果不存在则返回null
     */
    VoiceprintIdentificationLog findByRequestId(@Param("requestId") String requestId);
    
    /**
     * 根据用户ID查询识别日志
     * 
     * @param userId 用户ID
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据讯飞特征ID查询识别日志
     * 
     * @param featureId 讯飞特征ID
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findByFeatureId(@Param("featureId") String featureId);
    
    /**
     * 根据时间范围查询识别日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最近的识别日志
     * 
     * @param limit 限制条数
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findRecent(@Param("limit") int limit);
    
    /**
     * 查询所有识别日志（包含用户信息）
     * 
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findAllWithUser();
    
    /**
     * 插入新的识别日志
     * 
     * @param log 识别日志对象
     * @return 影响的行数
     */
    int insert(VoiceprintIdentificationLog log);
    
    /**
     * 更新识别日志
     * 
     * @param log 识别日志对象
     * @return 影响的行数
     */
    int update(VoiceprintIdentificationLog log);
    
    /**
     * 物理删除识别日志
     * 
     * @param id 日志ID
     * @return 影响的行数
     */
    int delete(@Param("id") Long id);
    
    /**
     * 根据时间范围批量删除识别日志
     * 
     * @param beforeTime 指定时间之前的日志将被删除
     * @return 影响的行数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 统计识别日志总数
     * 
     * @return 日志总数
     */
    long count();
    
    /**
     * 统计指定用户的识别次数
     * 
     * @param userId 用户ID
     * @return 识别次数
     */
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计指定时间范围内的识别次数
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 识别次数
     */
    long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据条件分页查询识别日志
     * 
     * @param userId 用户ID（可选）
     * @param featureId 讯飞特征ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 识别日志列表
     */
    List<VoiceprintIdentificationLog> findWithPagination(@Param("userId") Long userId,
                                                        @Param("featureId") String featureId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime,
                                                        @Param("offset") int offset,
                                                        @Param("limit") int limit);
    
    /**
     * 统计符合条件的识别日志数量
     * 
     * @param userId 用户ID（可选）
     * @param featureId 讯飞特征ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 日志数量
     */
    long countWithCondition(@Param("userId") Long userId,
                           @Param("featureId") String featureId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取识别统计信息
     * 
     * @param days 统计最近几天的数据
     * @return 统计信息列表
     */
    List<VoiceprintIdentificationLog> getStatistics(@Param("days") int days);
}
