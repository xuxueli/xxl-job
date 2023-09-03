var multiSelector = (function () {
    var select;
    function init(divId, name, data) {
        layui.use(['xmSelect'], function () {
            var xmSelect = layui.xmSelect;
            select = xmSelect.render({
                el: divId,
                name: name,
                tips: '请选择',
                empty: '呀, 没有数据呢',
                filterable: true,    //是否开启搜索
                searchTips: '请在此搜索',
                paging: true,    //是否开启自定义分页
                pageSize: 20,    //分页条数
                radio: false, //是否开启单选模式,默认false
                repeat: false,    //是否开启重复性模式,默认false
                clickClose: false,    //是否点击选项后自动关闭下拉框,默认false
                max: 0,  //设置多选选中上限  0-不限制
                theme: {color: '#1e9fff', maxColor: '#f00', hover: '#f2f2f2'},
                // layVerify:'required',    //表单验证, 同layui的lay-verify
                data: data,
                // on: function (data) {
                // //arr:  当前多选已选中的数据
                // var arr = data.arr;
                // //change, 此次选择变化的数据,数组
                // var change = data.change;
                // //isAdd, 此次操作是新增还是删除  true/false
                // var isAdd = data.isAdd;
                // }
            });
        });
    }

    function update(data, oldValue) {
        if (!_.isEmpty(select)) {
            select.update({data: data, initValue: oldValue});
        }
    }

    function getValue() {
        let values = [];
        if (!_.isEmpty(select)) {
            var dep = select.getValue();
            if (!_.isEmpty(dep)) {
                for (let d of dep) {
                    values.push(d.value);
                }
            }
        }
        return values;
    }

    function destroy() {
        select = null;
    }

    return {
        init: init,
        update: update,
        getValue: getValue,
        destroy: destroy
    }

})();


