/**
 * Created by kui.li on 2014/10/11.
 */
var tabItemIds=
{
    system:1, //系统管理菜单
        system_user:11, // 用户管理
            system_user_assign:111, // 用户授权
            system_user_assign_permission:112, // 特殊权限
        system_role:12, // 角色管理
            system_role_add:121, // 角色添加
            system_role_edit:122, // 角色修改
            system_role_delete:123, // 角色删除
            system_role_assign:124, // 角色授权
            system_role_view:125, // 角色预览
    platform_manager:2, // 平台维护
        platfrom:21, // 平台维护
            platform_add:211,//平台添加
            platform_edit:212,//平台修改
            platform_delete:213,//平台删除
            platform_view:214, // 平台预览
        sourcedb:22, // 代码库
            sourcedb_add:221,//代码库添加
            sourcedb_edit:222,//代码库修改
            sourcedb_delete:223,//代码库删除
            sourcedb_view:224, // 代码库预览
        branch:23,// 分支
            branch_add:231,//分支添加
            branch_edit:232,//分支修改
            branch_delete:233,//分支删除
            branch_view:234, // 分支预览
        eservice:24,// EService
            eservice_add:241,//EService添加
            eservice_edit:242,//EService修改
            eservice_delete:243,//EService删除
            eservice_view:244, // EService预览

    project_manager:3, // 项目管理
        project:31, // 项目管理
            project_add:311,//项目添加
            project_edit:312,//项目修改
            project_delete:313,//项目删除
            project_view:314, // 项目预览
        customer:32, // 客户
            customer_add:321,//客户添加
            customer_edit:332,//客户修改
            customer_delete:323,//客户删除
            customer_view:324, // 客户预览
        customer_project:33, // 客户项目
            customer_project_add:331,//客户项目添加
            customer_project_edit:332,//客户项目修改
            customer_project_delete:333,//客户项目删除
            customer_project_view:334, // 项目预览
            customer_project_clone:335,//客户项目克隆
            customer_project_pcb:336,//主板调试任务
            customer_project_hw:337, // 器件调试任务
        issues:34, // issues
            issues_add:341,//issues添加
            issues_edit:342,//issues修改
            issues_delete:343,//issues删除
            issues_view:344, // issues预览
        jiedian:35, // 项目节点
        pcb:36, // 主板调试
            pcb_edit:361, // 修改
            pcb_clone:362, // 克隆
        hw:37, // 器件调试
            hw_edit:371, // 修改
            hw_clone:372, // 克隆
    config:4, // 配置管理
        config_project:41, // 项目配置
            config_project_add:411,
            config_project_edit:412,
            config_project_delete:413,
            config_project_view:414,
        config_issues:42, // 问题状态
            config_issues_add:421,
            config_issues_edit:422,
            config_issues_delete:423,
            config_issues_view:424,
        config_hw_type:43, // 器件类型
            config_hw_type_add:431,
            config_hw_type_edit:432,
            config_hw_type_delete:433,
            config_hw_type_view:434,
        config_hw_params:44, // 器件参数
            config_hw_params_add:441,
            config_hw_params_edit:442,
            config_hw_params_delete:443,
            config_hw_params_view:444,
        config_sync:45, // 数据同步
            config_sync_user:441,
        config_pcb_model:46, // 主板调试
            config_pcb_model_add:461,
            config_pcb_model_edit:462,
            config_pcb_model_delete:463,
            config_pcb_model_view:464
}

var paramTypes=[{id:1,text:'参数，屏的分辨率&Camera AF/FF 像素大小'},{id:2,text:'供应商名'},{id:3,text:'IC型号'},{id:4,text:'模组型号'}];

var allPer = '';
setAllPer = function (data) {
    allPer = data;
}
checkPermission = function (per,id) {
    return per.indexOf("," + id + ",")>0;
}

function duibi(a, b) {
    var arr = a.split("-");
    var starttime = new Date(arr[0], arr[1], arr[2]);
    var starttimes = starttime.getTime();

    var arrs = b.split("-");
    var lktime = new Date(arrs[0], arrs[1], arrs[2]);
    var lktimes = lktime.getTime();

    if (starttimes >= lktimes) {
        return false;
    }
    else
        return true;
}