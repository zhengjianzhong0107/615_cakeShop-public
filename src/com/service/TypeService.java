package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.TypesDao;
import com.entity.Types;

/**
 * 类型服务
 */
@Service	// 注解为service层spring管理bean
@Transactional	// 注解此类所有方法加入spring事务, 具体设置默认
public class TypeService {

	@Autowired	
	private TypesDao typeDao;
	
	
	/**
	 * 获取列表
	 * @return
	 */
	public List<Types> getList(){
		return typeDao.getList();
	}
	
	/**
	 * 通过名字获取列表
	 * @return
	 */
	public List<Types> getList(String name){
		return typeDao.getListByName(name);
	}

	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	public Types get(int id) {
		return typeDao.selectById(id);
	}
	
	/**
	 * 添加
	 * @param type
	 * @return
	 */
	public Integer add(Types type) {
		return typeDao.insert(type);
	}

	/**
	 * 更新
	 * @param type
	 */
	public boolean update(Types type) {
		return typeDao.updateById(type) > 0;
	}

	/**
	 * 删除
	 * @param type
	 */
	public boolean delete(Types type) {
		return typeDao.deleteById(type.getId()) > 0;
	}
	
}
