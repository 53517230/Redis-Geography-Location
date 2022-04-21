package com.geo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dangyicheng
 * @date 2022-04-20 15:44:10 星期三
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGeoPoint {
    private String key;

    private String member;

    private Double longitude;

    private Double latitude;
}
