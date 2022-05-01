package org.xk.crawler.spout;



import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.xk.crawler.entity.Document;
import org.xk.crawler.global.GlobalConfig;

import java.util.Map;


public class CrawlerQueueSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        //begin with start url
        Document document = GlobalConfig.CRAWLER_QUEUE.poll();

        if(document != null){
            this.collector.emit(new Values(document));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("document"));
    }
}
