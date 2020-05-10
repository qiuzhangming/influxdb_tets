package com.zzdz.influxdb_tets;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.List;

@Accessors(chain = true)
@Data
@Measurement(name = "testDatas3")
public class MeasuringPointData {

    // Column中的name为measurement中的列名
    // 此外,需要注意InfluxDB中时间戳均是以UTC时保存,在保存以及提取过程中需要注意时区转换
    @Column(name = "time")
    private Long time;
    @Column(name = "alarm")
    private Boolean alarm;
    @Column(name = "data")
    private String data;
    @Column(name = "equipmentId")
    private String equipmentId;
    @Column(name = "label")
    private String label;
    @Column(name = "systemModelIds")
    private List<String> systemModelIds;
    @Column(name = "type")
    private Integer type;

    // 注解中添加tag = true,表示当前字段内容为tag内容
    @Column(name = "cpuid", tag = true)
    private String cpuid;
    @Column(name = "cputype", tag = true)
    private String cputype;
}
