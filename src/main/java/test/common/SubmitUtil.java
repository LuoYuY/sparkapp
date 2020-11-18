package test.common;

import java.io.*;

/**
 * Created by lyy on 2020/11/18 下午11:06
 */

public class SubmitUtil {
    public static void createShell(String path, String[] strs) throws Exception {
        if (strs == null) {
            System.out.println("strs is null");
            return;
        }

        File sh = new File(path);
        if (sh.exists()) {
            sh.delete();
        }

        sh.createNewFile();
        sh.setExecutable(true);
        FileWriter fw = new FileWriter(sh);
        BufferedWriter bf = new BufferedWriter(fw);

        for (int i = 0; i < strs.length; i++) {
            bf.write(strs[i]);

            if (i < strs.length - 1) {
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
    }

    //执行shell
    public static String runShell(String shpath) throws Exception {

        if (shpath == null || shpath.equals("")) {
            return "shpath is empty";
        }
        Process ps = Runtime.getRuntime().exec(shpath);
        ps.waitFor();

        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();
        return result;
    }

    //测试效果
    public static void main(String[] args) {
        //文件存放路径
        String path = "/home/hadoop/hadoop/tempShell.sh";
        //执行的脚本，一个字符串就是一行。
        //启动HDFS
//        String[] strs1 = { "cd /home/hadoop/hadoop/spark-2.4.3-bin-hadoop2.7","./sbin/start-dfs.sh"};
        String[] strs = { "cd /home/hadoop/hadoop/spark-2.4.3-bin-hadoop2.7"
                ,"./bin/spark-submit --deploy-mode cluster --class org.apache.spark.examples.SparkPi --name submit --master spark://hadoopmaster:7077 examples/jars/spark-examples_2.11-2.4.3.jar 100"};
        try {
            SubmitUtil.createShell(path, strs);
            String result = SubmitUtil.runShell(path);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
