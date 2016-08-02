/**
 * Created by zixiao.zhang on 2016/3/16.
 */

var Tip = {};

Tip.tip = function(content) {
    var t = $.ligerDialog.tip({
       content:content
    });
    setTimeout(function() {
        t.close();
    },2000);
};

$(function() {
    $.ajaxSetup({
       type:"post",
        dataType:"json",
        error:function(jqXHR, textStatus, errorThrown) {
            switch (jqXHR.status) {
                case(500):
                    $.ligerDialog.error("服务器系统内部错误");
                    break;
                case(401):
                    $.ligerDialog.error("未登录");
                    break;
                case(403):
                    $.ligerDialog.error("无权限执行此操作");
                    break;
                case(408):
                    $.ligerDialog.error("请求超时");
                    break;
                default:
                    $.ligerDialog.error("未知错误");
            }
        }
    });
});

var Admin = {};
Admin.onDialogConfirmClick = function(data) {}
Admin.onDialogCancelClick = function(data) {}
Admin.afterOpenDialog = function(data) {}
Admin.openDialog = function(options, data) {
    if(Admin.dialog != undefined) {
        Admin.dialog.show();
        Admin.afterOpenDialog(data);
        return;
    }
    var defaults = {
        title:"新增",
        elementId:"#add",
        dialogWidth:600,
        dialogHeight:400
    }
    var opts = $.extend(defaults, options);
    Admin.dialog = $.ligerDialog.open({
            title:opts.title,
            target:$(opts.elementId),
            width:opts.dialogWidth,
            height:opts.dialogHeight/*$(window).height() * 0.9*/,
            buttons:[
                {text:'提交',onclick:function(item, dialog) {
                    Admin.onDialogConfirmClick(data);
                }},
                {text:'取消',onclick:function(item, dialog){
                    Admin.onDialogCancelClick(data);
                    dialog.hide();
                }}
            ]
        }
    );
    Admin.afterOpenDialog(data);
};
//返回 yyyy/MM/dd,date是日期对象
Admin.formatToDate = function (date) {
    var dateStr = date.getFullYear();
    dateStr += "/";
    var month = date.getMonth(); //日期0-11
    month++;
    if(month < 10) {
        dateStr += "0";
    }
    dateStr += month;
    dateStr += "/";
    var day = date.getDate(); //1-31
    if(day < 10) {
        dateStr += "0";
    }
    dateStr += day;
    return dateStr;
}
