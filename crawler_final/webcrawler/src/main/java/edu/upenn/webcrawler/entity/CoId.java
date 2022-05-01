package edu.upenn.webcrawler.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoId implements Serializable {
    private String id;
    private String pid;
}
