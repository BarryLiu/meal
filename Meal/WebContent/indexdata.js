var indexdata1 =
[
    { text: '我的菜单',isexpand:true, children: [
		{url:"mySupplement",text:"我的餐补"}
	]
    }
];


var indexdata2 =
    [
        { text: '我的菜单',isexpand:true, children: [
            {url:"mySupplement",text:"我的餐补"},
            {url:"kaoqin",text:"我的考勤"},
            {url:"search",text:"统计查询"}
        ]
        }
    ];
/*
var indexdata3 =
    [
        { text: '我的菜单',isexpand:true, children: [
            {url:'user',text:"用户"},
            {url:"mySupplement",text:"我的餐补"},
            {url:"kaoqin",text:"我的考勤"},
            {url:"upload.jsp",text:"上传补助信息"},
            {url:"search",text:"统计查询"}
        ]
        },
        { text: '后台管理', isexpand:true, children: [
            {url:'bill', text:"单据列表"},
            {url:'billtype', text:"单据类型"},
            {url:'leave', text:"异常时间"}
        ]
        }
];
*/
var indexdata3 =
    [
        { text: '用户管理', isexpand:false, children: [
            {url:'user', text:"用户", tabid:100101},
            {url:'permission', text:"权限", tabid:100102}
        ]},
        { text: '我的菜单',isexpand:true, children: [
            {url:"kaoqin",text:"我的考勤", tabid:100201},
            {url:"mySupplement",text:"我的餐补", tabid:100202},
            {url:"myleave", text:"我的考勤异常", tabid:100203},
            {url:"mystatistic", text:"我的统计", tabid:100204}
        ]},
        { text: '统计查询',isexpand:true, children: [
            {url:"search",text:"所有餐补",tabid:100301},
            {url:"leave",text:"所有考勤异常",tabid:100302},
            {url:"statistic",text:"所有统计",tabid:100303}
        ]},
        { text: '后台管理', isexpand:true, children: [
            {url:"upload.jsp",text:"上传考勤记录",tabid:100401},
            {url:'bill', text:"考勤异常单据",tabid:100402}
        ]},
        { text: '系统管理', isexpand:false, children: [
            {url:'billtype', text:"考勤异常单据类型",tabid:100501}
        ]}
    ];
