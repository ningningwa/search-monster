package edu.upenn.webcrawler.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name="T_DOCUMENT")
public class Document {

    @Id
//    @GeneratedValue(generator="system_uuid")
//    @GenericGenerator(name="system_uuid",strategy="uuid")
    @Column(name="id",length = 32)
    private String id;

    @Column(name="childId",length = 32)
    private String parentId;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String html;
    @Column(name="updateDate",length = 64)
    private String updateDate;
    @Column(name="accessUrl",length = 2048)
    private String accessUrl;
    @Column(name="accessTimes",length = 32)
    private Integer accessTimes;
}
