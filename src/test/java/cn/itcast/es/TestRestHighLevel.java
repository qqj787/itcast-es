package cn.itcast.es;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestRestHighLevel {
    private RestHighLevelClient restHighLevelClient;
    @Before
    public void init() {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("192.168.88.132",9200),
                new HttpHost("192.168.88.132",9201),
                new HttpHost("192.168.88.132",9202)
                );
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }
    @After
    public void close() throws IOException {
        this.restHighLevelClient.close();
    }
    @Test
    public void testSave() throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("id",2002);
        map.put("title","南京东路，6室一厅");
        map.put("price",4000);
        IndexRequest indexRequest = new IndexRequest("haoke","house").source(map);
        IndexResponse response = this.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("id ->" + response.getId());
        System.out.println("version ->" + response.getVersion());
        System.out.println("result ->" + response.getResult());
    }
    @Test
    public void testCreateAsync() throws InterruptedException {
        Map<String,Object> map = new HashMap<>();
        map.put("id",2004);
        map.put("title","南京东路2，最新房源两室一厅");
        map.put("price",5330);
        IndexRequest indexRequest = new IndexRequest("haoke","house").source(map);
        this.restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println("id ->" + indexResponse.getId());
                System.out.println("index ->" + indexResponse.getIndex());
                System.out.println("type ->" + indexResponse.getType());
                System.out.println("version ->" + indexResponse.getVersion());
                System.out.println("result ->" + indexResponse.getResult());
                System.out.println("shareInfo ->" + indexResponse.getShardInfo());
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println(e);
            }
        });
        System.out.println("ok");
        Thread.sleep(20000);
    }
    @Test
    public void testQuery() throws IOException {
        GetRequest getRequest = new GetRequest("haoke","house","wayZEnoBefBfEcptADrJ");
        String[] includes = new String[]{"title","id"};
        String[] excludes = new String[]{};
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true,includes,excludes);
        getRequest.fetchSourceContext(fetchSourceContext);
        GetResponse response = this.restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("数据 -> " + response.getSource());
    }
    @Test
    public void  testExists() throws IOException {
        GetRequest request = new GetRequest("haoke","house","wayZEnoBefBfEcptADrJ");
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = this.restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println("exists-> " + exists);
    }
    @Test
    public void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("haoke","house","t6xxEnoBefBfEcptIjo2");
        DeleteResponse deleteResponse = this.restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }
    @Test
    public void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("haoke","house","w6ymEnoBefBfEcptETqi");
        Map<String, Object> map = new HashMap<>();
        map.put("title","北京夕儿胡同,三室一厅1");
        map.put("price",10000);
        updateRequest.doc(map);
        UpdateResponse update = this.restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("version -> " + update.getVersion());
    }
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("haoke");
        searchRequest.types("house");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("title","领包入住"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse search = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("查询到的记录总条数 -> " + search.getHits().totalHits);
        SearchHits hits = search.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
}
