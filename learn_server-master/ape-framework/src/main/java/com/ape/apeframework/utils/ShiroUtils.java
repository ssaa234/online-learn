package com.ape.apeframework.utils;

import com.ape.apesystem.domain.ApeUser;
import org.apache.shiro.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShiroUtils {
    public static ApeUser getUserInfo(){
        return (ApeUser) SecurityUtils.getSubject().getPrincipal();
    }

}
