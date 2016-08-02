<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户</title>

    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/core/inject.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerMenuBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/ligerUI/lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>

    <link href="${pageContext.request.contextPath}/css/admin.content.css" rel="stylesheet" />
    <!-- for fcbkcomplete -->
    <link href="${pageContext.request.contextPath }/css/fcbkcomplete.style.css" rel="stylesheet" />
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/fcbkcomplete.min.js"></script>

    <script src="${pageContext.request.contextPath}/js/admin.js?t=1" type="text/javascript"></script>

    <style type="text/css">
        div.user div{
            float:left;
            margin-right:10px;
            margin-left:10px;
            padding:2px;
        }
        div.group {
            position:relative;
            line-height:30px;
        }
        div.group div.img-wrap {
            float:left;
            margin-top:5px;
            margin-left:45px;
            margin-right:15px;
            clear:left;
        }
        div.group div.content{
            float:left;
            clear:right;
        }
        #maingrid table td a{
            color:#395a7b;
            text-decoration:none;
        }
        #maingrid table td a:hover {
            text-decoration:underline;
        }
        #form2 table {
            margin-left:5px;
        }
    </style>

    <script type="text/javascript">
        $(function () {
            //表格
            var options = {elementId:'#assign',title:'权限分配'};
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '权限名称', isSort: 'false', width: 150, minWidth: 60, render: function(item) {
                        //var html ="<a href='javascript:;' onclick='Admin.openDialog({'title':'分配权限'},"+JSON.stringify(item)+")'>";
                        var html ="<a href='javascript:;' onclick='Admin.openDialog("+JSON.stringify(options)+","+JSON.stringify(item)+")'>";
                        html += item.perName;
                        html += "</a>";
                        return html;
                    } },
                    { display: '用户',align: 'center', isSort: 'false', width: 600, minWidth: 400, render: function(item) {
                        var html = "<div class='user'>";
                        for(var i=0;i<item.users.length;i++) {
                            html += "<div>"+item.users[i].displayName+"</div>";
                        }
                        html +="</div>";
                        return html;
                    } },
                    { display: '部门',align: 'center', isSort: 'false', width: 250, minWidth: 200, render: function(item) {
                        var html = "";
                        for(var i=0;i<item.groups.length;i++) {
                            html += "<div class='group'>"
                            html += "<div class='img-wrap'><img width='20px' height='20px' src='${pageContext.request.contextPath}/images/group.jpg'/></div>";
                            html += "<div class='content'><p>"+item.groups[i].groupName+"</p></div>";
                            html += "</div>";
                        }
                        return html;
                    } },
                    { display: '修改', name: 'number',align: 'center', isSort: 'false', width: 40, minWidth: 40, render: function(item) {
                        var html = "<div style='position:relative;height:35px;'>";
                        html += "<a class='opt' title='Modify' href='javascript:;' onclick='Admin.openDialog("+JSON.stringify(options)+","+JSON.stringify(item)+")'>";
                        html += "<span class='icon-sprite icon-edit'></span></a></div>";
                        return html;
                    } }
                ], dataAction: 'server', data: ${data},enabledEdit:false,
                width: '100%', height: '100%', pageSize: 30, rownumbers: false,
                checkbox: false,fixedCellHeight: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6
            });
            Admin.gridManager = $("#maingrid").ligerGetGridManager();
            $("#pageloading").hide();

            //$("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right'});
            //$("#eDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right'});
        });
        Admin.onDialogCancelClick = function(data) {

        };
        Admin.onDialogConfirmClick = function(data) {
            $("#form2").ajaxSubmit({
                type:"post",
                url:"${pageContext.request.contextPath}/permission",
                dataType:"json",
                success:function(data) {
                    Tip.tip(data.msg);
                    if(data.res) {
                        Admin.dialog.hide();
                        location.reload();
                    }
                },
            });
        };
        Admin.afterOpenDialog = function(data) {
            $("#perName").html(data.perName);
            $("#permissionId").val(data.id);
            initfacebooklist1(data);
            initfacebooklist2(data);
            var allUsers = eval('(${allUsers})');
            if(allUsers.length > 0) {
                for(var i=0;i<allUsers.length;i++) {
                    var user = allUsers[i];
                    user.caption = user.displayName;
                    user.value = user.id;
                }
                $.facebooklist('#user', '#preadded1', '#facebook-auto1', '#feed1', {data:allUsers}, 10, {userfilter:1,casesensetive:0}, 0, "#default1");
            }
            var allDepartments = eval('(${allDepartments})');
            if(allDepartments.length > 0) {
                for(var i=0;i<allDepartments.length;i++) {
                    var department = allDepartments[i];
                    department.caption = department.groupName;
                    department.value = department.id;
                }
                $.facebooklist('#department', '#preadded2', '#facebook-auto2', '#feed2', {data:allDepartments}, 10, {userfilter:1,casesensetive:0}, 0, "#default2");
            }
        }
    </script>
</head>
<body style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="user" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            权限分配
        </div>
    </div>

    <div id="maingrid" style="margin:0; padding:0">
    </div>

</form>

<div style="display:none" id="assign">
    <form name="form2" method="post" action="permission" id="form2">
        <table class="data-form" cellspacing="0" cellpadding="0">
            <tbody>
            <tr>
                <th scope="row">权限名称：</th>
                <td id="perName">
                </td>
            </tr>
            <tr>
                <th scope="row">用户：</th>
                <td>
                    <ol class="fcbk">
                        <li id="facebook-list1" class="input-text facebook-list">
                            <label></label>
                            <input type="text" value="无用户" id="user" name="userIds" class="input-normal" style="width:315px;" disabled="disabled"/>
                            <ul id="preadded1" style="display:none">
                                <c:forEach items="${userPermissions }" var="userPermission">
                                    <li rel="${userPermission.user.id }">${userPermission.user.name }</li>
                                </c:forEach>
                            </ul>
                            <div id="facebook-auto1" class="facebook-auto">
                                <div id="default1" class="default">Type the name of an User writer you like</div>
                                <ul id="feed1">
                                </ul>
                            </div>
                        </li>
                    </ol>
                </td>
            </tr>
            <tr>
                <th scope="row">部门：</th>
                <td>
                    <ol class="fcbk">
                        <li id="facebook-list2" class="input-text facebook-list">
                            <label></label>
                            <input type="text" value="没有部门" id="department" name="departmentIds" class="input-normal" style="width:315px;" disabled="disabled"/>
                            <ul id="preadded2" style="display:none">
                                <c:forEach items="${departmentPermissions }" var="departmentPermission">
                                    <li rel="${departmentPermission.department.id }">${departmentPermission.department.departmentName }</li>
                                </c:forEach>
                            </ul>
                            <div id="facebook-auto2" class="facebook-auto">
                                <div id="default2" class="default">Type the name of an Department writer you like</div>
                                <ul id="feed2">
                                </ul>
                            </div>
                        </li>
                    </ol>
                </td>
            </tr>
            <tr>
                <th>&nbsp;</th>
                <td><input type="hidden" name="op" value="assign">
                    <input type="hidden" id="permissionId" name="permissionId" value="0">
                </td>
            </tr>
            </tbody>
        </table>
    </form>
</div>
<script type="text/javascript">
    function initfacebooklist1(data) {
        $("#facebook-list1").empty();
        $("<label>").appendTo("#facebook-list1");
        $("<input>").attr({type:"text",value:"无用户",id:"user",name:"userIds",class:"input-normal",style:"width:315px",disabled:"disabled"}).appendTo("#facebook-list1");
        var preadded = $("<ul>").attr({id:"preadded1",style:"display:none"});
        preadded.appendTo("#facebook-list1");
        for(var i=0;i<data.users.length;i++) {
            var user = data.users[i];
            $("<li>").attr("rel",user.id).html(user.displayName).appendTo(preadded);
        }
        var auto1 = $("<div>").attr({id:"facebook-auto1",class:"facebook-auto"});
        auto1.appendTo("#facebook-list1");
        $("<div>").attr({id:"default1",class:"default"}).html("Type the name of an User writer you like").appendTo(auto1);
        $("<ul>").attr({id:"feed1"}).appendTo(auto1);
    }
    function initfacebooklist2(data) {
        $("#facebook-list2").empty();
        $("<label>").appendTo("#facebook-list2");
        $("<input>").attr({type:"text",value:"没有部门",id:"department",name:"departmentIds",class:"input-normal",style:"width:315px",disabled:"disabled"}).appendTo("#facebook-list2");
        var preadded = $("<ul>").attr({id:"preadded2",style:"display:none"});
        preadded.appendTo("#facebook-list2");
        for(var i=0;i<data.groups.length;i++) {
            var group = data.groups[i];
            $("<li>").attr("rel", group.id).html(group.groupName).appendTo(preadded);
        }
        var auto2 = $("<div>").attr({id:"facebook-auto2",class:"facebook-auto"});
        auto2.appendTo("#facebook-list2");
        $("<div>").attr({id:"default2",class:"default"}).html("Type the name of an Department writer you like").appendTo(auto2);
        $("<ul>").attr({id:"feed2"}).appendTo(auto2);
    }
</script>
<div style="display:none;">

</div>

</body>
</html>
