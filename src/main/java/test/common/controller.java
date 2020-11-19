package test.common;


import ch.ethz.ssh2.Connection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * Created by lyy on 2020/9/8 下午8:40
 */

@RestController
@RequestMapping("/getList")
public class controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Resource
//    private MongoTemplate mongoTemplate;
    MongoClient mongoClient = new MongoClient("172.19.241.132", 27017);
    //连接数据库
    MongoDatabase mDatabase = mongoClient.getDatabase("streaming");

    @ResponseBody
    @PostMapping(value = "/stream")
    public ServerResponse stream(HttpServletResponse response) throws UnsupportedEncodingException {
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @PostMapping(value = "/startStreaming")
    public ServerResponse startStreaming(HttpServletResponse response) throws UnsupportedEncodingException {
        String result = " ";
//        //文件存放路径
//        String path = "/home/hadoop/hadoop/tempShell.sh";
//        //执行的脚本，一个字符串就是一行。
//        //启动HDFS
////        String[] strs1 = { "cd /home/hadoop/hadoop/spark-2.4.3-bin-hadoop2.7","./sbin/start-dfs.sh"};
//        String[] strs = { "ssh hadoop@hadoopslave1"
//                ,"cd /home/hadoop/hadoop/spark-2.4.3-bin-hadoop2.7"
//                ,"./bin/spark-submit --deploy-mode cluster --class org.apache.spark.examples.SparkPi --name submit --master spark://hadoopmaster:7077 examples/jars/spark-examples_2.11-2.4.3.jar 100"
//                ,"exit"
//        };
//        try {
//            SubmitUtil.createShell(path, strs);
//            result = SubmitUtil.runShell(path);
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Connection c = SshLinux.login("hadoopslave3","hadoop","lyy19971221");
        result=SshLinux.execute(c);
        return ServerResponse.createBySuccess("submit success",result);
    }

    @ResponseBody
    @PostMapping(value = "/getMap")
    public ServerResponse getMap(HttpServletResponse response) throws UnsupportedEncodingException {

        System.out.println("Connect to database successfully!");
        System.out.println("MongoDatabase inof is : " + mDatabase.getName());
        MongoCollection collection = mDatabase.getCollection("hometeam");
//
//        Document d = new Document("name","lyy");
//        Document document = new Document("title", "MongoDB Insert Demo")
//                .append("description","database")
//                .append("likes", 30)
////                .append("by", d)
//                .append("url", "http://c.biancheng.net/mongodb/");
//        collection.insertOne(document);
//        172.19.241.132
//        List<Document> list = collection.find().sort(descending("number")).into(new ArrayList<Document>());

        MongoCursor<Document> cursor = collection.find().sort(descending("number")).limit(10).iterator();
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
