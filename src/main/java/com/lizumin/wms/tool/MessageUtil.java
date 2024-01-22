package com.lizumin.wms.tool;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {
    private final static Logger logger = LoggerFactory.getLogger(MessageUtil.class);
    private static MessageSource messageSource;

    public static Locale DEFAULT_MESSAGE_LOCALE = Locale.CHINA;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    /**
     * 指定Locale获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByLocale(String code, Locale locale) {
        String result;
        try {
            result = messageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            logger.trace("Cannot found i18 message, return directly");
            return code;
        }
        return result;
    }

    /**
     * 根据LocaleContextHolder上下文的Locale获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByContext(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageByLocale(code, locale);
    }

    /**
     * 根据默认Locale获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByDefault(String code) {
        return getMessageByLocale(code, DEFAULT_MESSAGE_LOCALE);
    }

    /**
     * 根据Request Accept-Language头获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByRequest(String code, HttpServletRequest request) {
        String localeHeader = request.getHeader("Accept-Language");
        try {
            Locale locale = new Locale.Builder().setLanguageTag(localeHeader.replace('_', '-')).build();
            return getMessageByLocale(code, locale);
        } catch (Exception e) {
            logger.trace("Accept-Language format is illegal");
            return getMessageByDefault(code);
        }
    }
}
