package cn.itcast.es;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRestApi {
    private RestClient restClient;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Before
    public void init() {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("192.168.88.132", 9200),
                new HttpHost("192.168.88.132", 9201),
                new HttpHost("192.168.88.132", 9202)
        );
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                System.out.println("出错 -> " + node);
            }
        });
        this.restClient = restClientBuilder.build();
    }

    @After
    public void close() throws IOException {
        //跟随应用程序的关闭而关闭
        this.restClient.close();
    }

    @Test
    public void testInfo() throws IOException {
        Request request = new Request("GET", "/_cluster/state");
        request.addParameter("pretty", "true");
        Response response = this.restClient.performRequest(request);
        System.out.println("请求完成 -> " + response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testSave2() throws IOException {
        Request request = new Request("POST", "/haoke/house/_bulk");
        StringBuilder sb = new StringBuilder();
        String createStr = "{\"index\":{\"_index\":\"haoke\",\"_type\":\"house\"}}";
        List<String> lines = FileUtils.readLines(new File("C:\\code\\itcast-haoke\\data.json"), "UTF-8");
        int count = 0;
        for (String line : lines) {
            sb.append(createStr + "\n");
            sb.append(line + "\n");
            if (count >= 200) {
                request.setJsonEntity(sb.toString());
                Response response = this.restClient.performRequest(request);
                System.out.println("存储200条完成 -> " + response.getStatusLine());
                count = 0;
                sb = new StringBuilder();
            }
            count++;
        }
        if (!sb.toString().isEmpty()) {
            request.setJsonEntity(sb.toString());
            Response response = this.restClient.performRequest(request);
        }
    }
    @Test
    public void testSave() throws IOException {
        Request request = new Request("POST", "/haoke/house");
        request.addParameter("pretty", "true");
        Map<String, Object> data = new HashMap<>();
        data.put("id", 2001);
        data.put("title", "南京西路一室一厅");
        data.put("price", 3500);
        String json = MAPPER.writeValueAsString(data);
        request.setJsonEntity(json);
        Response response = this.restClient.performRequest(request);
        System.out.println("请求完成 -> " + response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
    @Test
    public void testQueryData() throws IOException {
        Request request = new Request("GET", "/haoke/house/taxoEnoBefBfEcptrzqU");
        request.addParameter("pretty","true");
        Response response = this.restClient.performRequest(request);
        System.out.println("请求完成 -> " + response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
    @Test
    public void testSearchData() throws IOException {
        Request request = new Request("post", "/haoke/house/_search");
        request.addParameter("pretty","true");
        String searchJson = "{\n" +
                "    \"query\":{\n" +
                "        \"match\":{\n" +
                "            \"title\":\"上海\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        request.setJsonEntity(searchJson);
        Response response = this.restClient.performRequest(request);
        System.out.println("请求完成 -> " + response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
