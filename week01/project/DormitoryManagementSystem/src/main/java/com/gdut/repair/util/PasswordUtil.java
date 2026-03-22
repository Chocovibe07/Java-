package com.gdut.repair.util;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String encrypt(String password) {
        try {
            // 获取 MD5 摘要算法实例。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 对原始密码做哈希，得到 16 字节摘要。
            byte[] bytes = md.digest(password.getBytes());

            // 把字节数组转换为 32 位十六进制字符串，便于存库与比对。
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            // 统一转换成运行时异常，简化上层调用处理。
            throw new RuntimeException("加密失败");
        }
    }
}

