<script type="text/javascript">
    $(function(){

        jQuery.i18n.properties({
            name : 'message', //资源文件名称
            path : '/static/i18n/', //资源文件路径
            mode : 'map', //用Map的方式使用资源文件中的值
            language : 'en',
            callback : function() {//加载成功后设置显示内容
                // console.log($.i18n.map.job_dashboard_name);
                I18n = $.i18n.map;
            }
        });

    });
</script>