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
    <script src="${pageContext.request.contextPath}/js/jquery.form.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerForm.js?t=7" type="text/javascript"></script>
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

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172046" type="text/javascript"></script>

    <script type="text/javascript">
        var gridManager = null;
        var typeData = [{Type:0,text:'调休'},{Type:1,text:'不调休'},{Type:2,text:"扣薪"},{Type:3,text:"考勤补充"}];
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
                        } else if(item.billType == 2) {
                            return "扣薪";
                        } else if(item.billType == 3) {
                            return "考勤补充";
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
                    { text: '删除', click: deleteItemClick, img: '${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/icons/delete.gif' },
                    {line: true},
                    {text: '说明', click: helpItemClick, icon: 'help'}
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
                    addItem();
                }
            });
        });

        function addItem() {
            $("#form2").ajaxSubmit({
                type:"post",
                url:"${pageContext.request.contextPath}/billtype",
                dataType:"json",
                success:function(data) {
                    if(data.res) {
                        Tip.tip("添加成功！");
                        m.hide();
                        resetData();
                        gridManager.addRow(data.data);
                    } else {
                        $.ligerDialog.error(data.msg);
                        //$("#billName").ligerTip({content:data.msg, target:this});
                        //Tip.tip(data.msg);
                    }
                }
            });
        }

        function addItemClick(item)
        {
            if(m != undefined) {
                m.show();
                return;
            }
            m = $.ligerDialog.open({
                        title:"新增",
                        target:$("#add"),
                        width:480,
                        height:/*$(window).height() * 0.9*/250,
                        buttons:[
                            {text:'提交',onclick:function(item, dialog) {
                                $("#form2").submit();
                            }},
                            {text:'取消',onclick:function(item, dialog){
                                $("form .l-text,.l-textarea").ligerHideTip();
                                m.hide();
                                resetData();
                            }}
                        ]
                    }
            );
            $("#form2").ligerForm();
        }

        function modifyItemClick(item) {
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

        function resetData() {
            $("#form2")[0].reset();
            var form2 = liger.get("form2");
            form2.setData({"pms.billType":"0"}); //目前系統有個限制：值不能為false，比如空字符串。數字0，變量false等。具體限制在ligerForm.js的setData方法中
        }

        function helpItemClick() {
            $.ligerDialog.alert("<b>调休</b>：如果本月加班时间够，可以用加班时间抵请假时间，加班时间不够则扣薪。<br/><br/>" +
                    "<b>不调休</b>：病假、婚假等法定假期及出差等，按正常上班计，不用调休，不扣薪。<br/><br/>" +
                    "<b>扣薪</b>：直接扣薪，即使本月加班时间足够。<br/>注：该情况目前系统中按调休处理。<br/><br/>" +
                    "<b>考勤补充</b>：一般是指未打卡记录，会重新计算考勤，有餐补和加班时间，同时不会出现在考勤异常里，是考勤的一种修正。<br/><br/>" +
                    "<b><font color='red'>该表数据原则上不能修改，若需要修改请与系统维护人员确认后再修改。因为考虑到统计和以前的数据，有可能改动后要更改后台代码或后台数据库数据！</font></b>","单据类型说明",'question',function() {
            },{width:400});
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
    <form name="form2" method="post" action="bill" id="form2">
        <div>
        </div>
        <table cellpadding="0" cellspacing="0" class="l-table-edit" >
            <tr>
                <td align="right" class="l-table-edit-td">单据名称:</td>
                <td align="left" class="l-table-edit-td"><input name="pms.billName" type="text" id="billName" class="l-text" ltype="text" validate="{required:true,maxlength:30}" /></td>
                <td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">单据类型:</td>
                <td align="left" class="l-table-edit-td">
                    <select name="pms.billType" id="billType" ltype="select">
                        <option value="0" selected="selected">调休</option>
                        <option value="1">不调休</option>
                        <option value="2">扣薪</option>
                        <option value="3">考勤补充</option>
                    </select>
                </td>
            </tr>
        </table>
        <br />
        <input type="hidden" ltype="none" name="op" value="add" />
    </form>
</div>

<div style="display:none;">
</div>

</body>
</html>
