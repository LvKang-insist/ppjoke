package com.lvkang.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author 345 QQ:1831712732
 * @name ppjoke
 * @class name：com.lvkang.libnavannotation
 * @time 2020/3/3 21:02
 * @description
 */

@Target(ElementType.TYPE)
public @interface FragmentDestination {


    String pageUrl();

    /**
     * 是否需要登录后跳转
     *
     */
    boolean needLogin() default false;

    /**
     * 是否为首页
     *
     */
    boolean asStarter() default false;
}
