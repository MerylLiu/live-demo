layui.config({
    base: '../../js/module/'
}).extend({
    dialog: 'dialog'
});

layui.use(['form', 'jquery', 'laydate', 'layer', 'table', 'laypage', 'dialog', 'element'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        dialog = layui.dialog,
        table = layui.table;

    $.ajaxSetup({
        cache: false,
        contentType: 'application/json;charset=utf-8'
    });
    $.fn.serializeJson = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || null);
            } else {
                o[this.name] = this.value || null;
            }
        });
        return o;
    };

    //获取当前iframe的name值
    var iframeObj = $(window.frameElement).attr('name');
    //全选
    form.on('checkbox(allChoose)', function (data) {
        var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]');
        child.each(function (index, item) {
            item.checked = data.elem.checked;
        });
        form.render('checkbox');
    });
    //渲染表单
    form.render();

    //顶部添加
    $('.btn-add').click(function () {
        var url = $(this).attr('data-url');
        var id = $(this).attr('data-id');
        if (id) {
            window.location.href = url + "?id=" + id;
        } else {
            window.location.href = url;
        }
        return false;
    }).mouseenter(function () {
        dialog.tips('添加', this);
    });

    //顶部编辑
    $('.btn-edit').click(function () {
        var url = $(this).attr('data-url');
        var $table = $(this).attr('data-table-id');

        var checkStatus = table.checkStatus($table);
        var checkedData = checkStatus.data;
        if (checkedData.length > 0) {
            window.location.href = url + '?id=' + checkedData[0].id;
        } else {
            layer.alert('请选择你要编辑的项', {icon: 2});
        }

        return false;
    }).mouseenter(function () {
        dialog.tips('编辑', this);
    });

    //顶部批量删除
    $('.btn-del-batch').click(function () {
        var url = $(this).attr('data-url');
        var $table = $(this).attr('data-table-id');

        layer.confirm('您确定要删除选中项', {icon: 3, title: '提示'}, function (index) {
            var checkStatus = table.checkStatus($table);
            var checkedData = checkStatus.data;
            var data = {id: []};
            for (var i = 0; i < checkedData.length; i++) {
                data.id.push(checkedData[i].id)
            }

            $.post(url, JSON.stringify(data), function (res) {
                if (res && res.errCode == 0) {
                    layer.close(index);
                    table.reload($table, {
                        page: {curr: 1}
                    })
                } else {
                    layer.closeAll();
                    layer.alert(res.errMsg, {icon: 2})
                }
            });
        });
        return false;
    }).mouseenter(function () {
        dialog.tips('批量删除', this);
    });

    //查询条件
    form.on('submit(formSearch)', function (data) {
        var condition = $(data.form).serializeJson();

        var tableId = $(this).data('table-id');
        table.reload(tableId, {
            page: {
                curr: 1
            }
            , where: condition
        }, 'data');

        return false;
    });
});

/**
 * 控制iframe窗口的刷新操作
 */
var iframeObjName;

//父级弹出页面
function page(title, url, obj, w, h) {
    if (title == null || title == '') {
        title = false;
    }
    ;
    if (url == null || url == '') {
        url = "404.html";
    }
    ;
    if (w == null || w == '') {
        w = '700px';
    }
    ;
    if (h == null || h == '') {
        h = '350px';
    }
    ;
    iframeObjName = obj;
    //如果手机端，全屏显示
    if (window.innerWidth <= 768) {
        var index = layer.open({
            type: 2,
            title: title,
            area: [320, h],
            fixed: false,
            content: url
        });
        layer.full(index);
    } else {
        var index = layer.open({
            type: 2,
            title: title,
            area: [w, h],
            fixed: false,
            content: url
        });
    }
}

/**
 * 刷新子页,关闭弹窗
 */
function refresh() {
    //根据传递的name值，获取子iframe窗口，执行刷新
    if (window.frames[iframeObjName]) {
        window.frames[iframeObjName].location.reload();
    } else {
        window.location.reload();
    }

    layer.closeAll();
}


Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "H+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}