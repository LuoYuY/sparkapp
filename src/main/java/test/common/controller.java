package test.common;


import ch.ethz.ssh2.Connection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
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
import java.util.regex.Pattern;
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

        Connection c = SshLinux.login("hadoopslave3", "hadoop", "lyy19971221");
        result = SshLinux.execute(c);
        return ServerResponse.createBySuccess("submit success", result);
    }

    @ResponseBody
    @PostMapping(value = "/getMap")
    public ServerResponse getMap(HttpServletResponse response) throws UnsupportedEncodingException {

        System.out.println("Connect to database successfully!");
        System.out.println("MongoDatabase inof is : " + mDatabase.getName());

        //获取球迷数量多的队伍
        MongoCollection collection = mDatabase.getCollection("hometeam");
        MongoCursor<Document> cursor = collection.find().sort(descending("number")).limit(10).iterator();
        JSONArray teamArray = new JSONArray();
        try {
            while (cursor.hasNext()) {
                JSONObject jsonObject = JSONObject.parseObject(cursor.next().toJson());
                teamArray.add(jsonObject);
            }
        } finally {
            cursor.close();
        }

        //获取性别比
        MongoCollection genderCollection = mDatabase.getCollection("gender");
        MongoCursor<Document> genderCr = genderCollection.find().iterator();
        JSONArray genderArray = new JSONArray();
        try {
            while (genderCr.hasNext()) {
                JSONObject jsonObject = JSONObject.parseObject(genderCr.next().toJson());
                genderArray.add(jsonObject);
            }
        } finally {
            genderCr.close();
        }

        //获取用户地区分布
        JSONArray regionArray = new JSONArray();
        MongoCollection regionCollection = mDatabase.getCollection("region");
        String[] regionList = {"北京", "天津", "上海", "重庆", "河北", "河南", "云南", "辽宁", "黑龙江", "湖南",
                "安徽", "山东", "新疆", "江苏","浙江", "江西", "湖北", "广西", "甘肃", "山西", "内蒙古", "陕西", "吉林", "福建", "贵州", "广东", "青海", "西藏", "四川", "宁夏", "海南", "台湾", "香港", "澳门", "南海诸岛"
        };

        for(int i=0;i<regionList.length;i++) {
            JSONObject obj = new JSONObject();
            Pattern pattern = Pattern.compile("^.*"+regionList[i]+".*$",Pattern.CASE_INSENSITIVE);
            BasicDBObject query = new BasicDBObject();
            query.put("region",pattern);
            MongoCursor cur = regionCollection.find(query).iterator();
            Integer count =0 ;
            try {
                while (cur.hasNext()) {
                    cur.next();
                    count++;
                }
                obj.put("name", regionList[i]);
                obj.put("value", count.toString());
                regionArray.add(obj);
            }finally {
                cur.close();
            }
        }

        //获取词云
        JSONArray wordArray = new JSONArray();
        MongoCollection wordCollection = mDatabase.getCollection("word");
        MongoCursor<Document> wordCursor = wordCollection.find().sort(descending("number")).limit(50).iterator();
        try {
            while (wordCursor.hasNext()) {
                JSONObject jsonObject = JSONObject.parseObject(wordCursor.next().toJson());
                wordArray.add(jsonObject);
            }
        } finally {
            wordCursor.close();
        }

        JSONObject re = new JSONObject();
        re.put("teamArray",teamArray);
        re.put("regionArray",regionArray);
        re.put("genderArray",genderArray);
        re.put("wordArray",wordArray);
        return ServerResponse.createBySuccess("fetch success", re);
    }

}
