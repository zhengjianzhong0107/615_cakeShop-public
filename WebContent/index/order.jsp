<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
	<title>我的订单</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link type="text/css" rel="stylesheet" href="css/bootstrap.css">
	<link type="text/css" rel="stylesheet" href="css/style.css">
	<script type="text/javascript" src="js/jquery.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="layer/layer.js"></script>
	<script type="text/javascript" src="js/cart.js"></script>
</head>
<body>
	
	<jsp:include page="header.jsp"/>
	
	<!--cart-items-->
	<div class="cart-items">
		<div class="container">
		
		<c:if test="${msg!=null}"><div class="alert alert-success">${msg}</div></c:if>
		<c:if test="${orderList!=null}">
			<h2>我的订单</h2>
			
				<table class="table table-bordered table-hover">

				<tr>
					<th width="10%">ID</th>
					<th width="10%">总价</th>
					<th width="20%">商品详情</th>
					<th width="20%">收货信息</th>
					<th width="10%">订单状态</th>
					<th width="10%">支付方式</th>
					<th width="10%">下单时间</th>
					<th width="10%">售后评价</th>
				</tr>
				<c:forEach var="order" items="${orderList}">
			         <tr>
			         	<td id="order_id"><p>${order.id}</p></td>
			         	<td><p>${order.total}</p></td>
			         	<td>
				         	<c:forEach var ="item" items="${order.itemList}">
					         	<p><a href="detail?goodid=${item.good.id}" target="_blank">${item.good.name}</a>(${item.price}) x ${item.amount}</p>
				         	</c:forEach>
			         	</td>
			         	<td>
			         		<p>${order.name}</p>
			         		<p>${order.phone}</p>
			         		<p>${order.address}</p>
			         	</td>
						<td>
							<p>
								<c:if test="${order.status==1}">未付款</c:if>
								<c:if test="${order.status==2}"><span style="color:red;">已付款</span></c:if>
								<c:if test="${order.status==3}">配送中</c:if>
								<c:if test="${order.status==4}">已完成</c:if>
							</p>
						</td>
						<td>
							<p>
								<c:if test="${order.paytype==1}">微信</c:if>
								<c:if test="${order.paytype==2}">支付宝</c:if>
								<c:if test="${order.paytype==3}">货到付款</c:if>
							</p>
						</td>
						<td><p><fmt:formatDate value="${order.systime}" pattern="yyyy-MM-dd HH:mm:ss" /></p></td>
						 <td><a href="/pj.jsp">去评价</a></td>
					 </tr>
				</c:forEach>
			     
			</table>
			
			</c:if>
			<c:if test="${orderList==null}"><div class="alert alert-info">空空如也</div></c:if>
			
		</div>
	</div>
	<!--//cart-items-->	
	
	<jsp:include page="footer.jsp"/>

</body>
</html>