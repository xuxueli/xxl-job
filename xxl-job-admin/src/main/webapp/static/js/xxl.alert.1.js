/**
 * Created by xuxueli on 16/8/12.
 *
 * dependency, jquery + bootstrap
 */

// 通用提示
var ComAlert = {
    html:function(){
        var html =
            '<div class="modal fade" id="ComAlert" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
                '<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                        '<div class="modal-header hidden"><h4 class="modal-title"><strong>提示:</strong></h4></div>' +
                        '<div class="modal-body"><div class="alert alert-success"></div></div>' +
                        '<div class="modal-footer">' +
                            '<div class="text-center" >' +
                                '<button type="button" class="btn btn-default ok" data-dismiss="modal" >确认</button>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';
        return html;
    },
    show:function(type, msg, callback){
        // dom init
        if ($('#ComAlert').length == 0){
            $('body').append(ComAlert.html());
        }

        // 弹框初始
        if (type == 1) {
            $('#ComAlert .alert').attr('class', 'alert alert-success');
        } else {
            $('#ComAlert .alert').attr('class', 'alert alert-warning');
        }
        $('#ComAlert .alert').html(msg);
        $('#ComAlert').modal('show');

        $('#ComAlert .ok').click(function(){
            $('#ComAlert').modal('hide');
            if(typeof callback == 'function') {
                callback();
            }
        });

        // $("#ComAlert").on('hide.bs.modal', function () {	});	// 监听关闭
    }
};

// 通用确认弹框
var ComConfirm = {
    html:function(){
        var html =
            '<div class="modal fade" id="ComConfirm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
                '<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                        '<div class="modal-body"><div class="alert alert-success"></div></div>' +
                            '<div class="modal-footer">' +
                            '<div class="text-center" >' +
                                '<button type="button" class="btn btn-primary ok" data-dismiss="modal" >确认</button>' +
                                '<button type="button" class="btn btn-default cancel" data-dismiss="modal" >取消</button>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';
        return html;
    },
    show:function(msg, callback){
        // dom init
        if ($('#ComConfirm').length == 0){
            $("body").append(ComConfirm.html());
        }

        // 弹框初始
        $('#ComConfirm .alert').attr('class', 'alert alert-warning');
        $('#ComConfirm .alert').html(msg);
        $('#ComConfirm').modal('show');

        $('#ComConfirm .ok').unbind("click");	// 解绑陈旧事件
        $('#ComConfirm .ok').click(function(){
            $('#ComConfirm').modal('hide');
            if(typeof callback == 'function') {
                callback();
                return;
            }
        });

        $('#ComConfirm .cancel').click(function(){
            $('#ComConfirm').modal('hide');
            return;
        });
    }
};
// 提示-科技主题
var ComAlertTec = {
    html:function(){
        var html =
            '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
                '<div class="modal-dialog">' +
                    '<div class="modal-content-tec">' +
                        '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
                        '<div class="modal-footer">' +
                            '<div class="text-center" >' +
                                '<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';
        return html;
    },
    show:function(msg, callback){
        // dom init
        if ($('#ComAlertTec').length == 0){
            $('body').append(ComAlertTec.html());
        }

        // 弹框初始
        $('#ComAlertTec .alert').html(msg);
        $('#ComAlertTec').modal('show');

        $('#ComAlertTec .ok').click(function(){
            $('#ComAlertTec').modal('hide');
            if(typeof callback == 'function') {
                callback();
            }
        });
    }
};