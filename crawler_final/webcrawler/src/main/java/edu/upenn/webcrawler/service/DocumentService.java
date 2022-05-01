package edu.upenn.webcrawler.service;


import edu.upenn.webcrawler.entity.Document;

public interface DocumentService {
    public void saveOrUpdate(Document document);
    public void saveAll();
}
