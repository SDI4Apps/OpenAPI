/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.utils;

import eu.sdi4apps.openapi.types.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author runarbe
 */
public class HttpParam {

    public static Map<String, String> GetParameters(HttpServletRequest request, Map<String, Boolean> fields, Response r) {

        Map<String, String> m = new LinkedHashMap<String, String>();

        for (Map.Entry<String, Boolean> e : fields.entrySet()) {
            String key = e.getKey();
            String value = GetString(request, key);
            if (e.getValue() == true && value == null) {
                r.missingParam(key);
            }
            m.put(e.getKey(), value);
        }
        return m;
    }

    public static String GetString(HttpServletRequest request, String stringFieldName, String defaultValue) {
        String value = request.getParameter(stringFieldName);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else {
            return defaultValue;
        }
    }

    public static String GetString(HttpServletRequest request, String stringFieldName) {
        return GetString(request, stringFieldName, null);
    }

    public static Integer GetInt(HttpServletRequest request, String intFieldName, Integer defaultValue) {
        String value = request.getParameter(intFieldName);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        } else {
            return defaultValue;
        }
    }

    public static Integer GetInt(HttpServletRequest request, String intFieldName) {
        return GetInt(request, intFieldName, null);
    }

    public static Float GetFloat(HttpServletRequest request, String floatFieldName, Float defaultValue) {
        String value = request.getParameter(floatFieldName);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Float.parseFloat(value);
        } else {
            return defaultValue;
        }
    }

    public static Float GetFloat(HttpServletRequest request, String floatFieldName) {
        return GetFloat(request, floatFieldName, null);
    }

}
