package edu.upenn.webcrawler.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.upenn.webcrawler.dao.DocumentRelRepository;
import edu.upenn.webcrawler.dao.DocumentRepository;
import edu.upenn.webcrawler.entity.Document;
import edu.upenn.webcrawler.entity.DocumentRel;
import edu.upenn.webcrawler.service.DocumentService;
import edu.upenn.webcrawler.utils.FileUtils;
import edu.upenn.webcrawler.utils.JedisUitls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("documentService")

public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentRelRepository documentRelRepository;
    @Value("${basePath}")
    private String basePath;
    @Override
    @Transactional
    public void saveOrUpdate(Document document) {
//        String url = document.getAccessUrl();
//        List<Document> list = documentRepository.findDocumentsByAccessUrl(url);
//        if(list == null){
//            documentRepository.save(document);
//            return;
//        }
//        if(list.size() == 0){
//            documentRepository.save(document);
//            return;
//        }
//        Document doc = list.get(0);
//        doc.setHtml(document.getHtml());
        documentRepository.save(document);
    }

    @Override
    public void saveAll() {
//        File baseFolder = new File(basePath);
//        if(!baseFolder.exists()){
//            throw new RuntimeException("can not find baseFolder!");
//        }
//        String[] folders = baseFolder.list();
//        for(String folder:folders){
//            folder = baseFolder + "\\" + folder;
//            File hourFolder = new File(folder);
//            String[] filesPath = hourFolder.list();
//            for(String filePath : filesPath){
//                filePath = folder + "\\" + filePath;
//                System.out.println("executing file:" + filePath);
//                String json = FileUtils.readFile(filePath);
//                Document document = JSONObject.parseObject(json,Document.class);
//                saveOrUpdate(document);
//                System.out.println("deleting file:" + filePath);
//                FileUtils.delete(filePath);
//            }
//            System.out.println("deleting folder:" + folder);
//            FileUtils.delete(folder);
//        }
        //load from redis
        Jedis jedis = JedisUitls.getJedis();
        Set<String> keySet = jedis.keys("*");
        int index = 0;
        for(String key : keySet){
            try{
                index ++;
                System.out.println(index + " documents has been saved!");
                String json = jedis.get(key);
                List<DocumentRel> drl = new ArrayList<>();
                if(key.startsWith("REL:")){
                    Set<String> set = JSON.parseObject(json, HashSet.class);
                    String relKey = key.substring(4);
                    System.out.println(key);
                    System.out.println(relKey);
                    for(String setKey:set){
                        DocumentRel dr = new DocumentRel();
                        dr.setId(relKey);
                        dr.setPid(setKey);
                        drl.add(dr);
                    }
                    try{
                        documentRelRepository.saveAll(drl);
                    }catch (Exception e){
                        e.printStackTrace();
                        for(DocumentRel dr : drl){
                            try{
                                documentRelRepository.save(dr);
                            }catch (Exception ee){
                                ee.printStackTrace();
                            }
                        }
                    }

                }else{
                    Document document = JSONObject.parseObject(json,Document.class);
                    saveOrUpdate(document);
                }
                jedis.del(key);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        jedis.close();
        System.out.println("Finished!");
    }
}
