package cn.web1992.es;

import cn.web1992.es.manager.SearchManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class ElasticSearchApplicationTests {


    @Resource
    private SearchManager searchManager;

    @Test
    void contextLoads() throws IOException {

        searchManager.search();
    }


}
