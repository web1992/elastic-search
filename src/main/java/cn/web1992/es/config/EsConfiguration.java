package cn.web1992.es.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "es.config")
public class EsConfiguration {

    /**
     * 集群地址，多个用,隔开 例：127.0.0.1:9200,127.0.0.2:9200
     */
    private String address = "127.0.0.1:9200,127.0.0.2:9200";

    /**
     * 使用的协议
     */
    private String schema = "http";

    /**
     * 连接超时时间 三次握手完成时间
     */
    private int connectTimeOut = 1000;

    /**
     * 连接超时时间 ,数据传输过程中数据包之间间隔的最大时间
     */
    private int socketTimeOut = 30000;
    /**
     * 获取连接的超时时间 使用连接池来管理连接,从连接池获取连接的超时时间
     */
    private int connectionRequestTimeOut = 500;

    /**
     * 最大连接数 连接池中最大连接数
     */
    private int maxConnectNum = 100;
    /**
     * 最大路由连接数 分配给同一个route最大的并发连接数,route为运行环境机器到目标机器的一条线路,举例来说,我们使用HttpClient的实现来分别请求 www.baidu.com 的资源和 www.bing.com 的资源那么他就会产生两个route
     */
    private int maxConnectPerRoute = 100;

    @Bean
    @PostConstruct
    public RestHighLevelClient client() {
        List<HttpHost> hostList = this.buildHosts(address);
        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[0]));

        // 异步httpclient连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeOut);
            requestConfigBuilder.setSocketTimeout(socketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
            return requestConfigBuilder;
        });
        // 异步httpclient连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }


    private List<HttpHost> buildHosts(String address) {
        List<HttpHost> hostList = new ArrayList<>();
        String[] addresses = address.split(",");
        for (String from : addresses) {
            int idx = from.lastIndexOf(":");
            String host = idx != -1 ? from.substring(0, idx) : from;
            String port = idx != -1 ? from.substring(idx + 1) : "";
            hostList.add(new HttpHost(host, Integer.valueOf(port), schema));
        }
        return hostList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectionRequestTimeOut() {
        return connectionRequestTimeOut;
    }

    public void setConnectionRequestTimeOut(int connectionRequestTimeOut) {
        this.connectionRequestTimeOut = connectionRequestTimeOut;
    }

    public int getMaxConnectNum() {
        return maxConnectNum;
    }

    public void setMaxConnectNum(int maxConnectNum) {
        this.maxConnectNum = maxConnectNum;
    }

    public int getMaxConnectPerRoute() {
        return maxConnectPerRoute;
    }

    public void setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
    }
}

