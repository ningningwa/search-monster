package org.xk.crawler;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.xk.crawler.bolt.DocumentFetcherBolt;
import org.xk.crawler.entity.Document;
import org.xk.crawler.global.GlobalConfig;
import org.xk.crawler.spout.CrawlerQueueSpout;
import org.xk.crawler.utils.DateUtils;

public class CrawlerTopology {
    public static void main(String[] args){
        Document document = new Document();
        document.setAccessUrl("https://www.glassdoor.com/");
        document.setParentId("");
        document.setUpdateDate(DateUtils.nowFormat());
        document.setAccessTimes(0);
        GlobalConfig.CRAWLER_QUEUE.add(document);
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("crawlerQueueSpout",new CrawlerQueueSpout(),10);
        topologyBuilder.setBolt("documentFetcherBolt",new DocumentFetcherBolt(),20).shuffleGrouping("crawlerQueueSpout");
        StormTopology topology = topologyBuilder.createTopology();
        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();
        localCluster.submitTopology("crawlerTopology", config, topology);
    }
}
