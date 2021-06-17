package cn.itcast.es.pojo;

import com.sun.javafx.beans.IDProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "haoke",type = "user",createIndex = false)
public class User {
    @Id
    private Long id;
    @Field(store = true)
    private String name;
    @Field
    private Integer age;
    @Field(store = true)
    private String hobby;
}
