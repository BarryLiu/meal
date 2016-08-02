/*js保存COOKIE，直接给document加上cookie就可以了，但是一般如果单个的加会很麻烦所以一般会直接写好一个函数，可以直接操作cookie，这样就很方便了
setCookie这个是写入cookie，第一个是名称，第二个是cookie值，第三个是过期时间
getCookie这个是查找cookie;
removeCookie这是你需要删除的cookie;*/
function setCookie(name, value, iSecond)
{
    var oDate=new Date();

    oDate.setSeconds(oDate.getSeconds()+iSecond);

    document.cookie=name+'='+encodeURIComponent(value)+';expires='+oDate;
}

function getCookie(name)
{
    var arr=document.cookie.split('; ');
    var i=0;
    for(i=0;i<arr.length;i++)
    {
        //arr2->['username', 'abc']
        var arr2=arr[i].split('=');

        if(arr2[0]==name)
        {
            var getC = decodeURIComponent(arr2[1]);
            return getC;
        }
    }

    return '';
}

function removeCookie(name)
{
    setCookie(name, '1', -1);
}


function subWindow(){
    	win = window.open("",  CHILD_WINDOW_NAME, CHILD_WINDOW_ATTRS);
    	if (win.location.href === "about:blank") {
        	    //窗口不存在
        	    win = window.open(CHILD_WINDOW_URL,  CHILD_WINDOW_NAME, CHILD_WINDOW_ATTRS);
        	} else {
                //窗口以已经存在了
            win.focus();
        	}

}


//  yingjing.liu 2016/06/14 cookie 保存 1s  页面 作用于 页面回显
addCookie = function (){
    setCookie("currPage",gridManager.options.page,1);//在session中保存1秒钟
}
toPage = function(){
    var currPage = getCookie("currPage")
    if(currPage==""||currPage==null)return;
    gridManager.options.page=currPage-1;
    gridManager.changePage('next');
    removeCookie("currPage") //一次用完后删除
}
// yingjing.liu 2016/06/14 end