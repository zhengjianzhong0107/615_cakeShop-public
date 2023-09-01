<%--
  Created by IntelliJ IDEA.
  User: ghq
  Date: 2021/12/11
  Time: 23:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>五星评价</title>

    <link type="text/css" href="css/style.css" rel="stylesheet" />

</head>
<body>

<div class="order-evaluation clearfix">
    <h4>给蛋糕的评价</h4>
    <p>请严肃认真对待此次评价哦！您的评价对我们真的真的非常重要！</p>
    <div class="order-evaluation-text">
        本次交易，乖，摸摸头 给您留下了什么印象呢？
    </div>
    <div class="order-evaluation-checkbox">
        <ul class="clearfix">
            <li class="order-evaluation-check" data-impression="1">香甜可口<i class="iconfont icon-checked">√</i></li>
            <li class="order-evaluation-check" data-impression="2">美味诱人<i class="iconfont icon-checked">√</i></li>
            <li class="order-evaluation-check" data-impression="3">色味俱全<i class="iconfont icon-checked">√</i></li>
            <li class="order-evaluation-check" data-impression="4">松软可口<i class="iconfont icon-checked">√</i></li>
        </ul>
    </div>
    <div class="order-evaluation-textarea">
        <textarea name="content" id="TextArea1" onKeyUp="words_deal();" ></textarea>
        <span>还可以输入<em id="textCount">140</em>个字</span>
    </div>
    <a href="javascript:;" id="order_evaluation">评价完成</a>
</div>

<div id="order_evaluate_modal" class="dmlei_tishi_info"></div>


<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">

    //印象
    $(".order-evaluation-check").click(function(){
        if($(this).hasClass('checked')){
            //当前为选中状态，需要取消
            $(this).removeClass('checked');
        }else{
            //当前未选中，需要增加选中
            $(this).addClass('checked');
        }
    });
    //评价字数限制
    function words_deal()
    {
        var curLength=$("#TextArea1").val().length;
        if(curLength>140)
        {
            var num=$("#TextArea1").val().substr(0,140);
            $("#TextArea1").val(num);
            alert("超过字数限制，多出的字将被截断！" );
        }
        else
        {
            $("#textCount").text(140-$("#TextArea1").val().length);
        }
    }
    $("#order_evaluation").click(function(){
        $("#order_evaluate_modal").html("感谢您的评价！么么哒(* ￣3)(ε￣ *)").show().delay(3000).hide(500);
    })
</script>

</body>
</html>
