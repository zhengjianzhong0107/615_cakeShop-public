package com.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.ItemsDao;
import com.dao.OrdersDao;
import com.entity.Goods;
import com.entity.Items;
import com.entity.Orders;

/**
 * 商品订单服务
 */
@Service	// 注解为service层spring管理bean
@Transactional	// 注解此类所有方法加入spring事务, 具体设置默认
public class OrderService {

	@Autowired
	private OrdersDao orderDao;
	@Autowired
	private ItemsDao itemDao;
	@Autowired
	private GoodService goodService;
	@Autowired
	private UserService userService;
	
	
	/**
	 * 创建订单
	 * @param good
	 * @return
	 */
	public Orders add(Goods good) {
		List<Items> itemList = new ArrayList<Items>();
		itemList.add(addItem(good));
		Orders order = new Orders();
		order.setItemList(itemList);
		order.setTotal(good.getPrice());
		order.setAmount(1);
		return order;
	}

	/**
	 * 向订单添加项目
	 * @param order
	 * @param good
	 * @return
	 */
	public Orders addOrderItem(Orders order, Goods good) {
		List<Items> itemList = order.getItemList();
		itemList = itemList==null ? new ArrayList<Items>() : itemList;
		// 如果购物车已有此项目, 数量+1
		boolean notThis = true;
		for (Items item : itemList) {
			if (item.getGoodId() == good.getId()) {
				item.setAmount(item.getAmount() + 1);
				item.setTotal(good.getPrice() * item.getAmount());
				notThis = false;
			}
		}
		// 如果当前购物车没有此项目, 创建新条目
		if (notThis) {
			itemList.add(addItem(good));
		}
		order.setTotal(order.getTotal() + good.getPrice());
		order.setAmount(order.getAmount() + 1);
		return order;
	}
	
	/**
	 * 从订单中减少项目
	 * @param order
	 * @param good
	 * @return
	 */
	public Orders lessenIndentItem(Orders order, Goods good) {
		List<Items> itemList = order.getItemList();
		itemList = itemList==null ? new ArrayList<Items>() : itemList;
		// 如果购物车已有此项目, 数量-1
		boolean noneThis = true;
		for (Items item : itemList) {
			if (item.getGoodId() == good.getId()) {
				if (item.getAmount() - 1 <= 0) { // 减少到0后删除
					return deleteIndentItem(order, good);
				}
				item.setAmount(item.getAmount() - 1);
				item.setTotal(good.getPrice() * item.getAmount());
				noneThis = false;
			}
		}
		// 如果当前购物车没有项目, 直接返回
		if (noneThis) {
			return order;
		}
		order.setTotal(order.getTotal() - good.getPrice());
		order.setAmount(order.getAmount() - 1);
		return order;
	}
	
	/**
	 * 从订单中删除项目
	 * @param order
	 * @param good
	 * @return
	 */
	public Orders deleteIndentItem(Orders order, Goods good) {
		List<Items> itemList = order.getItemList();
		itemList = itemList==null ? new ArrayList<Items>() : itemList;
		// 如果购物车已有此项目, 数量清零
		boolean noneThis = true;
		int itemAmount = 0;
		List<Items> resultList = new ArrayList<Items>();
		for (Items item : itemList) {
			if (item.getGoodId() == good.getId()) {
				itemAmount = item.getAmount();
				noneThis = false;
				continue;
			}
			resultList.add(item);
		}
		// 如果已经没有项目, 返回null
		if (resultList.isEmpty()) {
			return null;
		}
		order.setItemList(resultList);
		// 如果当前购物车没有项目, 直接返回
		if (noneThis) {
			return order;
		}
		order.setTotal(order.getTotal() - good.getPrice() * itemAmount);
		order.setAmount(order.getAmount() - itemAmount);
		return order;
	}

	/**
	 * 保存订单
	 * @param order
	 */
	public int save(Orders order) {
		order.setStatus(Orders.STATUS_UNPAY);
		order.setSystime(new Date());
		orderDao.insert(order);
		int orderid = order.getId();
		for(Items item : order.getItemList()){
			item.setOrderId(orderid);
			itemDao.insert(item);
		}
		return orderid;
	}
	
	/**
	 * 订单支付
	 * @param order
	 */
	public void pay(Orders order) {
		Orders old = orderDao.selectById(order.getId());
		// 微信或支付宝支付时, 模拟支付完成
		int paytype = order.getPaytype();
		if(paytype == Orders.PAYTYPE_WECHAT || paytype == Orders.PAYTYPE_ALIPAY) {
			old.setStatus(Orders.STATUS_PAYED);
		}else {
			old.setStatus(Orders.STATUS_SEND);
		}
		old.setPaytype(order.getPaytype());
		old.setName(order.getName());
		old.setPhone(order.getPhone());
		old.setAddress(order.getAddress());
		orderDao.updateById(old);
	}
	
	/**
	 * 获取订单列表
	 * @param page
	 * @param row
	 * @return
	 */
	public List<Orders> getList(byte status, int page, int row) {
		List<Orders> orderList = status>0 ? orderDao.getListByStatus(status, row * (page-1), row) 
				: orderDao.getList(row * (page-1), row);
		for(Orders order : orderList) {
			order.setItemList(this.getItemList(order.getId()));
			order.setUser(userService.get(order.getUserId()));
		}
		return orderList;
	}
	
	/**
	 * 获取订单列表
	 * @param page
	 * @param row
	 * @return
	 */
	public List<Orders> getListById(int id) {
		Orders order = this.get(id);
		if(Objects.nonNull(order)) {
			order.setItemList(this.getItemList(order.getId()));
			return Arrays.asList(order);
		}
		return null;
	}
	
	/**
	 * 获取总数
	 * @return
	 */
	public int getTotal(byte status) {
		return (int)orderDao.getTotalByStatus(status);
	}

	/**
	 * 订单发货
	 * @param id
	 * @return 
	 */
	public boolean dispose(int id) {
		Orders order = orderDao.selectById(id);
		order.setStatus(Orders.STATUS_SEND);
		return orderDao.updateByIdSelective(order) > 0;
	}
	
	/**
	 * 订单完成
	 * @param id
	 * @return 
	 */
	public boolean finish(int id) {
		Orders order = orderDao.selectById(id);
		order.setStatus(Orders.STATUS_FINISH);
		return orderDao.updateByIdSelective(order) > 0;
	}

	/**
	 * 删除订单
	 * @param id
	 */
	public boolean delete(int id) {
		return orderDao.deleteById(id) > 0;
	}
	
	/**
	 * 获取某用户全部订单
	 * @param userid
	 */
	public List<Orders> getListByUserid(int userid) {
		return orderDao.getListByUserid(userid);
	}

	/**
	 * 通过id获取
	 * @param orderid
	 * @return
	 */
	public Orders get(int orderid) {
		return orderDao.selectById(orderid);
	}
	
	
	/**
	 * 创建订单项
	 * @param good
	 * @return
	 */
	private Items addItem(Goods good) {
		Items item = new Items();
		item.setGoodId(good.getId());
		item.setAmount(1);
		item.setPrice(good.getPrice());
		item.setTotal(good.getPrice());
		item.setGood(goodService.get(item.getGoodId()));
		return item;
	}
	
	/**
	 * 获取订单项目列表
	 * @param orderid
	 * @return
	 */
	public List<Items> getItemList(int orderid){
		List<Items> itemList = itemDao.getItemList(orderid);
		for(Items item : itemList) {
			item.setGood(goodService.get(item.getGoodId()));
		}
		return itemList;
	}
}
