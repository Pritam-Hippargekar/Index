package com.elastic.Index.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.json.JsonpMapper;
import jakarta.json.stream.JsonParser;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//
//Elasticsearch will analyze the Text before it’s stored into the Inverted Index while it won’t analyze Keyword type.
//        consider the data set below:
//        [
//        {
//        "id": 1,
//        "category": "dress"
//        },
//        {
//        "id": 2,
//        "category": "long dress"
//        },
//        {
//        "id": 3,
//        "category": "dress for women"
//        }
//        ]
//        And this corresponding query:
//
//        GET my-index/_search
//        {
//        "query": {
//        "term": {
//        "category": {
//        "value": "dress"
//        }
//        }
//        }
//        }

//        If the data type is text, results will include all categories containing the word “dress”
//        If the data type is keyword, results will include only the category called “dress”  (with id=1 from the example dataset)



@Service
public class IndexService {

    private final Logger log = LoggerFactory.getLogger(IndexService.class);
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void createIndex() throws IOException {

//        new Property.Builder().keyword(new KeywordProperty.Builder().normalizer("string_lowercase").build()).build();
        //map.put("id", new Property(new DateProperty.Builder().index(true).store(true).build()));
        Map<String, Property> map = new HashMap<>();//https://keep-memory.com/elasticsearch-java-http-logback
        // map.put("xxx", new Property(new KeywordProperty.Builder().index(false).store(false).norms(false).docValues(false).indexOptions(IndexOptions.Freqs).build()));
        map.put("xxx", new Property(new KeywordProperty.Builder().index(false).store(false).norms(false).indexOptions(IndexOptions.Freqs).build()));
        map.put("datetime", new Property(new DateProperty.Builder().index(false).build()));

        TypeMapping typeMapping = new TypeMapping.Builder().properties(map).build();

        IndexSettings indexSettings = new IndexSettings.Builder()
                .numberOfShards(String.valueOf(1))
                .numberOfReplicas(String.valueOf(0))
                .refreshInterval( new Time.Builder().time("60s").build() )
                .build();

        co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder cirb = new co.elastic.clients.elasticsearch.indices.CreateIndexRequest.Builder();
        co.elastic.clients.elasticsearch.indices.CreateIndexRequest cir =
                cirb.index("mytestindes")
                        .mappings(typeMapping)
                        .settings(indexSettings)
                        .build();

        CreateIndexResponse response = elasticsearchClient.indices().create(cir);
        log.info("response.acknowledged():" + response.acknowledged());
    }

    public void createIndexOne() throws IOException {
        CreateIndexResponse response = elasticsearchClient.indices().create(builder -> builder
                .settings(indexSettingsBuilder -> indexSettingsBuilder.numberOfReplicas("1").numberOfShards("2"))
                .mappings(typeMappingBuilder -> typeMappingBuilder
                        .properties("age", propertyBuilder -> propertyBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                        .properties("name", propertyBuilder -> propertyBuilder.keyword(keywordPropertyBuilder -> keywordPropertyBuilder))
                        .properties("poems", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("about", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("success", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                )
                .index("INDEX_NAME"));
        log.info("acknowledged={}", response.acknowledged());
    }

    public void modifyIndex() throws IOException {
        PutMappingResponse response = elasticsearchClient.indices().putMapping(typeMappingBuilder -> typeMappingBuilder
                .index("INDEX_NAME")
                .properties("age", propertyBuilder -> propertyBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                .properties("name", propertyBuilder -> propertyBuilder.keyword(keywordPropertyBuilder -> keywordPropertyBuilder))
                .properties("poems", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
        );
        log.info("acknowledged={}", response.acknowledged());
    }



    public void ravenMethod(){
        new Property.Builder().long_(
                new LongNumberProperty.Builder().index(true).build()
        ).build();
        new Property.Builder().nested(
                new NestedProperty.Builder().properties(Map.of()).build()
        ).build();
        new Property.Builder().object(
                new ObjectProperty.Builder().properties(Map.of()).build()
        ).build();
        new Property.Builder().boolean_(
                new BooleanProperty.Builder().build()
        ).build();
        new Property.Builder().geoPoint(
                new GeoPointProperty.Builder().build()
        ).build();
    }
//    String index = "testindex";
//    Map<String, Property> fields = Collections.singletonMap("keyword", Property.of(p -> p.keyword(k -> k.ignoreAbove(256))));
//    Property text = Property.of(p -> p.text(t -> t.fields(fields)));
//
//        client.indices().create(c -> c
//            .index(index)
//            .mappings(m -> m
//            .properties("id", text)
//            .properties("name", p -> p
//            .object(o -> o
//            .properties("first", text)
//            .properties("last", text)
//                    )
//                            )
//                            )
//                            );



    private void doYourIndex() throws IOException {
//        mapping.json
        //{
        //"properties":{
        // ..... your stuff goes here.
        //}
        //}
        String mappingPath = System.getProperty("user.dir")+"/mapping.json";
        JsonpMapper jsonpMapper = elasticsearchClient._transport().jsonpMapper();
        String mappingData = new String(Files.readAllBytes(Paths.get(mappingPath)));
        System.out.println("mapping data is: "+mappingData);
//        jsonpMapper.jsonProvider().createParser(new StringReader(Files.readString(Paths.get(mappingFile), Charsets.UTF_8)));
        JsonParser jsonParser = jsonpMapper.jsonProvider().createParser(new StringReader(mappingData));
//        elasticsearchClient.indices().create(indexRequest->indexRequest.index("index-name")
//                .mappings(TypeMapping._DESERIALIZER.deserialize(jsonParser,jsonpMapper)));
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index("")
                .mappings(TypeMapping._DESERIALIZER.deserialize(jsonParser,jsonpMapper))
                .build();
        CreateIndexResponse  createIndexResponse =  elasticsearchClient.indices().create(createIndexRequest);
    }



    private void createIndex(String index) throws IOException {

        CreateIndexRequest.Builder indexBuilder = new CreateIndexRequest.Builder();
        indexBuilder.index(index);
        TypeMapping.Builder tmBuilder = new TypeMapping.Builder();
        tmBuilder.properties("vec", new Property.Builder().denseVector(builder -> builder.index(true).dims(64).similarity("dot_product")
                .indexOptions(opBuilder -> opBuilder.type("hnsw").m(16).efConstruction(100))).build());
        tmBuilder.properties("id", new Property.Builder().long_(pb -> pb.index(false)).build());
        TypeMapping typeMapping = tmBuilder.build();
        indexBuilder.mappings(typeMapping);
        CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(indexBuilder.build());

        String resIndex = createIndexResponse.index();
        Boolean acknowledged = createIndexResponse.acknowledged();
        boolean b = createIndexResponse.shardsAcknowledged();
        log.info("index create response for {}, acknowledged= {}, shardsAcknowledged= {}", resIndex, acknowledged, b);
    }

}
//https://www.seaxiang.com/blog/ab3d3a88418643cdbfc2c8bfd6729190#menu_0
//https://www.mail-archive.com/notifications@james.apache.org/msg08499.html


//    public static void main(String[] args) throws Exception {
//        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("factor", 2);
//        Script script = new Script(ScriptType.INLINE, "painless", "doc['price'].value * params.factor", params);
//
//        SearchRequest searchRequest = new SearchRequest("my_index");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchSourceBuilder.sort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
//        searchSourceBuilder.scriptField("new_price", script);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        searchResponse.getHits().forEach(hit -> {
//            System.out.println(hit.getId() + ", price=" + hit.getSourceAsMap().get("price") + ", new_price=" + hit.getFields().get("new_price").getValue());
//        });
//
//        client.close();
//    }

//    public void createIndexTemplate() {
//https://realkoy.tistory.com/entry/elasticsearch-8x-Java-api-client%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-IndexTemplate-%EC%83%9D%EC%84%B1
//        ElasticsearchClient client = null;
//        try {
//            int numberOfShards = 3;
//            int replicas = 0;
//
//            Map<String, Tokenizer> tokMap = new HashMap<>();
//            Map<String, Analyzer> anlzMap = new HashMap<>();
//            Map<String, TokenFilter> filterMap = new HashMap<>();
//            Map<String, Normalizer> normMap = new HashMap<>();
//            Map<String, CharFilter> charMap = new HashMap<>();
//
//            // char filter
//            HtmlStripCharFilter htmlStripFilter = new HtmlStripCharFilter.Builder().build();
//            CharFilter chrFilter =  new CharFilter.Builder().definition(htmlStripFilter._toCharFilterDefinition()).build();
//            charMap.put("htmlfilter", chrFilter);
//
//            // remove punctuation chars
//            PatternReplaceCharFilter patternCharFilter = new PatternReplaceCharFilter.Builder().pattern("\\p{Punct}").replacement("").build();
//            CharFilter chrPatternFilter =  new CharFilter.Builder().definition(patternCharFilter._toCharFilterDefinition()).build();
//            charMap.put("patternfilter", chrPatternFilter);
//
//            List<String> charFilterList = new ArrayList<>();
//            charFilterList.add("htmlfilter");
//            charFilterList.add("patternfilter");
//
//            // Token filter
//            AsciiFoldingTokenFilter asciiFilter = new AsciiFoldingTokenFilter.Builder().preserveOriginal(false).build();
//            LowercaseTokenFilter lowerFilter = new LowercaseTokenFilter.Builder().build();
//            filterMap.put("asciifolding", new TokenFilter.Builder().definition(asciiFilter._toTokenFilterDefinition()).build());
//            filterMap.put("lowercase", new TokenFilter.Builder().definition(lowerFilter._toTokenFilterDefinition()).build());
//
//            List<String> filterList = new ArrayList<>();
//            filterList.add("lowercase");
//            filterList.add("asciifolding");
//
//            // bin/elasticsearch-plugin install analysis-nori
//            // discardPunctuation:true -
//            // decompoundMode: None -
//            NoriTokenizer noriTokenizer = new NoriTokenizer.Builder().decompoundMode(NoriDecompoundMode.Mixed).discardPunctuation(true).build();
//            Tokenizer tokenizer = new Tokenizer.Builder().definition(noriTokenizer._toTokenizerDefinition()).build();
//            tokMap.put("nori-tokenizer", tokenizer);
//
//            // char_filter ==> tokenizer ==> token filter
//            CustomAnalyzer noriAnalyzer = new CustomAnalyzer.Builder()
//                    .charFilter(charFilterList)
//                    .tokenizer("nori-tokenizer")
//                    .filter(filterList).build();
//
//            CustomAnalyzer noriAnalyzer = new CustomAnalyzer.Builder().filter(filterList).tokenizer("nori-tokenizer").build();
//            Analyzer analyzer = new Analyzer.Builder().custom(noriAnalyzer).build();
//            anlzMap.put("nori-analyzer", analyzer);
//
//            normMap.put("keyword_normalizer", new Normalizer.Builder()
//                    .custom(new CustomNormalizer.Builder().charFilter("patternfilter").filter(filterList).build())
//                    .build());
//
//            IndexSettings indexSettings = new IndexSettings.Builder()
//                    .numberOfReplicas(String.valueOf(replicas))
//                    .numberOfShards(String.valueOf(numberOfShards))
//                    .maxResultWindow((int)max_result_count)
//                    .refreshInterval(new Time.Builder().time("5s").build())
//                    .codec("best_compression")
//                    .analysis(a -> a.charFilter(charMap).normalizer(normMap).tokenizer(tokMap).filter(filterMap).analyzer(anlzMap))
//                    .build();
//
//            Map<String, Property> map = new HashMap<>();
//            map.put("key", new Property(new KeywordProperty.Builder().store(true).build()));
//            map.put("userid", new Property(new KeywordProperty.Builder().store(true).build()));
//            map.put("date", new Property(new DateProperty.Builder().format("yyyy-MM-dd HH:mm:ss").store(true).build()));
//            map.put("name", new Property(new KeywordProperty.Builder().normalizer("keyword_normalizer").store(true).build()));
//            map.put("email", new Property(new KeywordProperty.Builder().normalizer("keyword_normalizer").store(true).build()));
//            map.put("subject", new Property(new KeywordProperty.Builder().store(true).normalizer("keyword_normalizer").build()));
//            map.put("body", new Property(new TextProperty.Builder().analyzer("nori-analyzer").index(true).store(false).build()));
//
//            // Field Not Stored
//            SourceField source = new SourceField.Builder().excludes("body").build();
//            TypeMapping typeMapping = new TypeMapping.Builder().source(source).properties(map).build();
//
//            // ElasticSearchClient
//            client = ElasticClient.getInstacne();
//
//            // If exists same index template
//            ExistsIndexTemplateRequest existsIndexTemplateRequest = new ExistsIndexTemplateRequest.Builder().name("test_template").build();
//            boolean isExists = client.indices().existsIndexTemplate(existsIndexTemplateRequest).value();
//            if(!isExists) {
//                IndexTemplateMapping templateMapping = new IndexTemplateMapping.Builder()
//                        .settings(indexSettings)
//                        .mappings(typeMapping)
//                        .build();
//
//                PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest.Builder()
//                        .name("test_template")
//                        .indexPatterns("test-*")
//                        .template(templateMapping)
//                        .priority(1)
//                        .build();
//                PutIndexTemplateResponse templateResponse = client.indices().putIndexTemplate(putIndexTemplateRequest);
//                boolean acknowledged = templateResponse.acknowledged();
//                System.out.println("createIndexTemplate result:" + acknowledged);
//            } else {
//                System.out.println("createIndexTemplate Already Exists");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            ElasticClient.close();
//        }
//    }


