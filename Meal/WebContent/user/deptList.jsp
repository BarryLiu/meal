<%--
  Created by IntelliJ IDEA.
  User: yingjing.liu
  Date: 2016/7/22
  Time: 14:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>



    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>

    <script src="${pageContext.request.contextPath}/js/cookie.js" type="text/javascript"></script>

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





    <script src="${pageContext.request.contextPath}/user/TreeDeptData.js" type="text/javascript"></script>

    <script type="text/javascript">


        var manager;
        $(function ()
        {
            manager = $("#maingrid").ligerGrid({
                        columns: [
                            { display: '部门名', name: 'name', id: 'id1', width: 250, align: 'left' },
                            { display: '部门标示', name: 'value', width: 50, type: 'int', align: 'left' },

                            { display: '部门描述', name: 'comment', width: 250, align: 'left' },
                            { display: '负责人1', name: 'duname1', width: 50, align: 'left' },
                            { display: '负责人2', name: 'duname2', width: 50, align: 'left' },
                            { display: '负责人3', name: 'duname3', width: 50, align: 'left' },
                            { display: '操作', name: '', width: 150, align: 'left', render: function (rowdata, rowindex, value)
                                {
                                    var h = "";

                                         h= "<a href='javascript:modifyItemClick()'>修改</a>";


                                        h += "&nbsp;&nbsp;&nbsp;<a href='javascript:deleteItem()'>删除</a> ";


                                        h += "&nbsp;&nbsp;&nbsp;<a href='javascript:showUsers()'>显示用户</a> ";
                                    return h;
                                }
                            }
                        ], width: '100%', pageSizeOptions: [15, 20,30,45], height: '97%',clickToEdit: true,pageSize: 20,
                 data:  ${datas}, alternatingRow: false, tree: { columnId: 'id1' }
                    }
            );

          /*   $("#number").chosen({  //做不出效果不做了 下拉框效果
                search_contains:true
            });
            $("#duid1").chosen({
                search_contains:true
            });
            $("#duid2").chosen({
                search_contains:true
            });
            $("#duid3").chosen({
                search_contains:true
            });
*/

        });
        function getParent()
        {
            var row = manager.getParent(manager.getSelectedRow());
            alert(JSON.stringify(row));
        }
        function getSelected()
        {
            var row = manager.getSelectedRow();
            if (!row) { alert('请选择行'); return; }
            alert(JSON.stringify(row));
        }
        function getData()
        {
            var data = manager.getData();
            alert(JSON.stringify(data));
        }
        function hasChildren()
        {
            var row = manager.getSelectedRowObj();
            alert(manager.hasChildren(row));
        }
        function isLeaf()
        {
            var row = manager.getSelectedRowObj();
            alert(manager.isLeaf(row));
        }
        function collapseAll()
        {
            manager.collapseAll();
        }
        function expandAll()
        {
            manager.expandAll();
        }
        function showUsers(){
            //
            var row = manager.getSelectedRow();
            var url = "user?op=other&pms.dept1="+row.id+"&value="+row.value;
            var detailWin =  $.ligerDialog.open({ title: row.name +" 下的所有用户",url:url, height: 700,width: 1250});

        }
        function deleteItem(){
            var row = manager.getSelectedRow();
            if (row == 'null' || !row) {
                $.ligerDialog.alert('请选择要删除的部门', '提示', 'warn');
                return;
            }

            var rowDetail = manager.getSelectedRowObj();
            if(manager.hasChildren(rowDetail)){
                $.ligerDialog.alert('还有子部门不能删除', '提示', 'warn');
                return ;
            }

            dwr.checkCanDelete(row.id,function(data) { //判断有没有用户
                var res = eval("("+data+")");
                if(res ==true){
                    $.ligerDialog.confirm('确定要删除部门吗?', function (yes) {
                        if(yes){
                            location.href="dept?op=delete&deptId="+row.id;
                        }
                    });
                }else{
                    $.ligerDialog.alert('部门不能里面有员工,不能删除', '提示', 'warn');
                }
            });

            return  ;


        }
        function save(type) {

           var name =  $("#name").val();
            if(name == ''){
                $.ligerDialog.alert('姓名不能为空', '提示', 'warn');
                return;
            }
         /*   if(!$("#div_type1").is(":hidden")){
                var dept =  $("#dept").val();
                if(dept == ''){
                    $.ligerDialog.alert('请选择一级部门', '提示', 'warn');
                    return;
                }
            }
*/
            var comment =  $("#comment").val();
            if(comment == ''){
                $.ligerDialog.alert('描述不能为空', '提示', 'warn');
                return;
            }

            if(!$("#div_type1").is(":hidden")) {
                var fid = $("#dept").val();
                if (fid == '') {
                    $.ligerDialog.alert('请选择一级部门', '提示', 'warn');
                    return;
                }
            }


            if (type == 1) {// 插入

                $("#op").val("add");
                $("#EditUserForm").submit();
            }else if(type == 2) {
                $("#type1").attr("disabled",false);//提交的时候将限制放开
                $("#type2").attr("disabled",false);//

                $("#op").val("update");
                $("#EditUserForm").submit();
            }

       /*     $.ajax({
                cache: true,
                type: "POST",
                url:ajaxCallUrl,
                data:$('#EditUserForm').serialize(),// 你的formid
                async: false,
                error: function(request) {
                    alert("Connection error");
                },
                success: function(data) {
                   //  $("#commonLayout_appcreshi").parent().html(data);
                    alert("data");
                }
            });*/
        }

        function btnaddItemClick(){
            $("#type1").attr("disabled",false);//这里
            $("#type2").attr("disabled",false);//这里

            $("#id").val("");   //数据会显 设置为空
            $("#name").val("");
            $("#comment").val("")
            $("#groupNames").val("");
            $("#type1").attr("disabled",false);//提交的时候将限制放开
            $("#type2").attr("disabled",false);//
            $("#dept").find('option[value=""]').attr("selected",true);
            $("#duid1").find('option[value=""]').attr("selected",true);
            $("#duid2").find('option[value=""]').attr("selected",true);
            $("#duid3").find('option[value=""]').attr("selected",true);

            var detailWin =  $.ligerDialog.open({ title:"添加部门",target:$("#Editdetail"), height: 600,width: 550,
                buttons: [
                    { text: '保存', onclick: function () { save(1); } },
                    { text: '取消', onclick: function () { detailWin.hide(); } }
                ]});
        }
        function modifyItemClick(){
            var row = manager.getSelectedRow();
            if (row == 'null' || !row) {
                $.ligerDialog.alert('请选择要修改的部门', '提示', 'warn');
                return;
            }
            $("#id").val(row.id);   //数据会显
            $("#name").val(row.name);
            $("#comment").val(row.comment)
            $("#groupNames").val(row.groupNames);

            $("#type1").attr("disabled",false);//提交的时候将限制放开
            $("#type2").attr("disabled",false);//
            if(row.dtype==2){        //二级部门,显示修改部门
                $("#div_type1").show();
                 $("#type2").attr("checked","checked");
            }else{
                $("#div_type1").hide();
                $("#type1").attr("checked","checked");
            }
            $("#type1").attr("disabled","true");//这里
            $("#type2").attr("disabled","true");//这里

            $("#dept").find('option[value="'+row.parentId+'"]').attr("selected",true);

            $("#duid1").find('option[value="'+row.duid1+'"]').attr("selected",true);
            $("#duid2").find('option[value="'+row.duid2+'"]').attr("selected",true);
            $("#duid3").find('option[value="'+row.duid3+'"]').attr("selected",true);

            var detailWin = $.ligerDialog.open({ title:"修改部门",target:$("#Editdetail"), height: 600,width: 550,
                buttons: [
                    { text: '保存', onclick: function () { save(2); } },
                    { text: '取消', onclick: function () { detailWin.hide();} }
                ]});
        }
        $(function(){
            $("#type2").click(function(){
                    $("#div_type1").show();
            });
            $("#type1").click(function(){
                $("#div_type1").hide();
            });

        })

    </script>
</head>
<body  style="padding:4px">
<div>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="btnaddItemClick()">添加部门</a>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="modifyItemClick()">修改部门</a>

 <%--   <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="getParent()">获取父节点</a>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="hasChildren()">是否有子节点</a>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="isLeaf()">是否叶节点节点</a>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="getSelected()">获取选中的值(选择行)</a>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="getData()">获取当前的值</a>
--%>
    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="expandAll()">展开全部</a>

    <a class="l-button" style="width:120px;float:left; margin-left:10px;" onclick="collapseAll()">收缩全部</a>
    <div class="l-clear"></div>

</div>

<div id="maingrid"></div>
<div>


</div>
<!--添加用户 -->
    <div id="Editdetail" style="display:none;">
        <form id="EditUserForm"  action="dept" method="post">
            <input type="hidden" name="op" id="op" >
            <input type="hidden" name="id" id="id">
            <table style="height: 450px">
                <tr>
                    <td style="align:right">部门名称：</td>
                    <td><input type="text" name="name" id="name"></td>
                </tr>
                <tr>
                    <td style="align:right">等级：</td>
                    <td>
                        <input type="radio" name="type" id="type1" value="1" checked="checked" >一级部门
                        <input type="radio" name="type" id="type2" value="2">二级部门

                    </td>
                </tr>

                <tr  id="div_type1"  style="display: none;">
                    <td style="align:right">选择一级部门:</td>
                    <td>
                        <select id="dept" name="parentId">
                            <option value="" >请选择部门</option>
                            <c:forEach items="${depts}" var="dept">
                                <option value="${dept.id}" >${dept.name}</option>
                            </c:forEach>
                        </select>

                    </td>
                </tr>
                <tr>
                    <td style="align:right">描述：</td>
                    <td>
                        <textarea rows="10%" cols="60%" name="comment" id="comment"></textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                        部门负责人1
                    </td>
                    <td>
                        <select id="duid1" name="duid1">
                            <option value="">请选择</option>
                            <c:forEach items="${tUsers}" var="tUser">
                                <option value="${tUser.id}" >${tUser.displayName}</option>
                            </c:forEach>
                        </select>

                    </td>
                </tr>
                <tr>
                    <td>
                        部门负责人2
                    </td>
                    <td>
                        <select id="duid2" name="duid2">
                            <option value="">请选择</option>
                            <c:forEach items="${tUsers}" var="tUser">
                                <option value="${tUser.id}">${tUser.displayName}</option>
                            </c:forEach>
                        </select>

                    </td>
                </tr>
                <tr>
                    <td>
                        部门负责人3
                    </td>
                    <td>
                        <select id="duid3" name="duid3">
                            <option value="">请选择</option>
                            <c:forEach items="${tUsers}" var="tUser">
                                <option value="${tUser.id}" <c:if test="${tUser.number eq number}">selected="selected"</c:if>>${tUser.displayName}</option>
                            </c:forEach>
                        </select>

                    </td>
                </tr>
            </table>
        </form>
</div>
    <%--yingjing.liu end add 20160725 --%>
</body>
</html>
