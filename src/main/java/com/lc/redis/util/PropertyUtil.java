package com.lc.redis.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public final class PropertyUtil {
    private Properties properties;

    private PropertyUtil() {
        properties = new Properties();
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("common.properties");
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class PropertyUtilInner {
        private static final PropertyUtil propertiesUtil = new PropertyUtil();

        private PropertyUtilInner() {
        }
    }

    public static PropertyUtil instance() {
        return PropertyUtilInner.propertiesUtil;
    }

    public String getProperty(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        return Optional.ofNullable(properties.getProperty(key)).orElse("");
    }
}
