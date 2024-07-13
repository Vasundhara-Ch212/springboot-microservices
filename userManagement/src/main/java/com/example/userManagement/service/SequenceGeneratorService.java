package com.example.userManagement.service;

import com.example.userManagement.model.DbSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    //Method to update the sequenceNo,which is the id of the
    //last inserted record of every table.
    public int getSequneceNo(String sequenceName) {

        //get the current sequnceNo by passing the sequenceName,which uniquely identifies the table
        //for which we want to update the sequenceNo
        Query query = new Query(Criteria.where("sequenceName").is(sequenceName));

        //update the sequence no
        Update update = new Update().inc("sequenceNo", 1);

        //modify in document
        DbSequence counter = mongoOperations.findAndModify(query, update, options().returnNew(true).upsert(true),
                DbSequence.class);
        return !Objects.isNull(counter) ? counter.getSequenceNo() : 1;
    }

}
