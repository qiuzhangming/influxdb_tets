package com.zzdz.influxdb_tets;

import com.alibaba.fastjson.JSON;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class InfluxdbTetsApplicationTests {

    @Autowired
    InfluxDB influxDB;

    @Test
    void contextLoads() {
    }

    @Test
    public void insert3353() {
        for (int i = 0; i < 1000000000; i++) {
            MeasuringPointData data = new MeasuringPointData();
            data.setAlarm(true)
                    .setData(UUID.randomUUID().toString())
                    .setEquipmentId(UUID.randomUUID().toString())
                    .setCpuid(9999+"")
                    .setCputype("1234");


            Point point = Point.measurementByPOJO(data.getClass())
                    .addFieldsFromPOJO(data)
                    .time(System.currentTimeMillis(), TimeUnit.MICROSECONDS)
                    .build();

            influxDB.setDatabase("testdb").write(point);
        }
    }
    @Test
    public void insert22() {

        for (int i = 0; i < 10000000; i++) {
            MeasuringPointData data = new MeasuringPointData();
            data.setAlarm(true)
                    .setData(UUID.randomUUID().toString())
                    .setEquipmentId(UUID.randomUUID().toString())
                    .setCpuid(i+"a");


            Point point = Point.measurementByPOJO(data.getClass())
                    .addFieldsFromPOJO(data)
                    .time(System.currentTimeMillis(), TimeUnit.MICROSECONDS)
                    .build();

            influxDB.setDatabase("testdb").write(point);
        }
    }
    @Test
    public void insert() {
        Point.Builder builder = Point.measurement("testDatas12");
        builder.time(System.currentTimeMillis(), TimeUnit.MICROSECONDS);
        builder.addField("label","测试标签");
        builder.addField("type",2);
        builder.addField("data","东经30度，北纬38度，天气晴");
        builder.addField("equipmentId", UUID.randomUUID().toString());
        builder.addField("alarm", false);

        StringBuilder sb = new StringBuilder(UUID.randomUUID().toString());
        for (int i = 0; i < 2; i++) {
            sb.append(UUID.randomUUID().toString()).append(",");
        }

        builder.addField("systemModelIds", sb.toString().substring(0, sb.toString().length() - 1));
//        builder.addField("systemModelIds", UUID.randomUUID().toString());

        builder.tag("cpuid","55577799");
        builder.tag("cputype","F");
        Point point = builder.build();
        influxDB.setDatabase("testdb").write(point);
    }

    @Test
    public void testQuery() {
        String command = "select * from testDatas";
        Query query = new Query(command,"testdb");

        // QueryResult queryResult = influxDB.query(query);
        QueryResult queryResult = influxDB.query(query, TimeUnit.MILLISECONDS);
        List<QueryResult.Result> results = queryResult.getResults();
        System.out.println(JSON.toJSON("results:" + results));
        if (results == null) {
            return;
        }
        // 多个sql用分号隔开，因本次查询只有一个sql，所以取第一个就行
        QueryResult.Result result = results.get(0);
        List<QueryResult.Series> seriesList = result.getSeries();
        System.out.println(JSON.toJSON("Series:" + seriesList));
        List<MeasuringPointData> tests = new LinkedList<>();

        for (QueryResult.Series series : seriesList) {
            if (series == null) {
                return;
            }

//            System.out.println("colums ==>> " + JSON.toJSON(series.getColumns()));
//            System.out.println("tags ==>> " + JSON.toJSON(series.getTags()));
//            System.out.println("name ==>> " + JSON.toJSON(series.getName()));
//            System.out.println("values ==>> " + JSON.toJSON(series.getValues()));
//            System.out.println("查询总数为： ==>> " + (series.getValues() == null ? 0 : series.getValues().size()));

            List<MeasuringPointData> dataVos = new LinkedList<>();
            series.getValues().forEach(testData -> {

//                Map<String,Object> map1 = new HashMap<>();
//                for (int i = 0; i < testData.size(); i++) {
//                    map1.put(series.getColumns().get(i), testData.get(i));
//                }
//                System.out.println(JSON.toJSONString(map1));
//                MeasuringPointData measuringPointData = JSON.parseObject(JSON.toJSONString(map1), MeasuringPointData.class);
//                System.out.println("====="+measuringPointData);

                MeasuringPointData dataVo = new MeasuringPointData();

                // 直接查询出来的是科学计数法，需要转换为Long类型的数据
                BigDecimal decimalTime = new BigDecimal(testData.get(0).toString());
                dataVo.setTime(decimalTime.longValue());
                dataVo.setAlarm((boolean) testData.get(1));
                dataVo.setData(testData.get(4).toString());
                dataVo.setEquipmentId(testData.get(5).toString());
                dataVo.setLabel(testData.get(6).toString());
                System.out.println("长度信息==>> " + testData.get(7).toString().length());
                // dataVo.setSystemModelIds(Arrays.asList(StringUtils.split(testData.get(7).toString(), "\\,")));
                dataVo.setSystemModelIds(Arrays.asList(testData.get(7).toString().split(",")));
                dataVo.setType(Double.valueOf(testData.get(8).toString()).intValue());

                System.out.println("数组长度==>> " +dataVo.getSystemModelIds().size());

                dataVos.add(dataVo);
            });

            System.out.println("最终结果为： " + JSON.toJSON(dataVos));
            tests = dataVos;
        }

//        System.out.println(JSON.toJSONString(tests));
    }

}
