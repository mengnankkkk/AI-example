package edu.qlu.chatbot.mapper;

import edu.qlu.chatbot.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 * 使用MyBatis进行数据库操作
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User findById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱地址
     * @return 用户对象，如果不存在则返回null
     */
    User findByEmail(@Param("email") String email);
    
    /**
     * 查询所有激活的用户
     * 
     * @return 用户列表
     */
    List<User> findAllActive();
    
    /**
     * 查询所有用户（包括非激活用户）
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 插入新用户
     * 
     * @param user 用户对象
     * @return 影响的行数
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);
    
    /**
     * 软删除用户（设置is_active为false）
     * 
     * @param id 用户ID
     * @return 影响的行数
     */
    int softDelete(@Param("id") Long id);
    
    /**
     * 物理删除用户
     * 
     * @param id 用户ID
     * @return 影响的行数
     */
    int delete(@Param("id") Long id);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 存在返回true，否则返回false
     */
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱地址
     * @return 存在返回true，否则返回false
     */
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 根据条件分页查询用户
     * 
     * @param keyword 搜索关键词（用户名或全名）
     * @param isActive 激活状态
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 用户列表
     */
    List<User> findWithPagination(@Param("keyword") String keyword, 
                                 @Param("isActive") Boolean isActive,
                                 @Param("offset") int offset, 
                                 @Param("limit") int limit);
    
    /**
     * 统计符合条件的用户数量
     * 
     * @param keyword 搜索关键词
     * @param isActive 激活状态
     * @return 用户数量
     */
    long countWithCondition(@Param("keyword") String keyword, 
                           @Param("isActive") Boolean isActive);
}
