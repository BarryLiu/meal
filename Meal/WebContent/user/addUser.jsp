<%--
  Created by IntelliJ IDEA.
  User: yingjing.liu
  Date: 2016/6/23
  Time: 15:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<html>
<head>
    <title>添加用户</title>


    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>

    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery/jquery-1.9.0.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerForm.js?t=201603212002" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerRadio.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerTip.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerSpinner.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/json2.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/jquery.metadata.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/jquery.validate.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/messages_cn.js" type="text/javascript"></script>
    <!-- chosen -->
    <link href="${pageContext.request.contextPath }/js/chosen/chosen.min.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/chosen/chosen.jquery.min.js"></script>

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172046" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/modify.validate.common.js?t=2016032316491" type="text/javascript"></script>


    <script type="text/javascript" >


        $("#number").chosen({
            search_contains:true
        });

        function  firstChange() {
            var first = $("#firstDept").val();

            if (first == '' || first == '-1') {
                $("#secondDept").empty();
                $("#secondDept").append($("<option/>").text('请选择').attr("value", ''));
                return
            }
            var i = 100;
            dwr.getDeptSecond(first, function (data) {
                var res = eval("(" + data + ")");
                if (i == 100) {
                    $("#secondDept").empty();
                    $("#secondDept").append($("<option/>").text('请选择').attr("value", ''));
                    //对请求返回的JSON格式进行分解加载
                    $(res).each(function () {
                        $("#secondDept").append($("<option/>").text(this.name).attr("value", this.id));
                    });
                    i = 0;
                }
            });
        }

    </script>

</head>
<body>


    <form name = >
        <table  cellpadding="30"  align="center"  style="height: 80%" width="80%">
            <tr style="margin-top: 20ex" >
                <td>添加用户</td>
                <td>

                    <select id="loginAccount" name="loginAccount"  >
                        <option value="-1" >请选择</option>
                        <c:forEach items="${ldapUsers}" var="tUser">
                           <option value="${tUser.loginCount}" >${tUser.displayName}</option>
                        </c:forEach>
                    </select>

                </td>
            </tr>
            <tr style="margin-top: 50px">
                <td>工号</td>
                <td>
                    <input type="text" id="number" onblur="checkNumber()" onkeyup='this.value=this.value.replace(/\D/gi,"")'  > <font id="numberMsg" color="red"></font>
                </td>
            </tr>
            <tr style="margin-top: 50px">
                <td>入职日期</td>
                <td>
                    <input type="date" name="enterDate" id="enterDate" >
                </td>
            </tr>
            <tr  id="div_type1" >
                <td style="align:right">一级部门:</td>
                <td>
                    <select id="firstDept" name="parentId" onchange="firstChange()">
                        <option value="" >请选择部门</option>
                        <c:forEach items="${depts}" var="dept">
                            <option value="${dept.id}" >${dept.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="align:right">二级部门：</td>
                <td>
                    <select id="secondDept" name="secondDept">
                        <option value="">请选择</option>
                    </select>
                </td>
            </tr>
            <tr style="margin-top: 50px">
                <td><input type="button"  id="btn_add" value="添加"> </td>
                <td><input type="reset" value="重置"> </td>
            </tr>
        </table>

    </form>

    <script type="text/javascript">

        $("#btn_add").click(function(){

            var loginAccount = $("#loginAccount").val();
            var number = $("#number").val();
            var enterDate = $("#enterDate").val();


            var fid =  $("#firstDept").val();
            var sid = $("#secondDept").val();

            if($("#numberMsg").is(":hidden")){
                $("#numberMsg").show();    //如果元素为隐藏,则将它显现
            }else{
                $.ligerDialog.alert("工号已占用","提示", "warn");
                return ;
            }


            if(loginAccount == ""|| loginAccount=="-1"){
                $.ligerDialog.alert('请选择用户',"提示", "warn");
                return ;
            }
            if(number==""){
                $.ligerDialog.alert('员工工号不能为空',"提示", "warn");
                return ;
            }
            if(enterDate==""){
                $.ligerDialog.alert('入职日期不能为空', "提示","warn");
                return ;
            }


                $.ajax({
                    type:"post",
                    url:"${pageContext.request.contextPath}/user",
                    data:{"op":"add","todo":"1","loginAccount":loginAccount,"enterDate":enterDate,"number":number,"fid":fid,"sid":sid},
                    dataType:"json",
                    async:true,
                    success:function(data) {

                        if(data.res) {
                           // Tip.tip("修改成功！");
                            $.ligerDialog.alert('添加成功', "提示","success");

                           setTimeout(function(){
                               parent.$.ligerDialog.close(); //关闭
                               parent.$(".l-dialog,.l-window-mask").css("display","none");
                               parent.addCookie();
                               parent.location.reload();
                           },2000);

                        } else {
                            Tip.tip("修改失败");
                        }*/
                    }
                });
            $.ligerDialog.alert('添加成功', "提示","success");
            setTimeout(function(){
                parent.$.ligerDialog.close(); //关闭
                parent.$(".l-dialog,.l-window-mask").css("display","none");
                parent.addCookie();
                parent.location.reload();
            },2000);

        });

        function checkNumber(){
            var number  = $("#number").val();
            if(number==''){
                return;
            }
            dwr.checkNumberUnique(number, function(data) {
                $.ligerDialog.closeWaitting();
                var res = eval('('+data+')');
                var numberMsg= $("#numberMsg");
                if(res.res) {
                    numberMsg.text("");
                    numberMsg.hide();
                } else {
//                    $.ligerDialog.error(res.msg);//错误信息
                    numberMsg.text(res.msg);
                    numberMsg.show()

                }
            });

        }
    </script>



</body>
</html>
