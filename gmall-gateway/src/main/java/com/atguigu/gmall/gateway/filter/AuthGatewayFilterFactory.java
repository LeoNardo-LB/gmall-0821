package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.gateway.autoconfig.AuthProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {   // 泛型为内部类

    @Autowired
    AuthProperties authProperties;

    /**
     * 使用父类构造器, 将内部类字节码传入
     */
    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    /**
     * 重写 shortcutFieldOrder
     * 指定顺序, 此处为内部类接收过滤器参数的属性名
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("list");
    }

    /**
     * 重写 shortcutType 方法
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    /**
     * 过滤器的主业务逻辑
     */
    @Override
    public GatewayFilter apply(PathConfig config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();

                // 1.判断请求路径是否在请求名单中, 不在则直接放行
                String path = request.getURI().getPath();
                List<String> pathList = config.getList();
                boolean isInlist = pathList.stream().anyMatch(preparedPath -> {
                    return path.startsWith(preparedPath);
                });
                if (!isInlist) {
                    chain.filter(exchange);
                }

                // 2.取出同步或异步的请求token: 同步位于cookie, 异步位于head
                String token = request.getHeaders().getFirst("token");  // 异步 header
                if (StringUtils.isBlank(token)) {
                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();   // 同步 cookie
                    if (!CollectionUtils.isEmpty(cookies) && cookies.containsKey(authProperties.getCookieName())) {
                        token = cookies.getFirst(authProperties.getCookieName()).getValue();
                    }
                }

                // 3.判断token是否为空, token为空则直接拦截,返回登陆页面
                if (StringUtils.isBlank(token)) {
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }

                try {
                    // 4.解析jwt信息
                    Map<String, Object> jwtLoad = JwtUtils.getInfoFromToken(token, authProperties.getPublicKey());

                    // 5.判断ip是否一样
                    String remoteIp = IpUtil.getIpAddressAtGateway(request);
                    String tokenIp = jwtLoad.get("ip").toString();
                    if (!remoteIp.equals(tokenIp)) {
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                        return response.setComplete();
                    }

                    // 6.将登录信息传递给后续服务,使之不需要再次解析jwt
                    // mutate()方法: 对请求的某些属性进行更改
                    request.mutate().header("userId", jwtLoad.get("userId").toString());
                    exchange.mutate().request(request).build();
                    System.out.println(request.getHeaders().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }

                // 7.放行
                return chain.filter(exchange);
            }
        };
    }

    /**
     * 定义内部类,属性即为参数的名称
     */
    public static class PathConfig {

        private List<String> list;

        public PathConfig() {
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public PathConfig(List<String> list) {
            this.list = list;
        }

    }

}
