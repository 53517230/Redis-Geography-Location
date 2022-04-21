package com.geo.utils;

import com.geo.model.RedisGeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dangyicheng
 * @date 2022-04-20 14:46:41 星期三
 */
public class RedisGeoUtil {
  private static StringRedisTemplate redisTemplate = null;

  public static synchronized StringRedisTemplate getRedisTemplate() {
    if (redisTemplate == null) {
      redisTemplate = BeanUtil.getBean(StringRedisTemplate.class);
    }
    return redisTemplate;
  }


  /**
   * 添加经纬度信息 redis命令： geoadd key 116.405285 39.904989 "北京"
   *
   * @param key key
   * @param point 坐标
   * @param member 成员
   * @return java.lang.Long
   */
  public static Long geoAdd(String key, Point point, String member) {
    if (getRedisTemplate().hasKey(key)) {
      getRedisTemplate().opsForGeo().remove(key, member);
    }
      return getRedisTemplate().opsForGeo().add(key, point, member);
  }

  /**
   * 查找指定key的经纬度信息，可以指定多个member，批量返回 redis命令： geopos key 北京
   *
   * @param key key
   * @param members 成员
   * @return 坐标
   */
  public static List<Point> geoPos(String key, String... members) {
    return getRedisTemplate().opsForGeo().position(key, members);
  }

  /**
   * 返回两个位置的距离，可以指定单位，比如米m，千米km，英里mi，英尺ft redis命令： geodist key 北京 上海
   *
   * @param key key
   * @param member1 成员1
   * @param member2 成员2
   * @param metric 单位
   * @return 距离
   */
  public static Distance geoDist(String key, String member1, String member2, Metric metric) {
    return getRedisTemplate().opsForGeo().distance(key, member1, member2, metric);
  }

  /*************************************
   *     includeDistance 包含距离      *
   *     includeCoordinates包含经纬度 *
   *     sortAscending 正序排序        *
   *     limit 限定返回的记录数        *
   *************************************/

  /**
   * 根据给定的经纬度，返回半径不超过指定距离的元素 redis命令： georadius key 116.405285 39.904989 100 km WITHDIST WITHCOORD
   * ASC
   *
   * @param key key
   * @param circle 半径信息
   * @param count 限定返回的记录数
   * @return 满足条件的数据
   */
  public static GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(
      String key, Circle circle, long count) {
    RedisGeoCommands.GeoRadiusCommandArgs args =
        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance()
            .includeCoordinates()
            .sortAscending()
            .limit(count);
    return getRedisTemplate().opsForGeo().radius(key, circle, args);
  }

  /**
   * 根据指定经纬度返回指定范围内最近的元素
   *
   * @param key key
   * @param longitude 经度
   * @param latitude 纬度
   * @param distanceValue 距离值
   * @param metric 记录单位
   * @param count 限定返回的记录数
   * @return 满足条件的数据
   */
  public static List<RedisGeoPoint> geoNear(String key, Double longitude, Double latitude, Double distanceValue, Metric metric, Long count) {
    Circle circle = new Circle(new Point(longitude, latitude), new Distance(distanceValue, metric));
    GeoResults<RedisGeoCommands.GeoLocation<String>> geoLocationList = RedisGeoUtil.geoRadius(key, circle, count);
    List<RedisGeoPoint> resultList = new LinkedList<>();
    geoLocationList.forEach(item -> {
          RedisGeoCommands.GeoLocation<String> location = item.getContent();
          Point point = location.getPoint();
          RedisGeoPoint position = RedisGeoPoint.builder().key(key).member(location.getName()).longitude(point.getX()).latitude(point.getY()).build();
          resultList.add(position);
        });
    return resultList;
  }

  /**
   * 根据指定的地点查询半径在指定范围内的位置 redis命令： georadiusbymember key 北京 100 km WITHDIST WITHCOORD ASC COUNT 5
   *
   * @param key key
   * @param member 成员
   * @param distance 距离
   * @param count 限定返回的记录数
   * @return 满足条件的数据
   */
  public static GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember(
      String key, String member, Distance distance, long count) {

    RedisGeoCommands.GeoRadiusCommandArgs args =
        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance()
            .includeCoordinates()
            .sortAscending()
            .limit(count);
    return getRedisTemplate().opsForGeo().radius(key, member, distance, args);
  }

  /**
   * 获取一个或多个位置元素的 geohash 值 redis命令： geohash key 北京
   *
   * @param key key
   * @param members 成员
   * @return 结果
   */
  public static List<String> geoHash(String key, String... members) {
    return getRedisTemplate().opsForGeo().hash(key, members);
  }
}
