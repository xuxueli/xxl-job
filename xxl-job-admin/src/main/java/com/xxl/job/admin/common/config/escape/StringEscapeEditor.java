package com.xxl.job.admin.common.config.escape;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import java.beans.PropertyEditorSupport;
import java.util.Objects;

/**
 * 与spring mvc的@InitBinder结合 用于防止XSS攻击
 *
 * @author Rong.Jia
 * @date 2023/05/19
 */
public class StringEscapeEditor extends PropertyEditorSupport {

    /**
     * 转义HTML
     */
    private boolean escapeHTML;

    /**
     * 转义javascript
     */
    private boolean escapeJavaScript;

    /**
     * 是否将空字符串转换为null
     */
    private final boolean emptyAsNull;

    /**
     * 是否去掉前后空格
     */
    private final boolean trimmed;

    public StringEscapeEditor() {
        this(true, true, false, true);
    }

    public StringEscapeEditor(boolean escapeHTML, boolean escapeJavaScript) {
        this(true, true, escapeHTML, escapeJavaScript);
    }

    public StringEscapeEditor(boolean emptyAsNull, boolean trimmed, boolean escapeHTML, boolean escapeJavaScript) {
        super();
        this.emptyAsNull = emptyAsNull;
        this.trimmed = trimmed;
        this.escapeHTML = escapeHTML;
        this.escapeJavaScript = escapeJavaScript;
    }

    @Override
    public String getAsText() {
        Object value = getValue();

        if (Objects.nonNull(value)) {
            return value.toString();
        }
        return value != null ? value.toString() : null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        String value = text;

        if (value == null || emptyAsNull && text.isEmpty()) {
            //do nothing
        } else if (trimmed) {
            //去字符传参数前后空格
            value = value.trim();
        }

        if (escapeHTML) {
            //HTML转义（防止XSS攻击）
            //HtmlUtils.htmlEscape 默认的是ISO-8859-1编码格式，会将中文的某些符号进行转义。
            //如果不想让中文符号进行转义请使用UTF-8的编码格式。例如：HtmlUtils.htmlEscape(text, "UTF-8")
            value = HtmlUtils.htmlEscape(value, "UTF-8");
        }
        if (escapeJavaScript) {
            //javascript转义（防止XSS攻击）
            value = JavaScriptUtils.javaScriptEscape(value);
        }
        setValue(value);
    }

}
