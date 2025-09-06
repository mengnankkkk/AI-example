package edu.qlu.chatbot.mapper;

import edu.qlu.chatbot.model.Voiceprint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 声纹数据访问接口
 * 使用MyBatis进行数据库操作
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Mapper
public interface VoiceprintMapper {
    
    /**
     * 根据ID查询声纹记录
     * 
     * @param id 声纹记录ID
     * @return 声纹对象，如果不存在则返回null
     */
    Voiceprint findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询声纹记录
     * 
     * @param userId 用户ID
     * @return 声纹记录列表
     */
    List<Voiceprint> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询激活的声纹记录
     * 
     * @param userId 用户ID
     * @return 激活的声纹记录列表
     */
    List<Voiceprint> findActiveByUserId(@Param("userId") Long userId);
    
    /**
     * 根据讯飞特征ID查询声纹记录
     * 
     * @param featureId 讯飞特征ID
     * @return 声纹对象，如果不存在则返回null
     */
    Voiceprint findByFeatureId(@Param("featureId") String featureId);
    
    /**
     * 根据讯飞特征ID查询声纹记录（包含用户信息）
     * 
     * @param featureId 讯飞特征ID
     * @return 声纹对象（包含关联的用户信息），如果不存在则返回null
     */
    Voiceprint findByFeatureIdWithUser(@Param("featureId") String featureId);
    
    /**
     * 根据讯飞组ID查询所有声纹记录
     * 
     * @param groupId 讯飞组ID
     * @return 声纹记录列表
     */
    List<Voiceprint> findByGroupId(@Param("groupId") String groupId);
    
    /**
     * 查询所有激活的声纹记录
     * 
     * @return 激活的声纹记录列表
     */
    List<Voiceprint> findAllActive();
    
    /**
     * 查询所有声纹记录（包含用户信息）
     * 
     * @return 声纹记录列表
     */
    List<Voiceprint> findAllWithUser();
    
    /**
     * 插入新的声纹记录
     * 
     * @param voiceprint 声纹对象
     * @return 影响的行数
     */
    int insert(Voiceprint voiceprint);
    
    /**
     * 更新声纹记录信息
     * 
     * @param voiceprint 声纹对象
     * @return 影响的行数
     */
    int update(Voiceprint voiceprint);
    
    /**
     * 更新声纹记录的识别统计信息
     * 
     * @param featureId 讯飞特征ID
     * @param lastIdentifiedAt 最后识别时间
     * @return 影响的行数
     */
    int updateIdentificationStats(@Param("featureId") String featureId, 
                                 @Param("lastIdentifiedAt") LocalDateTime lastIdentifiedAt);
    
    /**
     * 软删除声纹记录（设置is_active为false）
     * 
     * @param id 声纹记录ID
     * @return 影响的行数
     */
    int softDelete(@Param("id") Long id);
    
    /**
     * 根据特征ID软删除声纹记录
     * 
     * @param featureId 讯飞特征ID
     * @return 影响的行数
     */
    int softDeleteByFeatureId(@Param("featureId") String featureId);
    
    /**
     * 物理删除声纹记录
     * 
     * @param id 声纹记录ID
     * @return 影响的行数
     */
    int delete(@Param("id") Long id);
    
    /**
     * 根据特征ID物理删除声纹记录
     * 
     * @param featureId 讯飞特征ID
     * @return 影响的行数
     */
    int deleteByFeatureId(@Param("featureId") String featureId);
    
    /**
     * 检查用户是否已注册声纹
     * 
     * @param userId 用户ID
     * @return 已注册返回true，否则返回false
     */
    boolean existsByUserId(@Param("userId") Long userId);
    
    /**
     * 检查讯飞特征ID是否存在
     * 
     * @param featureId 讯飞特征ID
     * @return 存在返回true，否则返回false
     */
    boolean existsByFeatureId(@Param("featureId") String featureId);
    
    /**
     * 统计指定用户的声纹数量
     * 
     * @param userId 用户ID
     * @return 声纹数量
     */
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计指定组的声纹数量
     * 
     * @param groupId 讯飞组ID
     * @return 声纹数量
     */
    long countByGroupId(@Param("groupId") String groupId);
    
    /**
     * 根据条件分页查询声纹记录
     * 
     * @param userId 用户ID（可选）
     * @param groupId 讯飞组ID（可选）
     * @param isActive 激活状态（可选）
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 声纹记录列表
     */
    List<Voiceprint> findWithPagination(@Param("userId") Long userId,
                                       @Param("groupId") String groupId,
                                       @Param("isActive") Boolean isActive,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);
    
    /**
     * 统计符合条件的声纹记录数量
     * 
     * @param userId 用户ID（可选）
     * @param groupId 讯飞组ID（可选）
     * @param isActive 激活状态（可选）
     * @return 声纹记录数量
     */
    long countWithCondition(@Param("userId") Long userId,
                           @Param("groupId") String groupId,
                           @Param("isActive") Boolean isActive);
}
