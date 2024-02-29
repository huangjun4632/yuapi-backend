package com.yupi.huangjunapigateway;

import com.huangjun.huangjunclientsdk.utils.SignUtils;
import com.yupi.yuapicommon.model.entity.InterfaceInfo;
import com.yupi.yuapicommon.model.entity.User;
import com.yupi.yuapicommon.service.InnterInterfaceInfoService;
import com.yupi.yuapicommon.service.InnterUserInterfaceInfoService;
import com.yupi.yuapicommon.service.InnterUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.http.server.reactive.SslInfo;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private InnterUserService innterUserService;

    @DubboReference
    private InnterUserInterfaceInfoService innterUserInterfaceInfoService;

    @DubboReference
    private InnterInterfaceInfoService innterInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8124";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1、请求日志
        ServerHttpRequest exchangeRequest = exchange.getRequest();
        String id = exchangeRequest.getId();
        //todo path 应该从数据库拿
        String path = INTERFACE_HOST +  exchangeRequest.getPath().value();
        HttpMethod method = exchangeRequest.getMethod();
        MultiValueMap<String, String> queryParams = exchangeRequest.getQueryParams();
        MultiValueMap<String, HttpCookie> cookies = exchangeRequest.getCookies();
        String hostString = exchangeRequest.getLocalAddress().getHostString();
        InetSocketAddress remoteAddress = exchangeRequest.getRemoteAddress();
        SslInfo sslInfo = exchangeRequest.getSslInfo();

        ServerHttpResponse response = exchange.getResponse();
        log.info("请求唯一标识是：" + id);
        log.info("请求路径是：" + path);
        log.info("请求参数是：" + queryParams);
        log.info("请求cookies是：" + cookies);
        log.info("请求localAddress地址是：" + hostString);
        log.info("请求remoteAddress地址是：" + remoteAddress);
        log.info("请求sslInfo是：" + sslInfo);
        log.info("请求方法是：" + method);
        log.info("custom global filter");

        //黑白名单
        if (!IP_WHITE_LIST.contains(hostString)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        //鉴权
        HttpHeaders headers = exchangeRequest.getHeaders();

        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String body = headers.getFirst("body");
        String sign = headers.getFirst("sign");
        // 实际从数据库查 accessKey
        User invokeUser = null;
        try {
            invokeUser =  innterUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error " ,e);
        }
        if (invokeUser ==null ){
            return handleNoAuth(response);
        }
        //5分钟有效
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        Long FIVE_MINUTES = 5 * 60l;
        if ((currentTimeMillis - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000l) {
            return handleNoAuth(response);
        }
        //实际从数据库查secretKey
        String secretKey = invokeUser.getSecretKey();
        String severSign = SignUtils.Sign(body, secretKey);
        if (severSign == null || !sign.equals(severSign)) {
            return handleNoAuth(response);
        }

        //从数据库查接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo =  innterInterfaceInfoService.getInterfaceInfo(path, method.toString());

        }catch (Exception e){
            log.error("getInterfaceInfo error ",e);
        }
        if (interfaceInfo == null){
            return handleNoAuth(response);
        }
        //请求转发，调用微服务
       // Mono<Void> filter = chain.filter(exchange);
        //todo 是否有调用次数
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = interfaceInfo.getUserId();
        long leftNum = 0l;
        try {
            leftNum =  innterUserInterfaceInfoService.isLeftNum(interfaceInfoId, userId);
        } catch (Exception e) {
            log.error("isLeftNum error ==============",e);
        }
        System.out.println("剩余调用次数=====" + leftNum);
        if (leftNum<=0){
            throw new RuntimeException("剩余调用次数不足");
        }


        //日志
        return handleResponse(exchange,chain,interfaceInfo.getId(), invokeUser.getId());
        //log.info("响应： " + response.getStatusCode());

/*        if (response.getStatusCode() == HttpStatus.OK) {


            //调用失败，返回一个规范的错误码
        } else {
            return handleInvokeError(response);
        }
        return filter;*/
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
//  https://blog.csdn.net/qq_19636353/article/details/126759522
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //调用玩转发的方法执行后再执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值写结果
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //todo 调用成功 成功次数加一

                                try {
                                    innterUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                   log.error("invokeCount error ",e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                sb2.append("<--- {} {} \n");
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                log.info("data是：" +data);
                                log.info(sb2.toString(), rspArgs.toArray());//log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            //失败 返回一个错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }
}




