package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.entity.Tops;

public interface TopsDao {
    int deleteById(Integer id);

    int insert(Tops record);

    int insertSelective(Tops record);

    Tops selectById(Integer id);

    int updateByIdSelective(Tops record);

    int updateById(Tops record);    
    
    // 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
    
    // ------------------------------------------------------------
    
    // 以下方法使用mybatis注解实现
    

	/**
	 * 获取列表
	 * @return
	 */
    @Select("select * from tops where type=#{type} order by id desc limit #{begin}, #{size}")
	public List<Tops> getList(@Param("type")byte type, @Param("begin")int begin, @Param("size")int size);
	/**
	 * 获取总数
	 * @param type
	 * @return
	 */
    @Select("select count(*) from tops where type=#{type}")
	public long getTotal(byte type);
	
	/**
	 * 通过商品id获取
	 * @param goodid
	 * @return
	 */
    @Select("select * from tops where good_id=#{goodid}")
	public List<Tops> getListByGoodid(int goodid);
	
	/**
	 * 通过商品id和类型删除
	 * @param goodid
	 * @param type
	 * @return
	 */
    @Delete("delete from tops where good_id=#{goodid} and type=#{type}")
	public boolean deleteByGoodidAndType(@Param("goodid")int goodid, @Param("type")byte type);
	
	/**
	 * 通过goodid删除
	 * @param goodid
	 * @return
	 */
    @Delete("delete from tops where good_id=#{goodid}")
	public boolean deleteByGoodid(@Param("goodid")int goodid);
}