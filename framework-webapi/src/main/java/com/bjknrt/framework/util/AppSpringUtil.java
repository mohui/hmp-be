package com.bjknrt.framework.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AppSpringUtil extends SpringUtil {

    /**
     * 获取国际化文本
     *
     * @param key code
     * @return
     */
    public static String getMessage(String key) {
        return getApplicationContext().getMessage(key, null, Locale.getDefault());
    }

    /**
     * 获取国际化文本
     *
     * @param key  code
     * @param args 参数
     * @return
     */
    public static String getMessage(String key, Object... args) {
        return getApplicationContext().getMessage(key, args, Locale.getDefault());
    }
}
