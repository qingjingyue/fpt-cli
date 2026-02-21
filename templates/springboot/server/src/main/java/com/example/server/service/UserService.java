package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.dto.AccountLoginDTO;
import com.example.domain.dto.PhoneLoginDTO;
import com.example.domain.po.User;
import com.example.domain.vo.UserInfoVO;

public interface UserService extends IService<User> {
    void register(AccountLoginDTO userLoginDTO);

    UserInfoVO loginByAccount(AccountLoginDTO userLoginDTO);

    UserInfoVO loginByPhone(PhoneLoginDTO phoneLoginDTO);

    void sendPhoneCode(String phone);
}
