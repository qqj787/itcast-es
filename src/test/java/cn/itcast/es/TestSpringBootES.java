package cn.itcast.es;

import cn.itcast.es.pojo.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSpringBootES {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void save() {
        User user = new User();
        user.setId(1001L);
        user.setName("张三");
        user.setAge(20);
        user.setHobby("足球，篮球，听音乐");
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(user).build();
        String index = this.elasticsearchTemplate.index(indexQuery);
        System.out.println(index);
    }

    @Test
    public void testBulk() {
        List list = new ArrayList();
        for (int i = 0; i < 5000; i++) {
            User user = new User();
            user.setId(1001L + i);
            user.setAge(i % 50 + 10);
            user.setName("张三" + i);
            user.setHobby("篮球，足球，听音乐" + i);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(user).build();
            list.add(indexQuery);
        }
        long start = System.currentTimeMillis();
        this.elasticsearchTemplate.bulkIndex(list);
        System.out.println("本次批量操作一共用时：" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testUpdate() {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source("age", "30");
        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withId("1008")
                .withClass(User.class)
                .withIndexRequest(indexRequest)
                .build();
        UpdateResponse update = this.elasticsearchTemplate.update(updateQuery);
        System.out.println(update.status());
    }

    @Test
    public void testDelete() {
        this.elasticsearchTemplate.delete(User.class, "1008");
    }

    @Test
    public void testSearch() {
        PageRequest pageRequest = PageRequest.of(1,10);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name","张三"))
                .withPageable(pageRequest)
                .build();
        AggregatedPage<User> users = this.elasticsearchTemplate.queryForPage(searchQuery, User.class);
        System.out.println("总页数" + users.getTotalPages());
        for (User user : users.getContent()) {
            System.out.println(user);
        }

    }
}
