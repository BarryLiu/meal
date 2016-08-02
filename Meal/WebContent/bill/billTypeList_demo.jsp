<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>

    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery/jquery-1.9.0.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerForm.js" type="text/javascript"></script>
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

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603161402" type="text/javascript"></script>

    <script type="text/javascript">
        var gridManager = null;
        var typeData = [{Type:0,text:'调休'},{Type:1,text:'不调休'}];
        $(function () {
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '单据名称', name: 'billName', isSort: 'true', width: 150, minWidth: 60, editor:{type:'text', onChange:function(item) {
                        var before = item.record.billName;
                        var after = item.value;
                        if(after == '') {
                            item.value = before;
                        } else if(before != after) {
                            var id = item.record.id;
                            DWREngine.setAsync(false);
                            dwr.updateBillName(id, after, {
                                callback:function(data) {
                                    var res = eval('('+data+')')
                                    if(!res.res) {
                                        item.value = before;
                                        $.ligerDialog.error(res.msg);
                                    } else {
                                        Tip.tip("修改成功！");
                                    }
                                },
                                errorHandler:function() {
                                    item.value = before;
                                    $.ligerDialog.error("Error!!!");
                                }
                            });
                            DWREngine.setAsync(true);
                        }
                        }}, render: function(item){
                        if(item.billName == "")
                        {return "请输入单据名称"}
                        else {return item.billName}} },
                    { display: '单据类型', name: 'billType', align: 'center',isSort: 'true',type:'int', width: 100, minWidth: 60, render: function(item) {
                        if(item.billType==null || item.billType==0) {
                            return "调休";
                        } else if(item.billType == 1) {
                            return "不调休";
                        } else {
                            return item.billType;
                        }
                        },editor:{type:'select',data:typeData, valueField:'Type',textField:'text',onChange:function(item) {
                        var before = item.record.billType;
                        var after = item.value;
                        if(after == '') {
                            item.value = before; //数据回退
                        } else if(before != after) {
                            var id = item.record.id;
                            DWREngine.setAsync(false);
                            dwr.updateBillType(id, after, {
                                callback:function(data) {
                                    var res = eval('('+data+')');
                                    if(!res.res) {
                                        item.value = before; //数据回退
                                        $.ligerDialog.error(res.msg);
                                    } else {
                                        Tip.tip("修改成功！");
                                    }
                                },
                                errorHandler:function() {
                                    item.value = before; //数据回退
                                    $.ligerDialog.error("Error!!!");
                                }
                            });
                            DWREngine.setAsync(true);
                        }
                        return true;
                    }} }
                ], dataAction: 'server', data: ${data}, sortName: 'billName',enabledEdit:true,
                width: '100%', height: '100%', pageSize: 30, rownumbers: false,
                checkbox: true,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6,
                toolbar: { items: [
                    { text: '增加', click: addItemClick, icon: 'add' },
                    /*{ line: true },
                    { text: '修改', click: modifyItemClick, icon: 'modify' },*/
                    { line: true },
                    { text: '删除', click: deleteItemClick, img: '${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/icons/delete.gif' }
                ]
                }
            });

            gridManager = $("#maingrid").ligerGetGridManager();

            $("#pageloading").hide();
        });

        var m;
        $(function ()
        {
            $.metadata.setType("attr", "validate");
            var v = $("#form2").validate({
                debug: false,
                errorPlacement: function (lable, element)
                {
                    if (element.hasClass("l-textarea"))
                    {
                        element.ligerTip({ content: lable.html(), target: element[0] });
                    }
                    else if (element.hasClass("l-text-field"))
                    {
                        element.parent().ligerTip({ content: lable.html(), target: element[0] });
                    }
                    else
                    {
                        lable.appendTo(element.parents("td:first").next("td"));
                    }
                },
                success: function (lable)
                {
                    lable.ligerHideTip();
                    lable.remove();
                },
                submitHandler: function (form)
                {
                    $("form .l-text,.l-textarea").ligerHideTip();
                    alert("Submitted!");
                    m.hide();
                    form.reset();
                }
            });
        });

        function addItemClick(item)
        {
            if(m != undefined) {
                m.show();
                return;
            }
            m = $.ligerDialog.open({
                        title:"新增",
                        target:$("#add"),
                        width:700,
                        height:/*$(window).height() * 0.9*/500,
                        buttons:[
                            {text:'提交',onclick:function(item, dialog) {
                                $("#form2").submit();
                            }},
                            {text:'取消',onclick:function(item, dialog){
                                $("form .l-text,.l-textarea").ligerHideTip();
                                m.hide();
                                $("#form2")[0].reset();
                            }}
                        ]
                    }
            );
            $("#form2").ligerForm();
        }

        function modifyItemClick(item) {
            //TODO ...
        }

        function deleteItemClick(item) {
            var rows = gridManager.getSelectedRows();
            if(rows != '') {
                $.ligerDialog.confirm("The operation can't be restored,are you sure to delete the selected items?", function(type) {
                    if(type) {
                        var da = "op=delete";
                        for(var i=0;i<rows.length;i++) {
                            da += "&ids="+rows[i].id;
                        }
                        //location.href = "${pageContext.request.contextPath}/billtype?op=delete&ids="+ids;
                        $.ajax({
                            type:"post",
                            url:"${pageContext.request.contextPath}/billtype",
                            data:da,
                            dataType:"json",
                            async:false,
                            success:function(data) {
                                if(data.res) {
                                    gridManager.deleteSelectedRow();
                                    Tip.tip("删除成功！");
                                }
                            },
                            error:function() {
                                $.ligerDialog.error("Error!!!");
                            }
                        });
                    }
                });
            } else {
                $.ligerDialog.alert("please select one item at least!");
            }
        }
    </script>
    <style type="text/css">
        body{ font-size:12px;}
        .l-table-edit {}
        .l-table-edit-td{ padding:4px;}
        .l-button-submit,.l-button-test{width:80px; float:left; margin-left:10px; padding-bottom:2px;}
        .l-verify-tip{ left:230px; top:120px;}
    </style>
</head>
<body style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="user" method="post">
    <div id="toptoolbar"></div>
    <!--
    <div class="l-panel-search">
        <a class="l-button" style="width:120px;float:left;margin-left:10px" onclick="deleteRow()">删除选择的行</a>
        <a class="l-button" style="width:100px;float:left;margin-left:10px" onclick="addNewRow()">添加行</a>
        <a class="l-button" style="width:100px;float:left;margin-left:10px" onclick="editRow()">修改行</a>
        <a class="l-button" style="width:100px;float:left;margin-left:10px" onclick="cancelAll()">取消所有修改</a>
    </div>
    -->
    <div id="maingrid" style="margin:0; padding:0;"></div>

</form>

<div style="display:none;" id="add">
    <form name="form2" method="post" action="../service/EmployeeEdit.aspx?ID=0" id="form2">
        <div>
        </div>
        <table cellpadding="0" cellspacing="0" class="l-table-edit" >
            <tr>
                <td align="right" class="l-table-edit-td">名字:</td>
                <td align="left" class="l-table-edit-td"><input name="txtName" type="text" id="txtName" class="l-text" ltype="text" validate="{required:true,minlength:3,maxlength:10}" /></td>
                <td align="left"></td>
            </tr>

            <tr>
                <td align="right" class="l-table-edit-td" valign="top">性别:</td>
                <td align="left" class="l-table-edit-td">
                    <input id="rbtnl_0" type="radio" name="rbtnl" value="1" checked="checked" /><label for="rbtnl_0">男</label> <input id="rbtnl_1" type="radio" name="rbtnl" value="2" /><label for="rbtnl_1">女</label>
                </td><td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">Email:</td>
                <td align="left" class="l-table-edit-td"><input name="txtEmail" type="text" class="l-text" id="txtEmail" ltype="text" validate="{required:true,email:true}" /></td>
                <td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td" valign="top">爱好:</td>
                <td align="left" class="l-table-edit-td">
                    <input id="CheckBoxList1_0" type="checkbox" name="CheckBoxList1$0" checked="checked" /><label for="CheckBoxList1_0">篮球</label><br /><input id="CheckBoxList1_1" type="checkbox" name="CheckBoxList1$1" /><label for="CheckBoxList1_1">网球</label> <br /><input id="CheckBox1" type="checkbox" name="CheckBoxList1$1" /><label for="CheckBoxList1_1">足球</label>
                </td><td align="left"></td>
            </tr>

            <tr>
                <td align="right" class="l-table-edit-td">入职日期:</td>
                <td align="left" class="l-table-edit-td">
                    <input name="txtDate" type="text" id="txtDate" ltype="date" class="l-text" validate="{required:true}" />
                </td><td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">年龄:</td>
                <td align="left" class="l-table-edit-td">
                    <input name="txtAge" type="text" id="txtAge" ltype='spinner' class="l-text" ligerui="{type:'int'}" value="20" class="required" validate="{digits:true,min:1,max:100}" />
                </td><td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">部门:</td>
                <td align="left" class="l-table-edit-td">
                    <select name="ddlDepart" id="ddlDepart">
                        <option value="1">主席</option>
                        <option value="2">研发中心</option>
                        <option value="3">销售部</option>
                        <option value="4">市场部</option>
                        <option value="5">顾问组</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">地址:</td>
                <td align="left" class="l-table-edit-td">
                    <textarea cols="100" rows="4" class="l-textarea" id="address" style="width:400px" validate="{required:true}" ></textarea>
                </td><td align="left"></td>
            </tr>
        </table>
        <br />
        <!--
        <input type="submit" value="提交" id="Button1" class="l-button l-button-submit" />
        <input type="button" value="测试" class="l-button l-button-test"/>
        -->
    </form>
</div>

<div style="display:none;">
</div>

</body>
</html>
