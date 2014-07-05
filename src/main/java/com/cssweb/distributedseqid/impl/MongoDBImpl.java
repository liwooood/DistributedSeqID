package com.cssweb.distributedseqid.impl;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by chenhf on 2014/7/2.
 */
public class MongoDBImpl {
    /*
    由于mongodb针对单个文档的操作提供了原子性，所以可以用于id的生成
    再加上复本集特性，比redis更有优势
     */

    public void getWithdraw()
    {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(Arrays.asList(new ServerAddress("192.168.1.201", 30000),
                    new ServerAddress("192.168.1.202", 30000),
                    new ServerAddress("192.168.1.203", 30000)));

            DB db = mongoClient.getDB( "test" );

            //boolean auth = db.authenticate(myUserName, myPassword);

            Set<String> colls = db.getCollectionNames();

            for (String s : colls) {
                System.out.println(s);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
}
