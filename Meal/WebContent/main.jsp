<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2014/9/2
  Time: 15:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<title>欢迎使用麦穗科技餐补系统</title>
<link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" id="mylink"/>
<script src="params.js" type="text/javascript"></script>
<script src="ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="ligerUI/lib/jquery/jquery.utils.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerMenuBar.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
<script src="ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/ligerui.all.js" type="text/javascript"></script>
<script src="ligerUI/lib/ligerUI/js/plugins/ligerTab.js"></script>
<script src="ligerUI/lib/jquery.cookie.js"></script>
<script src="ligerUI/lib/json2.js"></script>
<script src="indexdata.js?t=201603091650" type="text/javascript"></script>
<script type="text/javascript">

    var tab = null;
    var accordion = null;
    var tree = null;
    var tabItems = [];
    var allPer = "";//
    $(function () {
        //布局
        $("#layout1").ligerLayout({ leftWidth: 190, height: '100%', heightDiff: -34, space: 4, onHeightChanged: f_heightChanged });

        var height = $(".l-layout-center").height();

        //Tab
        $("#framecenter").ligerTab({
            height: height,
            showSwitchInTab: true,
            showSwitch: true,
            onAfterAddTabItem: function (tabdata) {
                tabItems.push(tabdata);
                saveTabStatus();
            },
            onAfterRemoveTabItem: function (tabid) {
                for (var i = 0; i < tabItems.length; i++) {
                    var o = tabItems[i];
                    if (o.tabid == tabid) {
                        tabItems.splice(i, 1);
                        saveTabStatus();
                        break;
                    }
                }
            },
            onReload: function (tabdata) {
                var tabid = tabdata.tabid;
                addFrameSkinLink(tabid);
            }
        });

        //面板
        $("#accordion1").ligerAccordion({ height: height - 24, speed: null });

        $(".l-link").hover(function () {
            $(this).addClass("l-link-over");
        }, function () {
            $(this).removeClass("l-link-over");
        });
        //树
        var treeData = indexdata1;
        var userDepart = '${userInfo.depart}';
        if (userDepart == "MANAGER") {
            treeData = indexdata2;
        } else if (userDepart == "SUPPER") {
            treeData = indexdata3;
        }
        //alert("${userInfo.number}:${userInfo.id}:${userInfo.username}");
        treeData = indexdata3; //add 20160314 tmp
        treeData = eval("(${sessionScope.treeData})");

        $("#tree1").ligerTree({
            data: treeData,
            checkbox: false,
            slide: false,
            nodeWidth: 120,
            attribute: ['nodename', 'url'],
            onSelect: function (node) {
                if (!node.data.url) return;
                /*
                var tabid = $(node.target).attr("tabid");
                if (!tabid) {
                    tabid = new Date().getTime();
                    $(node.target).attr("tabid", tabid)
                }
                <c:forEach items="${lstPer}" var="per">
                if('${per.link}'==node.data.url){
                    tabid = '${per.code}';
                    tabid = tabid.replace('-','');
                }
                </c:forEach>
                f_addTab(tabid, node.data.text, node.data.url);
                */
                f_addTab(node.data.tabid, node.data.text, node.data.url);
            },
            onCancelselect: function (node) {
                if (!node.data.url) return;
                /*
                var tabid = $(node.target).attr("tabid");
                if (!tabid) {
                    tabid = new Date().getTime();
                    $(node.target).attr("tabid", tabid)
                }
                <c:forEach items="${lstPer}" var="per">
                if('${per.link}'==node.data.url){
                    tabid = '${per.code}';
                    tabid = tabid.replace('-','');
                }
                </c:forEach>
                f_addTab(tabid, node.data.text, node.data.url);*/
                f_addTab(node.data.tabid, node.data.text, node.data.url);
            }
        });

        tab = liger.get("framecenter");
        accordion = liger.get("accordion1");
        tree = liger.get("tree1");
        $("#pageloading").hide();

        css_init();
        pages_init();
    });
    function f_heightChanged(options) {
        if (tab)
            tab.addHeight(options.diff);
        if (accordion && options.middleHeight - 24 > 0)
            accordion.setHeight(options.middleHeight - 24);
    }
    function f_addTab(tabid, text, url) {
        tab.addTabItem({
            tabid: tabid,
            text: text,
            url: url,
            callback: function () {
                // addShowCodeBtn(tabid);
                addFrameSkinLink(tabid);
            }
        });
    }
    function addShowCodeBtn(tabid) {
        var viewSourceBtn = $('<a class="viewsourcelink" href="javascript:void(0)">预览源码</a>');
        var jiframe = $("#" + tabid);
        viewSourceBtn.insertBefore(jiframe);
        viewSourceBtn.click(function () {
            showCodeView(jiframe.attr("src"));
        }).hover(function () {
            viewSourceBtn.addClass("viewsourcelink-over");
        }, function () {
            viewSourceBtn.removeClass("viewsourcelink-over");
        });
    }
    function showCodeView(src) {
        $.ligerDialog.open({
            title: '源码预览',
            url: 'dotnetdemos/codeView.aspx?src=' + src,
            width: $(window).width() * 0.9,
            height: $(window).height() * 0.9
        });

    }
    function addFrameSkinLink(tabid) {
        var prevHref = getLinkPrevHref(tabid) || "";
        var skin = getQueryString("skin");
        if (!skin) return;
        skin = skin.toLowerCase();
        attachLinkToFrame(tabid, prevHref + skin_links[skin]);
    }
    var skin_links = {
        "aqua": "lib/ligerUI/skins/Aqua/css/ligerui-all.css",
        "gray": "lib/ligerUI/skins/Gray/css/all.css",
        "silvery": "lib/ligerUI/skins/Silvery/css/style.css",
        "gray2014": "lib/ligerUI/skins/gray2014/css/all.css"
    };
    function pages_init() {
        var tabJson = $.cookie('liger-home-tab');
        if (tabJson) {
            var tabitems = JSON2.parse(tabJson);
            for (var i = 0; tabitems && tabitems[i]; i++) {
                f_addTab(tabitems[i].tabid, tabitems[i].text, tabitems[i].url);
            }
        }
    }
    function saveTabStatus() {
        $.cookie('liger-home-tab', JSON2.stringify(tabItems));
    }
    function css_init() {
        var css = $("#mylink").get(0), skin = getQueryString("skin");
        $("#skinSelect").val(skin);
        $("#skinSelect").change(function () {
            if (this.value) {
                location.href = "index.htm?skin=" + this.value;
            } else {
                location.href = "index.htm";
            }
        });


        if (!css || !skin) return;
        skin = skin.toLowerCase();
        $('body').addClass("body-" + skin);
        $(css).attr("href", skin_links[skin]);
    }
    function getQueryString(name) {
        var now_url = document.location.search.slice(1), q_array = now_url.split('&');
        for (var i = 0; i < q_array.length; i++) {
            var v_array = q_array[i].split('=');
            if (v_array[0] == name) {
                return v_array[1];
            }
        }
        return false;
    }
    function attachLinkToFrame(iframeId, filename) {
        if (!window.frames[iframeId]) return;
        var head = window.frames[iframeId].document.getElementsByTagName('head').item(0);
        var fileref = window.frames[iframeId].document.createElement("link");
        if (!fileref) return;
        fileref.setAttribute("rel", "stylesheet");
        fileref.setAttribute("type", "text/css");
        fileref.setAttribute("href", filename);
        head.appendChild(fileref);
    }
    function getLinkPrevHref(iframeId) {
        if (!window.frames[iframeId]) return;
        var head = window.frames[iframeId].document.getElementsByTagName('head').item(0);
        var links = $("link:first", head);
        for (var i = 0; links[i]; i++) {
            var href = $(links[i]).attr("href");
            if (href && href.toLowerCase().indexOf("ligerui") > 0) {
                return href.substring(0, href.toLowerCase().indexOf("lib"));
            }
        }
    }

    // 退出
    logout = function () {
        $.ligerDialog.confirm('您确定要退出当前系统吗？', function (yes) {
            window.location.href = "meal/login?op=other";
        });
    }
</script>
<style type="text/css">
    body, html {
        height: 100%;
    }

    body {
        padding: 0px;
        margin: 0;
        overflow: hidden;
    }

    .l-link {
        display: block;
        height: 26px;
        line-height: 26px;
        padding-left: 10px;
        text-decoration: underline;
        color: #333;
    }

    .l-link2 {
        text-decoration: underline;
        color: white;
        margin-left: 2px;
        margin-right: 2px;
    }

    .l-layout-top {
        background: #102A49;
        color: White;
    }

    .l-layout-bottom {
        background: #E5EDEF;
        text-align: center;
    }

    #pageloading {
        position: absolute;
        left: 0px;
        top: 0px;
        background: white url('ligerUI/lib/images/loading.gif') no-repeat center;
        width: 100%;
        height: 100%;
        z-index: 99999;
    }

    .l-link {
        display: block;
        line-height: 22px;
        height: 22px;
        padding-left: 16px;
        border: 1px solid white;
        margin: 4px;
    }

    .l-link-over {
        background: #FFEEAC;
        border: 1px solid #DB9F00;
    }

    .l-winbar {
        background: #2B5A76;
        height: 30px;
        position: absolute;
        left: 0px;
        bottom: 0px;
        width: 100%;
        z-index: 99999;
    }

    .space {
        color: #E7E7E7;
    }

    /* 顶部 */
    .l-topmenu {
        margin: 0;
        padding: 0;
        height: 31px;
        line-height: 31px;
        background: url('ligerUI/lib/images/top.jpg') repeat-x bottom;
        position: relative;
        border-top: 1px solid #1D438B;
    }

    .l-topmenu-logo {
        color: #E7E7E7;
        padding-left: 35px;
        line-height: 26px;
        background: url('ligerUI/lib/images/topicon.gif') no-repeat 10px 5px;
    }

    .l-topmenu-welcome {
        position: absolute;
        height: 24px;
        line-height: 24px;
        right: 30px;
        top: 2px;
        color: #070A0C;
    }

    .l-topmenu-welcome a {
        color: #E7E7E7;
        text-decoration: underline
    }

    .body-gray2014 #framecenter {
        margin-top: 3px;
    }

    .viewsourcelink {
        background: #B3D9F7;
        display: block;
        position: absolute;
        right: 10px;
        top: 3px;
        padding: 6px 4px;
        color: #333;
        text-decoration: underline;
    }

    .viewsourcelink-over {
        background: #81C0F2;
    }

    .l-topmenu-welcome label {
        color: white;
    }

    #skinSelect {
        margin-right: 6px;
    }
</style>
</head>
<body style="padding:0px;background:#EAEEF5;">
<div id="pageloading"></div>
<div id="layout1" style="width:99.2%; margin:0 auto; margin-top:4px; ">
    <div position="left" title="主要菜单" id="accordion1">
        <div title="功能列表" class="l-scroll">
            <ul id="tree1" style="margin-top:3px;"></ul>
        </div>
    </div>
    <div position="center" id="framecenter">
        <div tabid="home" title="主页" style="height:300px">
            <iframe frameborder="0" name="home" id="home" src="home.jsp"></iframe>
        </div>
    </div>
</div>
<div style="height:32px; line-height:32px; text-align:center;">
    <table width="100%" heigth="100%" border="0px" style="text-align: center;font-size: 12px;margin-top: 8px">
        <tr>
            <td align="left" width="20%">
                &nbsp;&nbsp;<font color="red">${userInfo.name}</font> 您好！
                <a href="login?op=other" >退出</a>
            </td>
            <td width="60%">
                麦穗科技 版权所有
            </td>
            <td width="20%">
                &nbsp;
            </td>
        </tr>
    </table>
</div>
<div style="display:none"></div>
</body>
</html>
