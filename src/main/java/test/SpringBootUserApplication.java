package test;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by lyy on 2020/9/8 下午7:49
 */


@SpringBootApplication
//@MapperScan(basePackages = {"cn.org.test.mapper"})
public class SpringBootUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootUserApplication.class,args);
    }
}
