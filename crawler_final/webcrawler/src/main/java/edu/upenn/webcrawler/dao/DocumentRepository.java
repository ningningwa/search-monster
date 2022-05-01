package edu.upenn.webcrawler.dao;

import edu.upenn.webcrawler.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepository extends JpaRepository<Document, String> , JpaSpecificationExecutor<Document> {
    public List<Document> findDocumentsByAccessUrl(String url);
}
