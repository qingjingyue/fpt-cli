package com.example;

import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.Test;

public class DigestUtilTest {

    @Test
    public void testDigest() {
        String hash = DigestUtil.bcrypt("password");
        System.out.println("加密结果: " + hash);
        System.out.println("长度: " + hash.length());
        System.out.println("校验结果: " + DigestUtil.bcryptCheck("password", hash));
    }
}
