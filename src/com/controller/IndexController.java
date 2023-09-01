package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.entity.Tops;
import com.service.GoodService;
import com.service.TopService;
import com.service.TypeService;
import com.util.PageUtil;

/**
 * 前台相关接口
 */
@Controller
@RequestMapping("/index")
public class IndexController{
	
	private static final int rows = 16; // 默认每页数量

	@Autowired
	private TopService topService;
	@Autowired
	private GoodService goodService;
	@Autowired
	private TypeService typeService;
	

	/**
	 * 首页
	 * @return
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request){
		request.setAttribute("flag", 1);
		request.setAttribute("typeList", typeService.getList());
		Map<String,Object> scrollGoods = new HashMap<>(16);
		List<Tops> list = topService.getList(Tops.TYPE_SCROLL, 1, 1);
		Tops tops = list.get(0);
		scrollGoods.put("id",tops.getGood().getId());
		scrollGoods.put("name",tops.getGood().getName());
		scrollGoods.put("cover",tops.getGood().getCover());
		request.setAttribute("scroll", scrollGoods);
		request.setAttribute("top2List", topService.getList(Tops.TYPE_LARGE, 1, 6));
		request.setAttribute("top3List", topService.getList(Tops.TYPE_SMALL, 1, 8));

		return "/index/index.jsp";
	}
	
	/**
	 * 推荐列表
	 * @return
	 */
	@RequestMapping("/top")
	public String tops(int typeid, @RequestParam(required=false, defaultValue="1")int page, HttpServletRequest request) {
		request.setAttribute("flag", typeid==2 ? 7 : 8);
		request.setAttribute("typeList", typeService.getList());
		request.setAttribute("goodList", goodService.getList(typeid, page, rows));
		request.setAttribute("pageTool", PageUtil.getPageTool(request, goodService.getTotal(typeid), page, rows));
		return "/index/goods.jsp";
	}
	
	/**
	 * 商品列表
	 * @return
	 */
	@RequestMapping("/goods")
	public String goods(int typeid, @RequestParam(required=false, defaultValue="1")int page, HttpServletRequest request){
		request.setAttribute("flag", 2);
		if (typeid > 0) {
			request.setAttribute("type", typeService.get(typeid));
		}
		request.setAttribute("typeList", typeService.getList());
		request.setAttribute("goodList", goodService.getListByType(typeid, page, rows));
		request.setAttribute("pageTool", PageUtil.getPageTool(request, goodService.getTotalByType(typeid), page, rows));
		return "/index/goods.jsp";
	}
	
	/**
	 * 商品详情
	 * @return
	 */
	@RequestMapping("/detail")
	public String detail(int goodid, HttpServletRequest request){
		request.setAttribute("good", goodService.get(goodid));
		request.setAttribute("typeList", typeService.getList());
		return "/index/detail.jsp";
	}
	
	/**
	 * 搜索
	 * @return
	 */
	@RequestMapping("/search")
	public String search(String name, @RequestParam(required=false, defaultValue="1")int page, HttpServletRequest request) {
		if (Objects.nonNull(name) && !name.trim().isEmpty()) {
			request.setAttribute("goodList", goodService.getListByName(name, page, rows));
			request.setAttribute("pageTool", PageUtil.getPageTool(request, goodService.getTotalByName(name), page, rows));
		}
		request.setAttribute("typeList", typeService.getList());
		return "/index/goods.jsp";
	}

}