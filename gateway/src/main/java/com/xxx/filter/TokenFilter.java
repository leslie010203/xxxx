//package com.ziguang.gateway.config.filters;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ziguang.gateway.feign.IAppFeign;
//import com.ziguang.gateway.feign.IAuthFeign;
//import com.ziguang.gateway.feign.IDopFeign;
//import com.ziguang.gateway.model.ApiVO;
//import com.ziguang.gateway.model.TokenResponse;
//import com.ziguang.gateway.utils.AppApiUtil;
//import com.ziguang.gateway.utils.RequestUtil;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.net.InetSocketAddress;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.regex.Pattern;
//
///**
// * @ClassName TokenFilter
// * @Desc TODO   请求认证过滤器
// * @Date 2019/6/29 17:49
// * @Version 1.0
// */
//
//@Component
//public class TokenFilter implements GlobalFilter, Ordered {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//
//    @Autowired
//    private IAuthFeign iAuthFeign;
//
//    @Autowired
//    private IAppFeign iAppFeign;
//
//    @Autowired
//    private AppApiUtil appApiUtil;
//
//    @Autowired
//    private IDopFeign iDopFeign;
//
//    //忽略的地址(保留)
//    private String[] skipAuthUrls;
//
//    //签名忽略地址
//    @Value("#{'${path.sign.ignore}'.split(',')}")
//    private List<String> signIgnore;
//
//
//    //有效时间
//    @Value("${request.expire}")
//    private int requestExpire;
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // 请求对象
//        ServerHttpRequest request = exchange.getRequest();
//        // 响应对象
//        ServerHttpResponse response = exchange.getResponse();
//
//
//        String url = exchange.getRequest().getURI().getPath();
//
//        String method = exchange.getRequest().getMethod().name();
//
//        //跳过不需要验证的路径
//        if (null != skipAuthUrls && Arrays.asList(skipAuthUrls).contains(url)) {
//            return chain.filter(exchange);
//            // 只有综合路由才添加这个全局过滤器（routesId：route_all）
//            // 如果请求路径中不存在 routeAll 字符串
//        } else if (request.getURI().toString().indexOf("/unicloud-engine/execute/") == -1) {
//            System.out.println("filter -> return");
//            // 直接跳出
//            return chain.filter(exchange);
//        }
//
//        //ip获取
//        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
//        String clientIp = Objects.requireNonNull(remoteAddress).getAddress().getHostAddress();
//        logger.info("调用的ip地址：{}", clientIp);
//        //1.调用dop服务
//        //appKey的校验
//        if (!StringUtils.isEmpty(exchange.getRequest().getHeaders().getFirst("appkey"))) {
//            logger.info("身份认证 start ...");
//            // 1.获取appkey
//            String appkey = exchange.getRequest().getHeaders().getFirst("appKey");
//            if (StringUtils.isNotBlank(appkey)) {
//                String uri = exchange.getRequest().getURI().toString();
//                // 调用接口，验证身份
//                String sublitUri = uri.substring(0, uri.indexOf("?"));
//                String resp = iDopFeign.verify(sublitUri, appkey, clientIp);
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(resp);
//
//                    if (null != jsonObject && StringUtils.isNotBlank(jsonObject.getString("status")) && "200".equals(jsonObject.getString(
//                            "status"))) {
//                        // 返回200，则校验成功
//                        logger.info("success path: {}", uri);
//                    } else {
//                        logger.info("permission denied");
//                        if (StringUtils.isNotBlank(resp)) {
//                            return returnAuthFail(exchange, resp);
//                        } else {
//                            JSONObject result = new JSONObject();
//                            jsonObject.put("status", 401);
//                            jsonObject.put("message", "permission denied");
//                            return returnAuthFail(exchange, result.toString());
//                        }
//                    }
//                } catch (Exception e) {
//                    return returnAuthFail(exchange, "API响应超时或API错误！");
//                }
//
//            } else {
//                logger.info("appkey not found");
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("status", 201);
//                jsonObject.put("message", "appkey not found");
//                return returnAuthFail(exchange, jsonObject.toString());
//            }
//            logger.info("身份认证 end ...");
//            //token的校验
//        } else if (!StringUtils.isEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))) {
//            try {
//                String token = exchange.getRequest().getHeaders().getFirst("Authorization").substring(6);
//                String dopClientId = exchange.getRequest().getHeaders().getFirst("clientId");
//                //检验token，获取信息
//                TokenResponse tokenResponse = iAuthFeign.checkToken(token);
//                //-1.判空
//                if (tokenResponse == null) {
//                    return returnAuthFail(exchange, "您的token信息不存在!");
//                }
//                //0.首先进行clinetId的对比
//                if (!dopClientId.equals(tokenResponse.getClient_id())) {
//                    return returnAuthFail(exchange, "请更新你的token!");
//                }
//                //1.时间戳
//                String reqTimestamp = exchange.getRequest().getHeaders().getFirst("timestamp");
//                if (reqTimestamp == null) {
//                    return returnAuthFail(exchange, "缺少请求消息头：timestamp");
//                }
//                boolean b = RequestUtil.checkTimestamp(reqTimestamp, requestExpire);
//                if (!b) {
//                    return returnAuthFail(exchange, "请求超时");
//                }
//                //2.校验开发者账号与组关系(保留)
//                //3.API访问权限
//                List<ApiVO> apiVOS = iAppFeign.listApis(tokenResponse.getClient_id());
//                ApiVO apiVO = null;
//                if (!CollectionUtils.isEmpty(apiVOS)) {
//                    Optional<ApiVO> any = apiVOS.stream().filter(api -> {
//                        String uri = api.getUrl().replaceAll("\\{\\*\\}", "\\\\S+");
//                        String regEx = "^" + uri + "$";
//                        return Pattern.compile(regEx).matcher(url).find() && method.equalsIgnoreCase(api.getMethod());
//                    }).findAny();
//                    if (any.isPresent()) {
//                        apiVO = any.get();
//                    }
//                }
//                if (apiVO == null) {
//                    return returnAuthFail(exchange, "您无操作权限！");
//                }
//                try {
//                    //4.校验有效期或者有效次数/ip地址/订单状态
//                    String sublitUri = exchange.getRequest().getURI().toString().substring(0,exchange.getRequest().getURI().toString().indexOf("?"));
//                    String checkOutResult = iDopFeign.checkApiApplyTerm(sublitUri, dopClientId, clientIp);
//                    JSONObject jsonObject = JSONObject.parseObject(checkOutResult);
//                    if (null != jsonObject && StringUtils.isNotBlank(jsonObject.getString("status")) && "200".equals(jsonObject.getString(
//                            "status"))) {
//                        // 返回200，则校验成功
//                        logger.info("success path: {}", url);
//                    } else {
//                        logger.info("permission denied");
//                        if (StringUtils.isNotBlank(checkOutResult)) {
//                            return returnAuthFail(exchange, checkOutResult);
//                        } else {
//                            JSONObject result = new JSONObject();
//                            jsonObject.put("status", 401);
//                            jsonObject.put("message", "permission denied");
//                            return returnAuthFail(exchange, "无使用期限");
//                        }
//                    }
//                    //5.签名校验，是否需要签名(方法保留)
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return returnAuthFail(exchange, "API响应超时或API错误！");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return returnAuthFail(exchange, "token无效，请检查token");
//            }
//        } else {
//            // 响应消息内容对象
//            JSONObject message = new JSONObject();
//            // 响应状态
//            message.put("code", -1);
//            // 响应内容
//            message.put("msg", "缺少凭证");
//            // 转换响应消息内容对象为字节
//            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bits);
//            // 设置响应对象状态码 401
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            // 设置响应对象内容并且指定编码，否则在浏览器中会中文乱码
//            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
//            // 返回响应对象
//            return response.writeWith(Mono.just(buffer));
//        }
//
//        // 获取请求地址
//        String beforePath = request.getPath().pathWithinApplication().value();
//        // 获取响应状态码
//        HttpStatus beforeStatusCode = response.getStatusCode();
//        System.out.println("响应码：" + beforeStatusCode + "，请求路径：" + beforePath);
//        // 请求前
//        System.out.println("filter -> before");
//        // 如果不为空，就通过
//
//        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//            // 获取请求地址
//            String afterPath = request.getPath().pathWithinApplication().value();
//            // 获取响应状态码
//            HttpStatus afterStatusCode = response.getStatusCode();
//            if (afterStatusCode.value() == 429) {
//
//            }
//            response.setComplete();
//            System.out.println("响应码：" + afterStatusCode + "，请求路径：" + afterPath);
//            // 响应后
//            System.out.println("filter -> after");
//        }));
//    }
//
//
//    @Override
//    public int getOrder() {
//        return -500;
//    }
//
//    /**
//     * 返回校验失败
//     *
//     * @param exchange
//     * @return
//     */
//    private Mono<Void> returnAuthFail(ServerWebExchange exchange, String message) {
//        ServerHttpResponse serverHttpResponse = exchange.getResponse();
//        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
//        String resultData = "{\"status\":\"500\",\"msg\":" + message + "}";
//        byte[] bytes = resultData.getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//        return exchange.getResponse().writeWith(Flux.just(buffer));
//    }
//
//}
