package cn.itcast.es.wm;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

public class LianjiaPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        List<String> list = html.css(".content__list--item--title a").links().all();
        //房源详情链接
        page.addTargetRequests(list);
        String title = html.xpath("//div[@class='content clear w1150']/p/text()").toString();
        page.putField("title", title);
        String rent = html.xpath("//div[@class='content__aside--title']/span/text()").toString();
        page.putField("rent", rent);
        String type = html.xpath("//ul[@class='content__aside__list']/allText()").toString();
        page.putField("type", type);
        page.putField("info", html.xpath("//div[@class='content__article__info']/allText()").toString());
        page.putField("img", html.xpath("//div[@class='content__article__slide__item']/img").toString());
        if (page.getResultItems().get("title") == null) {
            page.setSkip(true);
            for (int i = 1; i <= 100; i++) {
                page.addTargetRequest("https://sh.lianjia.com/zufang/pg" + i);
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new LianjiaPageProcessor())
                .addPipeline(new MyPipeLine())
                .addUrl("https://sh.lianjia.com/zufang/").thread(1).run();
    }
}
