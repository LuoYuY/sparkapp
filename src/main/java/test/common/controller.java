package test.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.Cursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.management.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by lyy on 2020/9/8 下午8:40
 */

@RestController
@RequestMapping("/getList")
public class controller<MongoTemplate> {
    //    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Resource
//    private MongoTemplate mongoTemplate;
    MongoClient mongoClient = new MongoClient("115.29.208.141", 27017);
    //连接数据库
    MongoDatabase mDatabase = mongoClient.getDatabase("testDB");

    @ResponseBody
    @PostMapping(value = "/stream")
    public ServerResponse stream(HttpServletResponse response) throws UnsupportedEncodingException {
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @PostMapping(value = "/getMap")
    public ServerResponse getMap(HttpServletResponse response) throws UnsupportedEncodingException {

        System.out.println("Connect to database successfully!");
        System.out.println("MongoDatabase inof is : " + mDatabase.getName());
        MongoCollection collection = mDatabase.getCollection("user");
//
//        Document d = new Document("name","lyy");
//        Document document = new Document("title", "MongoDB Insert Demo")
//                .append("description","database")
//                .append("likes", 30)
////                .append("by", d)
//                .append("url", "http://c.biancheng.net/mongodb/");
//        collection.insertOne(document);
        MongoCursor<Document> cursor = collection.find(eq("likes", 30)).iterator();
        JSONArray re = new JSONArray();
        try {
            while (cursor.hasNext()) {
                JSONObject jsonObject = JSONObject.parseObject(cursor.next().toJson());
                re.add(jsonObject);
            }
        } finally {
            cursor.close();
        }
        return ServerResponse.createBySuccess("fetch success", re);
    }

}
