package com.lzx.blog.service;

import com.lzx.blog.popj.VerCode;

/**
 * 缓存service接口
 * @author Administrator
 *
 */
public interface CacheService {

    /**
     * 保存邮箱的验证码和过期时间
     * @param verCode 需要存入的实体
     * @return
     */
    public VerCode SaveLocalEmail(VerCode verCode);

    /**
     * 取出邮箱的验证码和过期时间
     * @param email key值
     * @return
     */
    public VerCode GetLocalEmail(String email);


}