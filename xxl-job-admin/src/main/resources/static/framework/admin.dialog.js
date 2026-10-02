/*!
* Admin Dialog for XXL-BOOT
* ================
*
* 1、dialog： iframe 子页面内触发的弹框（Bootstrap modal、layer 弹层），
*            在弹框展示期间将当前 iframe 临时撑满窗口，并把页面正文按原内容区位置偏移，
*            使菜单/顶栏等页面结构、页面正文均保留，再由遮罩整体压暗，弹框显示在最上层；
*            窗口缩放或内容区尺寸变化时同步重算偏移，弹框全部关闭后自动还原
*            iframe、正文偏移与遮罩。
*
* 说明：
*     - 仅 iframe 子页面生效，顶层窗口（主框架）无需处理；
*     - 采用撑满 iframe + 正文偏移的方式，不搬运弹框 DOM，因此页面内的图标选择树、
*       iCheck、CKEditor 等第三方组件及事件绑定均不受影响；
*     - 多层弹框以计数方式管理，全部关闭后才还原。
*
* @author       xuxueli
* @repository   https://github.com/xuxueli/xxl-boot
*/
(function ($) {

    // 仅 iframe 子页面生效（顶层窗口无需处理）
    if (window.self === window.top) {
        return;
    }

    // 所属 iframe 元素；非标签页 iframe 方式打开时跳过
    var iframeEl = window.frameElement;
    if (!iframeEl) {
        return;
    }

    // 顶层窗口 jQuery；跨域父窗口无法访问，直接跳过
    var top$;
    try {
        top$ = window.parent.jQuery;
    } catch (e) {
        return;
    }
    if (!top$) {
        return;
    }

    var $iframe = top$(iframeEl);
    // 当前 iframe 文档根节点与 body：展示弹框时透明化背景、偏移正文
    var $docEl = $(document.documentElement);
    var $body = $(document.body);

    // iframe 原始内容区容器：撑满后容器布局不变，窗口缩放时据此重算正文偏移
    var contentParent = iframeEl.parentElement;

    // body 原始内联 padding：展示弹框前记录，关闭后还原（避免覆盖页面自带内联样式）
    var originBodyPadding = null;

    // 当前 iframe 窗口 resize 事件命名空间：弹框展示期间监听，用后解绑
    var resizeEvent = 'resize.xxlDialog';

    // 内容区尺寸监听：侧边栏收展、响应式断点等导致内容区变化时，同步重算正文偏移
    var contentObserver = null;

    // 弹框令牌：同一弹框重复触发只计一次，避免重复展开/还原
    var dialogTokens = {};
    // 已展示弹框计数：0 表示无弹框
    var dialogCount = 0;
    // 令牌自增序号
    var tokenSeq = 0;

    /**
     * 重算正文偏移：以 iframe 原始内容区容器为基准（撑满后布局不变），
     * 把页面正文偏移回内容区；窗口缩放时重新调用，保证正文始终与内容区对齐缩放。
     */
    function syncBodyOffset() {
        var ref = contentParent || iframeEl;
        var rect = ref.getBoundingClientRect();
        var viewportWidth = window.innerWidth;
        var viewportHeight = window.innerHeight;
        $body.css({
            paddingLeft: rect.left + 'px',
            paddingTop: rect.top + 'px',
            paddingRight: Math.max(0, viewportWidth - rect.right) + 'px',
            paddingBottom: Math.max(0, viewportHeight - rect.bottom) + 'px',
        });
    }

    // 窗口缩放：弹框展示期间重算正文偏移，使正文随内容区同步缩放
    function onWindowResize() {
        if (dialogCount > 0) {
            syncBodyOffset();
        }
    }

    /**
     * 弹框展示：登记令牌；首个弹框展示时撑满 iframe、按原位置偏移正文，并添加全屏遮罩
     */
    function openDialog(token) {
        if (dialogTokens[token]) {
            return;
        }
        dialogTokens[token] = true;
        if (++dialogCount === 1) {
            $iframe.addClass('J_dialogExpand');
            originBodyPadding = {
                left: $body[0].style.paddingLeft,
                top: $body[0].style.paddingTop,
                right: $body[0].style.paddingRight,
                bottom: $body[0].style.paddingBottom,
            };
            syncBodyOffset();
            $docEl.addClass('J_dialogOpen');
            if ($body.children('.J_dialogChildMask').length === 0) {
                $body.append('<div class="J_dialogChildMask"></div>');
            }
            // 监听窗口缩放，重算正文偏移（iframe 撑满后随窗口缩放，其窗口即触发 resize）
            $(window).on(resizeEvent, onWindowResize);
            // 监听内容区尺寸变化（侧边栏收展、响应式断点等）
            if (typeof window.ResizeObserver === 'function' && contentParent) {
                contentObserver = new window.ResizeObserver(onWindowResize);
                contentObserver.observe(contentParent);
            }
        }
    }

    /**
     * 弹框关闭：注销令牌；最后一个弹框关闭时还原 iframe、正文偏移与遮罩
     */
    function closeDialog(token) {
        if (!dialogTokens[token]) {
            return;
        }
        delete dialogTokens[token];
        if (--dialogCount <= 0) {
            dialogCount = 0;
            $(window).off(resizeEvent, onWindowResize);
            if (contentObserver) {
                contentObserver.disconnect();
                contentObserver = null;
            }
            $iframe.removeClass('J_dialogExpand');
            if (originBodyPadding) {
                $body.css({
                    paddingLeft: originBodyPadding.left,
                    paddingTop: originBodyPadding.top,
                    paddingRight: originBodyPadding.right,
                    paddingBottom: originBodyPadding.bottom,
                });
                originBodyPadding = null;
            }
            $docEl.removeClass('J_dialogOpen');
            $body.children('.J_dialogChildMask').remove();
        }
    }

    // ---------------------- Bootstrap modal ----------------------

    // 展示：撑满 iframe（以弹框元素作为令牌，兼容事件重复触发）
    $(document).on('show.bs.modal', '.modal', function () {
        if (!this.__xxlDialogToken) {
            this.__xxlDialogToken = 'modal_' + (++tokenSeq);
        }
        openDialog(this.__xxlDialogToken);
    });

    // 关闭：还原 iframe（用 hidden：等淡出动画结束、弹框已隐藏后再收缩，避免淡出过程中弹框位移）
    $(document).on('hidden.bs.modal', '.modal', function () {
        if (this.__xxlDialogToken) {
            closeDialog(this.__xxlDialogToken);
        }
    });

    // ---------------------- layer ----------------------

    // 包装 layer.open：带遮罩的弹层（弹窗/页面/iframe）展示期间撑满 iframe，关闭后还原
    if (window.layer && typeof window.layer.open === 'function') {
        var originOpen = window.layer.open;

        window.layer.open = function (options) {
            var config = options || {};
            var type = config.type;
            // 仅带遮罩的常规弹层需覆盖整页；msg/loading/tips 等不处理
            var cover = config.shade !== false && config.shade !== 0 && type !== 3 && type !== 4;
            if (!cover) {
                return originOpen.apply(this, arguments);
            }

            var token = 'layer_' + (++tokenSeq);
            openDialog(token);

            // 关闭回调：先执行原回调，再还原 iframe
            var originEnd = config.end;
            config.end = function () {
                try {
                    if (typeof originEnd === 'function') {
                        originEnd.apply(this, arguments);
                    }
                } finally {
                    closeDialog(token);
                }
            };
            return originOpen.call(this, config);
        };

        // 包装 layer.msg / layer.load：弹框撑满期间转发到顶层窗口渲染。
        // 原因：提示按当前 iframe 视口居中定位，iframe 随后由撑满状态还原收缩时，
        //       提示会被 layer 的 resize 回调重新居中而产生「跳动」；转发到顶层窗口可彻底避免。
        var topLayer = window.top.layer;
        if (topLayer && typeof topLayer.msg === 'function') {
            var originMsg = window.layer.msg;
            window.layer.msg = function () {
                if (dialogCount > 0) {
                    return topLayer.msg.apply(topLayer, arguments);
                }
                return originMsg.apply(this, arguments);
            };

            if (typeof window.layer.load === 'function' && typeof topLayer.load === 'function') {
                var originLoad = window.layer.load;
                window.layer.load = function () {
                    if (dialogCount > 0) {
                        return topLayer.load.apply(topLayer, arguments);
                    }
                    return originLoad.apply(this, arguments);
                };
            }
        }
    }

})(jQuery);
