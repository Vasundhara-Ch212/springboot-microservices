package com.example.userManagement.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//Model to store the sequenceNo,
//which is the id of the last inserted record of every table in db
@Getter
@Setter
@Document(collection = "db_sequence")
public class DbSequence {

    private String sequenceName;
    private int sequenceNo;

}
