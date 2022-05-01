package org.xk.crawler.bolt;

import com.alibaba.fastjson.JSON;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.xk.crawler.entity.Document;
import org.xk.crawler.global.GlobalConfig;
import org.xk.crawler.utils.*;
import redis.clients.jedis.Jedis;

import java.util.*;


public class DocumentFetcherBolt extends BaseRichBolt {
    public static final String REL_KEY = "REL:";
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        if (input != null) {
            System.out.println("Left Document:" + GlobalConfig.CRAWLER_QUEUE.size());
        }
        Jedis jedis = JedisUitls.getJedis();
        Document doc = (Document) input.getValueByField("document");
        System.out.println(JSON.toJSON(doc));
        String parentId = doc.getParentId();
        String url = doc.getAccessUrl();
        System.out.println("deal with url :" + url);
        //get html
        String html = HttpUtils.getHtml(url);
        if (html != null) {
            doc.setHtml(html);

            //do post
//            String id = HttpUtils.doPost("http://localhost:9527/document/save",JSON.toJSONString(doc));
            String id = IDUtils.getUUID();
            doc.setId(id);
            String key = url;
            String value = JSON.toJSONString(doc);
            if (!jedis.exists(key)) {
                jedis.set(key, value);
                try {
                    List<String> subUrlList = ParseUtils.getSubUrl(url);
                    int size = subUrlList.size();
                    for (String subUrl : subUrlList) {
                        //add to queue
                        if (!jedis.exists(subUrl)) {
                            if(subUrl.endsWith(".pdf") || subUrl.endsWith(".PDF") || subUrl.endsWith(".mp4") || subUrl.endsWith(".MP4")){
                                continue;
                            }
                            Document subDocument = new Document();
                            subDocument.setAccessUrl(subUrl);
                            subDocument.setAccessTimes(0);
                            subDocument.setParentId(id);
                            subDocument.setUpdateDate(DateUtils.nowFormat());
                            GlobalConfig.CRAWLER_QUEUE.add(subDocument);
                        }else{
                            //only save relationship
                            Document currentDocument = JSON.parseObject(jedis.get(subUrl),Document.class);
                            String rkey = REL_KEY + currentDocument.getId();
                            if(!jedis.exists(rkey)){
                                Set<String> set = new HashSet<>();
                                set.add(id);
                                jedis.set(rkey,JSON.toJSONString(set));
                            }else{
                                String json = jedis.get(rkey);
                                Set<String> set = JSON.parseObject(json,HashSet.class);
                                set.add(id);
                                jedis.set(rkey,JSON.toJSONString(set));
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
            //save relationship
            Document currentDocument = JSON.parseObject(jedis.get(url),Document.class);
            String rkey = REL_KEY + currentDocument.getId();
            if(!jedis.exists(rkey)){
                Set<String> set = new HashSet<>();
                set.add(parentId);
                jedis.set(rkey,JSON.toJSONString(set));
            }else{
                String json = jedis.get(rkey);
                Set<String> set = JSON.parseObject(json,HashSet.class);
                set.add(parentId);
                jedis.set(rkey,JSON.toJSONString(set));
            }
//            String docJson = JSON.toJSONString(doc);
            // save to file
//            String path = GlobalConfig.BASE_PATH+"\\"+DateUtils.getCurrentPath();
//            FileUtils.mkdirs(path);
//            String filePath = path + "\\"+id+".json";
//            FileUtils.writeToFile(docJson,filePath);



        }
        jedis.close();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}
