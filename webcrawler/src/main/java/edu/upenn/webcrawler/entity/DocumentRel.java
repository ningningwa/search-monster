package edu.upenn.webcrawler.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "T_DOC_REL")
@IdClass(value = CoId.class)
public class DocumentRel {
    @Id
    @Column(length = 32, name = "id")
    private String id;
    @Id
    @Column(length = 32, name = "cid")
    private String pid;
}
