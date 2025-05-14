package org.example.btreeweb;

import org.example.btreeweb.repository.BTree;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BTreeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BTreeWebApplication.class, args);
    }

    @Bean
    public BTree bTree(){
        return new BTree(2);
    }

}
