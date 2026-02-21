package com.example;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;

public class TestExample {

    @Test
    public void test() {
        // 空密码
        String pw = "";
        // 密码加密
        String encryptedPw = BCrypt.hashpw(pw, BCrypt.gensalt(10));
        System.out.println(encryptedPw);
        // 密码校验
        boolean check = BCrypt.checkpw(pw, encryptedPw);
        System.out.println(check);
        assert check;
    }
}
