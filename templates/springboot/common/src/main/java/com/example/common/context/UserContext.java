package com.example.common.context;


/**
 * 在该线程中记录用户id
 */
public class UserContext {

    // 线程局部变量
    private static final ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void setId(Long id) {
        TL.set(id);
    }

    public static Long getId() {
        return TL.get();
    }

    public static void removeId() {
        TL.remove();
    }

}
