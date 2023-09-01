package com.controller;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.entity.Goods;
import com.entity.Items;
import com.entity.Orders;
import com.entity.Users;
import com.service.GoodService;
import com.service.OrderService;
import com.service.TypeService;
import com.service.UserService;
import com.util.SafeUtil;

/**
 * 用户相关接口
 */
@Controller
@RequestMapping("/index")
public class UserController{
	
	private static final String INDENT_KEY = "order";
	
	@Resource
	private UserService userService;
	@Resource
	private OrderService orderService;
	@Resource
	private GoodService goodService;
	@Resource
	private TypeService typeService;

	
	/**
	 * 注册用户
	 * @return
	 */
	@RequestMapping("/register")
	public String register(@RequestParam(required=false, defaultValue="0")int flag, Users user, Model model){
		model.addAttribute("typeList", typeService.getList());
		if(flag==-1) {
			model.addAttribute("flag", 5); // 注册页面
			return "/index/register.jsp";
		}
		if (user.getUsername().isEmpty()) {
			model.addAttribute("msg", "用户名不能为空!");
			return "/index/register.jsp";
		}else if (userService.isExist(user.getUsername())) {
			model.addAttribute("msg", "用户名已存在!");
			return "/index/register.jsp";
		}else {
			String password = user.getPassword();
			userService.add(user);
			user.setPassword(password);
			return "redirect:login?flag=-1"; // 注册成功后转去登录
		}
	}
	
	/**
	 * 用户登录
	 * @return
	 */
	@RequestMapping("/login")
	public String login(@RequestParam(required=false, defaultValue="0")int flag, Users user, HttpSession session, Model model) {
		model.addAttribute("typeList", typeService.getList());
		if(flag==-1) {
			flag = 6; // 登录页面
			return "/index/login.jsp";
		}
		if(userService.checkUser(user.getUsername(), user.getPassword())){
			session.setAttribute("user", userService.get(user.getUsername()));
			return "redirect:index";
		} else {
			model.addAttribute("msg", "用户名或密码错误!");
			return "/index/login.jsp";
		}
	}

	/**
	 * 注销登录
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.removeAttribute("order");
		return "/index/login.jsp";
	}
	
	/**
	 * 查看购物车
	 * @return
	 */
	@RequestMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("typeList", typeService.getList());
		return "/index/cart.jsp";
	}
	
	/**
	 * 购买
	 * @return
	 */
	@RequestMapping("/buy")
	public @ResponseBody String buy(int goodid, HttpSession session){
		Goods goods = goodService.get(goodid);
		if (goods.getStock() <= 0) { // 库存不足
			return "empty";
		}
		Orders order = (Orders) session.getAttribute(INDENT_KEY);
		if (order==null) {
			session.setAttribute(INDENT_KEY, orderService.add(goods));
		}else {
			session.setAttribute(INDENT_KEY, orderService.addOrderItem(order, goods));
		}
		return "ok";
	}
	
	/**
	 * 减少
	 */
	@RequestMapping("/lessen")
	public @ResponseBody String lessen(int goodid, HttpSession session){
		Orders order = (Orders) session.getAttribute(INDENT_KEY);
		if (order != null) {
			session.setAttribute(INDENT_KEY, orderService.lessenIndentItem(order, goodService.get(goodid)));
		}
		return "ok";
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public @ResponseBody String delete(int goodid, HttpSession session){
		Orders order = (Orders) session.getAttribute(INDENT_KEY);
		if (order != null) {
			session.setAttribute(INDENT_KEY, orderService.deleteIndentItem(order, goodService.get(goodid)));
		}
		return "ok";
	}
	
	
	/**
	 * 提交订单
	 * @return
	 */
	@RequestMapping("/save")
	public String save(ServletRequest request, HttpSession session, Model model){
		model.addAttribute("typeList", typeService.getList());
		Users user = (Users) session.getAttribute("user");
		if (user == null) {
			request.setAttribute("msg", "请登录后提交订单!");
			return "/index/login.jsp";
		}
		Orders sessionOrder = (Orders) session.getAttribute(INDENT_KEY);
		if (sessionOrder != null) {
			if (sessionOrder != null) {
				for(Items item : sessionOrder.getItemList()){ // 检测商品库存(防止库存不足)
					Goods good = goodService.get(item.getGoodId());
					if(item.getAmount() > good.getStock()){
						request.setAttribute("msg", "商品 ["+good.getName()+"] 库存不足! 当前库存数量: "+good.getStock());
						return "/index/cart.jsp";
					}
				}
				// 减商品库存
				for(Items item : sessionOrder.getItemList()){ // 检测商品库存(防止库存不足)
					Goods good = goodService.get(item.getGoodId());
					good.setStock(good.getStock() - item.getAmount());
					goodService.update(good);
				}
			}
			sessionOrder.setUserId(user.getId());
			sessionOrder.setUser(userService.get(user.getId()));
			int orderid = orderService.save(sessionOrder);	// 保存订单
			session.removeAttribute(INDENT_KEY);	// 清除购物车
			return "redirect:topay?orderid="+orderid;
		}
		request.setAttribute("msg", "处理失败!");
		return "/index/cart.jsp";
	}
	
	/**
	 * 支付页面
	 * @return
	 */
	@RequestMapping("/topay")
	public String topay(int orderid, ServletRequest request, Model model) {
		model.addAttribute("typeList", typeService.getList());
		request.setAttribute("order", orderService.get(orderid));
		return "/index/pay.jsp";
	}
	
	/**
	 * 支付(模拟)
	 * @return
	 */
	@RequestMapping("/pay")
	public String pay(Orders order, Model model) {
		model.addAttribute("typeList", typeService.getList());
		orderService.pay(order);
		return "redirect:payok?orderid="+order.getId();
	}
	
	/**
	 * 支付成功
	 * @return
	 */
	@RequestMapping("/payok")
	public String payok(int orderid, ServletRequest request, Model model) {
		model.addAttribute("typeList", typeService.getList());
		Orders order = orderService.get(orderid);
		int paytype = order.getPaytype();
		if(paytype == Orders.PAYTYPE_WECHAT || paytype == Orders.PAYTYPE_ALIPAY) {
			request.setAttribute("msg", "订单["+orderid+"]支付成功");
		}else {
			request.setAttribute("msg", "订单["+orderid+"]货到付款");
		}
		return "/index/payok.jsp";
	}
	
	/**
	 * 查看订单
	 * @return
	 */
	@RequestMapping("/order")
	public String order(HttpSession session, Model model){
		model.addAttribute("flag", 3);
		model.addAttribute("typeList", typeService.getList());
		Users user = (Users) session.getAttribute("user");
		if (user == null) {
			model.addAttribute("msg", "请登录后查看订单!");
			return "/index/login.jsp";
		}
		List<Orders> orderList = orderService.getListByUserid(user.getId());
		if (orderList!=null && !orderList.isEmpty()) {
			for(Orders order : orderList){
				order.setItemList(orderService.getItemList(order.getId()));
			}
		}
		model.addAttribute("orderList", orderList);
		return "/index/order.jsp";
	}
	
	
	/**
	 * 个人信息
	 * @return
	 */
	@RequestMapping("/my")
	public String my(Users user, HttpSession session, Model model){
		model.addAttribute("flag", 4);
		model.addAttribute("typeList", typeService.getList());
		Users userLogin = (Users) session.getAttribute("user");
		if (userLogin == null) {
			model.addAttribute("msg", "请先登录!");
			return "/index/login.jsp";
		}
		// 进入个人中心
		if (Objects.isNull(user) || Objects.isNull(user.getId())) {
			return "/index/my.jsp";
		}
		Users u = userService.get(user.getId());
		// 修改资料
		u.setName(user.getName());
		u.setPhone(user.getPhone());
		u.setAddress(user.getAddress());
		userService.update(u);  // 更新数据库
		session.setAttribute("user", u); // 更新session
		model.addAttribute("msg", "信息修改成功!");
		// 修改密码
		if(user.getPasswordNew()!=null && !user.getPasswordNew().trim().isEmpty()) {
			if (user.getPassword()!=null && !user.getPassword().trim().isEmpty() 
					&& SafeUtil.encode(user.getPassword()).equals(u.getPassword())) {
				if (user.getPasswordNew()!=null && !user.getPasswordNew().trim().isEmpty()) {
					u.setPassword(SafeUtil.encode(user.getPasswordNew()));
				}
				userService.update(u);  // 更新数据库
				session.setAttribute("user", u); // 更新session
				model.addAttribute("msg", "密码修改成功!");
			}else {
				model.addAttribute("msg", "原密码错误!");
			}
		}
		return "/index/my.jsp";
	}

	
}