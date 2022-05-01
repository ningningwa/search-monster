package edu.upenn.webcrawler.controller;

import edu.upenn.webcrawler.entity.Document;
import edu.upenn.webcrawler.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @PostMapping("/save")
    public String save(@RequestBody Document document){
        try{
            documentService.saveOrUpdate(document);
        }catch (Exception e){
            return null;
        }

        return document.getId();
    }

    @GetMapping("/saveAll")
    public String saveAll(){
        documentService.saveAll();
        return "SUCCESS";
    }
}
