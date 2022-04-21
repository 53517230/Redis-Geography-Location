package com.geo.controller;

import com.geo.model.RedisGeoPoint;
import com.geo.utils.RedisGeoUtil;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dangyicheng
 * @date 2022-04-20 16:15:01 星期三
 */
@RestController
@RequestMapping("/geo")
public class GeoController {

    private final String GEO_KEY = "geo_key";

    private String buildRedisKey(String key, String cityId) {
        return key + ":" + cityId;
    }

    /**
     *
     * @param cityId
     * @param cityName
     * @param longitude
     * @param latitude
     * @return
     */
    @GetMapping("/geoAdd")
    public List<Point> geoAdd(String cityId, String cityName, Double longitude, Double latitude) {
        String redisKey = this.buildRedisKey(this.GEO_KEY, cityId);

        RedisGeoUtil.geoAdd(redisKey, new Point(longitude, latitude), cityName);
        return RedisGeoUtil.geoPos(redisKey, cityName);
    }


    @GetMapping("/geoNear")
    public List<RedisGeoPoint> geoNear(String cityId, Double longitude, Double latitude) {
        String redisKey = this.buildRedisKey(this.GEO_KEY, cityId);
        return RedisGeoUtil.geoNear(redisKey, longitude, latitude, 2000D, RedisGeoCommands.DistanceUnit.KILOMETERS, 5L);
    }

    @GetMapping("/geoDist")
    public Distance geoDist(String cityId, String member1, String member2){
       String redisKey = this.buildRedisKey(this.GEO_KEY,cityId);
       return RedisGeoUtil.geoDist(redisKey,member1,member2,RedisGeoCommands.DistanceUnit.KILOMETERS);
    }
}
