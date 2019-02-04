$(function(){

    // init date tables
    var dataTable = $("#data_list").dataTable({
        "deferRender": true,
        "processing" : true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/jobuser/pageList",
            type:"post",
            data : function ( d ) {
                var obj = {};
                obj.permission = $('#permission').val();
                obj.username = $('#username').val();
                obj.start = d.start;
                obj.length = d.length;
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// X轴滚动条，取消自适应
        "columns": [
            { "data": 'username', 'width': '40%'},
            {
                "data": 'permission',
                'width': '30%',
                "visible" : true,
                "render": function ( data, type, row ) {
                    var tmp = '';
                    if (data == 0) {
                        tmp = '普通用户';
                    } else if (data == 1) {
                        tmp = '管理员';
                    }
                    return tmp;
                }
            },
            {
                "data": '操作',
                'width': '30%' ,
                "render": function ( data, type, row ) {
                    return function(){
                        rowData[row.username] = row;

                        var permissionData = '';
                        if (row.permission != 1) {
                            permissionData = '<button class="btn btn-warning btn-xs permissionData" type="button">分配项目权限</button>  ';
                        }

                        // html
                        var html = '<p username="'+ row.username +'" >'+
                            '<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
                            permissionData +
                            '<button class="btn btn-danger btn-xs delete" type="button">删除</button>  '+
                            '</p>';

                        return html;
                    };
                }
            }
        ],
        "language" : {
            "sProcessing" : "处理中...",
            "sLengthMenu" : "每页 _MENU_ 条记录",
            "sZeroRecords" : "没有匹配结果",
            "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
            "sInfoEmpty" : "无记录",
            "sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix" : "",
            "sSearch" : "搜索:",
            "sUrl" : "",
            "sEmptyTable" : "表中数据为空",
            "sLoadingRecords" : "载入中...",
            "sInfoThousands" : ",",
            "oPaginate" : {
                "sFirst" : "首页",
                "sPrevious" : "上页",
                "sNext" : "下页",
                "sLast" : "末页"
            },
            "oAria" : {
                "sSortAscending" : ": 以升序排列此列",
                "sSortDescending" : ": 以降序排列此列"
            }
        }
    });

    $("#searchBtn").click(function(){
        dataTable.fnDraw();
    });

    var rowData = {};

    // 删除
    $("#data_list").on('click', '.delete',function() {

        var username = $(this).parent('p').attr("username");

        layer.confirm( "确定要删除用户：" + username , {
            icon: 3,
            title: '系统提示' ,
            btn: [ '确定', '取消' ]
        }, function(index){
            layer.close(index);

            $.post(
                base_url + "/jobuser/delete",
                {
                    "username" : username
                },
                function(data, status) {
                    if (data.code == 200) {
                        layer.open({
                            icon: '1',
                            content: '删除成功' ,
                            end: function(layero, index){
                                dataTable.fnDraw();
                            }
                        });
                    } else {
                        layer.open({
                            icon: '2',
                            content: (data.msg||'删除失败')
                        });
                    }
                }
            );

        });

    });

    // jquery.validate 自定义校验
    jQuery.validator.addMethod("myValid01", function(value, element) {
        var length = value.length;
        var valid = /^[a-z][a-z0-9]*$/;
        return this.optional(element) || valid.test(value);
    }, "限制以小写字母开头，由小写字母、数字组成");

    // 新增
    $("#add").click(function(){
        $('#addModal').modal('show');
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
            username : {
                required : true ,
                rangelength:[5,50],
                myValid01: true
            },
            password : {
                //required : true ,
                //rangelength:[4,50]
            }
        },
        messages : {
            username : {
                required : '请输入用户名'  ,
                rangelength : "用户名长度限制为5~50"
            },
            password : {
                required : '请输入密码'  ,
                rangelength : "密码长度限制为4~50"
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {
            $.post(base_url + "/jobuser/add", $("#addModal .form").serialize(), function(data, status) {
                if (data.code == 200) {
                    layer.open({
                        icon: '1',
                        content: '新增成功' ,
                        end: function(layero, index){
                            dataTable.fnDraw();
                            $('#addModal').modal('hide');
                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg||'新增失败')
                    });
                }
            });
        }
    });
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset()
    });


    $("#updateModal .form input[name='passwordInput']").change(function () {
        var passwordInput = $("#updateModal .form input[name='passwordInput']").prop('checked');
        $("#updateModal .form input[name='password']").val( '' );
        if (passwordInput) {
            $("#updateModal .form input[name='password']").removeAttr("readonly");
        } else {
            $("#updateModal .form input[name='password']").attr("readonly","readonly");
        }
    });

    // 更新
    $("#data_list").on('click', '.update',function() {

        var username = $(this).parent('p').attr("username");
        var row = rowData[username];

        $("#updateModal .form input[name='username']").val( row.username );
        $("#updateModal .form select[name='permission']").find("option[value='"+ row.permission +"']").prop("selected", 'selected');

        $("#updateModal .form input[name='passwordInput']").prop('checked', false);
        $("#updateModal .form input[name='password']").val( '' );
        $("#updateModal .form input[name='password']").attr("readonly","readonly");

        $('#updateModal').modal('show');
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
            username : {
                required : true ,
                rangelength:[5,50],
                myValid01: true
            },
            password : {
                //required : true ,
                //rangelength:[4,50]
            }
        },
        messages : {
            username : {
                required : '请输入用户名'  ,
                rangelength : "用户名长度限制为5~50"
            },
            password : {
                required : '请输入密码'  ,
                rangelength : "密码长度限制为4~50"
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {
            $.post(base_url + "/jobuser/update", $("#updateModal .form").serialize(), function(data, status) {
                if (data.code == 200) {
                    layer.open({
                        icon: '1',
                        content: '更新成功' ,
                        end: function(layero, index){
                            dataTable.fnDraw();
                            $('#updateModal').modal('hide');
                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg||'更新失败')
                    });
                }
            });
        }
    });
    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset()
    });


    // 分配项目权限
    $("#data_list").on('click', '.permissionData',function() {
        var username = $(this).parent('p').attr("username");
        var row = rowData[username];

        // fill username
        $("#permissionDataModal .form input[name='username']").val( row.username );

        // fill permission
        var permissionDataChoose;
        if (row.permissionData) {
            permissionDataChoose = $(row.permissionData.split(","));
        }
        $("#permissionDataModal .form input[name='permissionData']").each(function () {
            if ( $.inArray($(this).val(), permissionDataChoose) > -1 ) {
                $(this).prop("checked",true);
            } else {
                $(this).prop("checked",false);
            }
        });

        $('#permissionDataModal').modal('show');
    });
    $('#permissionDataModal .ok').click(function () {
        $.post(base_url + "/jobuser/updatePermissionData", $("#permissionDataModal .form").serialize(), function(data, status) {
            if (data.code == 200) {
                layer.open({
                    icon: '1',
                    content: '操作成功' ,
                    end: function(layero, index){
                        dataTable.fnDraw();
                        $('#permissionDataModal').modal('hide');
                    }
                });
            } else {
                layer.open({
                    icon: '2',
                    content: (data.msg||'操作失败')
                });
            }
        });
    });
    $("#permissionDataModal").on('hide.bs.modal', function () {
        $("#permissionDataModal .form")[0].reset()
    });

    // permission data
    var permissionDataTable = $("#permissionData").dataTable({
        "searching": true,
        "ordering": false,
        "paging": false,
        //"scrollX": true,	// X轴滚动条，取消自适应
        "language" : {
            "sProcessing" : "处理中...",
            "sLengthMenu" : "每页 _MENU_ 条记录",
            "sZeroRecords" : "没有匹配结果",
            "sInfo" : "",   // 第 _PAGE_ 页 ( 总共 _PAGES_ 页 )
            "sInfoEmpty" : "无记录",
            "sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix" : "",
            "sSearch" : "搜索:",
            "sEmptyTable" : "表中数据为空",
            "sInfoThousands" : ","
        }
    });


});