package cn.web1992.es.manager;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class SearchManager {


    @Resource
    private RestHighLevelClient client;

    public void search() throws IOException {
        SearchRequest request = buildSearchRequest();
        System.out.println(request.toString());
        System.out.println(JSON.toJSON(request));
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        System.out.println(searchResponse);
    }

    protected SearchRequest buildSearchRequest() {
        SearchRequest searchRequest = new SearchRequest();
        //设置搜索索引（本处设置为索引别名） 设置类型type
        searchRequest.indices("orders").types("_doc");
        //设置查询条件
        SearchSourceBuilder sourceBuilder = this.buildSearchSourceBuilder2();
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    /**
     * @return
     * @link {https://www.elastic.co/guide/en/elasticsearch/painless/8.5/painless-sort-context.html}
     */
    private SearchSourceBuilder buildSearchSourceBuilder2() {

        Map<String, Object> params = new HashMap<>();
        params.put("subPrice", 5);
        String expression = "doc['amount'].value + params.subPrice";
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, expression, params);

        ScriptSortBuilder scriptSortBuilder = new ScriptSortBuilder(script, ScriptSortBuilder.ScriptSortType.NUMBER);
        //设置查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        sourceBuilder.query(boolQueryBuilder);
        //设置排序
        sourceBuilder.sort(scriptSortBuilder);
        //设置聚合
        //设置分页
        //设置超时
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        return sourceBuilder;
    }

    private SearchSourceBuilder buildSearchSourceBuilder() {
        //设置查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // Build the script
        Map params = new HashMap();
        params.put("field", "my_binary");
        params.put("query_value", "iiQ1QDEABAA=");
        params.put("space_type", "hammingbit");
        Script script = new Script(ScriptType.INLINE, "", "", params);

        ScriptQueryBuilder scriptQueryBuilder = QueryBuilders.scriptQuery(script);

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(scriptQueryBuilder);
        functionScoreQueryBuilder.boostMode(CombineFunction.REPLACE);

        sourceBuilder.query(functionScoreQueryBuilder);


        //设置排序
        //设置聚合
        //设置分页
        //设置超时
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        return sourceBuilder;
    }


}
