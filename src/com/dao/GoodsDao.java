package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.entity.Goods;

public interface GoodsDao {
    int deleteById(Integer id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectById(Integer id);

    int updateByIdSelective(Goods record);

    int updateById(Goods record);    
    
    // 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
    
    // ------------------------------------------------------------
    
    // 以下方法使用mybatis注解实现
    
    
	/**
	 * 获取列表
	 * @param page
	 * @param size
	 * @return
	 */
    @Select("select * from goods order by id desc limit #{begin}, #{size}")
	public List<Goods> getList(@Param("begin")int begin, @Param("size")int size);
	
	/**
	 * 获取总数
	 * @return
	 */
    @Select("select count(*) from goods")
	public long getTotal();
	
	/**
	 * 通过类型获取列表
	 * @param typeid
	 * @param page
	 * @param size
	 * @return
	 */
    @Select("select * from goods where type_id=#{typeid} order by id desc limit #{begin}, #{size}")
	public List<Goods> getListByType(@Param("typeid")int typeid, @Param("begin")int begin, @Param("size")int size);
	
	/**
	 * 通过类型获取总数
	 * @param typeid
	 * @return
	 */
    @Select("select count(*) from goods where type_id=#{typeid}")
	public long getTotalByType(@Param("typeid")int typeid);
	
	/**
	 * 通过名称获取列表
	 * @param name
	 * @param page
	 * @param size
	 * @return
	 */
    @Select("select * from goods where name like concat('%',#{name},'%') order by id desc limit #{begin}, #{size}")
	public List<Goods> getListByName(@Param("name")String name, @Param("begin")int begin, @Param("size")int size);
	
	/**
	 * 通过名称获取总数
	 * @param name
	 * @return
	 */
    @Select("select count(*) from goods where name like concat('%',#{name},'%')")
	public long getTotalByName(@Param("name")String name);
}