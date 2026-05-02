package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.dto.AccountLoginDTO;
import com.example.domain.dto.EmailLoginDTO;
import com.example.domain.dto.PhoneLoginDTO;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVO;

public interface UserService extends IService<User> {


    void sendVerifyCode(String type, String value);

    UserInfoVO loginByPhone(PhoneLoginDTO phoneLoginDTO);

    UserInfoVO loginByEmail(EmailLoginDTO emailLoginDTO);

    UserInfoVO loginByAccount(AccountLoginDTO accountLoginDTO);

    void registerByAccount(AccountLoginDTO accountLoginDTO);
}
