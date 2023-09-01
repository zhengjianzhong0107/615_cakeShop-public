package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.entity.Users;

public interface UsersDao {
    int deleteById(Integer id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectById(Integer id);

    int updateByIdSelective(Users record);

    int updateById(Users record);    
    
    // 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
    
    // ------------------------------------------------------------
    
    // 以下方法使用mybatis注解实现
    
	/**
	 * 通过用户名查找用户
	 * @return 无记录返回null
	 */
    @Select("select * from users where username=#{username}")
	public Users getByUsername(String username);
	
	/**
	 * 通过用户名和密码查找
	 * @param username
	 * @param password
	 * @return 无记录返回null
	 */
    @Select("select * from users where username=#{username} and password=#{password}")
	public Users getByUsernameAndPassword(@Param("username")String username, @Param("password")String password);
	
	/**
	 * 获取列表
	 * @param page
	 * @param rows
	 * @return 无记录返回空集合
	 */
    @Select("select * from users order by id desc limit #{begin}, #{size}")
	public List<Users> getList(@Param("begin")int begin, @Param("size")int size);
    
    /**
     * 通过名称搜索列表
     * @return 无记录返回空集合
     */
    @Select("select * from users where username like concat('%',#{name},'%') order by id desc ")
    public List<Users> getListByName(@Param("name")String name);

	/**
	 * 总数
	 * @return
	 */
    @Select("select count(*) from users")
	public long getTotal();
	
    
}