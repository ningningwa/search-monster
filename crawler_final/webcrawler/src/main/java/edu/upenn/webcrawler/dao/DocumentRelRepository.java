package edu.upenn.webcrawler.dao;

import edu.upenn.webcrawler.entity.CoId;
import edu.upenn.webcrawler.entity.DocumentRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRelRepository extends JpaRepository<DocumentRel, CoId> {
}
