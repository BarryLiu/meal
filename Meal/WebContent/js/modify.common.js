/**
 * Created by zixiao.zhang on 2016/3/16.
 */

var Data = {};
Data.dialogWidth = 600;
Data.dialogHeight = 400;
Data.validate = function () {
    return true;
};
Data.submitHandler = function(form) {
};
Data.resetData = function(){
};
Data.afterOpenDialog = function() {
};
$(function ()
{
    $.metadata.setType("attr", "validate");
    Data.v = $("#form2").validate({
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
            if(Data.validate()) {
                $("form .l-text-field,.l-textarea").ligerHideTip();
                Data.submitHandler(form);
            }
        }
    });
});

function addItemClick(item)
{
    if(Data.dialog != undefined) {
        Data.dialog.show();
        return;
    }
    Data.dialog = $.ligerDialog.open({
            title:"新增",
            target:$("#add"),
            width:Data.dialogWidth,
            height:Data.dialogHeight/*$(window).height() * 0.9*/,
            buttons:[
                {text:'提交',onclick:function(item, dialog) {
                    Data.validate();
                    $("#form2").submit();
                }},
                {text:'取消',onclick:function(item, dialog){
                    $("form .l-text-field,.l-textarea").ligerHideTip();
                    Data.resetData();
                    Data.dialog.hide();
                }}
            ]
        }
    );
    Data.afterOpenDialog();
}

function modifyItemClick(item) {
}


