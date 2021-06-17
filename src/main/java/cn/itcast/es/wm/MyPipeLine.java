package cn.itcast.es.wm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyPipeLine implements Pipeline {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> data = new HashMap<>();
        data.put("url",resultItems.getRequest().getUrl());
        data.put("title",resultItems.get("title"));
        data.put("rent",resultItems.get("rent"));
        String[] types = resultItems.get("type").toString().split(" ");
        data.put("rentMethod",types[0].split("：")[1]);
        data.put("houseType",types[1].split("：")[1]);
        data.put("orientation",types[2]);
        String[] infos = StringUtils.split(resultItems.get("info"), ' ');
        for (String info : infos) {
            if (StringUtils.startsWith(info,"看房：")) {
                data.put("time",StringUtils.split(info,"：")[1]);
            }else if (StringUtils.startsWith(info,"楼层：")) {
                data.put("floor",StringUtils.split(info,"：")[1]);
            }
        }
        String imageUrl = StringUtils.split(resultItems.get("img"), '"')[3];
        String newName = StringUtils.substringBeforeLast(
                StringUtils.substringAfterLast(resultItems.getRequest().getUrl(), "/"), "."
        ) + ".jpg";

        try {
            this.downLoadFile(imageUrl,new File("C:\\code\\itcast-haoke\\images\\" + newName));
            data.put("image",newName);
            String json = MAPPER.writeValueAsString(data);
            FileUtils.write(new File("C:\\code\\itcast-haoke\\data.json"),json + "\n","UTF-8",true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * 下载文件
     * @param imageUrl 文件url
     * @param file   目标目录
     */
    private void downLoadFile(String imageUrl, File file) throws IOException {
        HttpGet httpGet = new HttpGet(imageUrl);
        CloseableHttpResponse response = HttpClientBuilder.create().build().execute(httpGet);
        try {
            FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(response.getEntity().getContent()));
        } finally {
            response.close();
        }

    }

}
