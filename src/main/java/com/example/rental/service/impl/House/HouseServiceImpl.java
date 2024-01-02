package com.example.rental.service.impl.House;

import com.example.rental.dao.CityDao;
import com.example.rental.dao.CollectionDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.dao.SearchHistoryDao;
import com.example.rental.domain.House;
import com.example.rental.domain.SearchHistory;
import com.example.rental.service.HouseService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HouseServiceImpl implements HouseService {
    @Autowired
    private HouseDao houseDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private LandlordDao landlordDao;

    @Autowired
    private SearchHistoryDao searchHistoryDao;

    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 推荐房屋给指定用户，考虑用户的历史订单和收藏列表，基于用户偏好和行为生成推荐结果。
     *
     * @param consumerId 用户ID
     * @return 包含推荐房屋列表的Result对象
     */
    @Override
    public Result getHouseRand(String consumerId) {
        // 检查用户ID是否有效
        if (StringUtils.isNotEmpty(consumerId)) {
            // 初始化选择城市和类型的数量
            int numProvinceListToSelect = 4;
            int numTypeListToSelect = 4;

            // 获取用户历史订单中的房屋ID
            List<String> houseId = orderEndDao.getHouseIdByConsumerId(consumerId);
            // 如果历史订单数量少于1，增加选择省份的数量
            if (houseId.size() < 1) {
                numProvinceListToSelect = 8;
            }

            // 获取城市和类型列表
            List<Map<String, String>> cityAndType = getCityAndTypeList(houseId);
            List<Map<String, String>> typeList = getTypeList(cityAndType);
            List<String> provinceList = getProvinceList(cityAndType);

            // 获取用户收藏列表中的房屋ID
            List<String> houseList = collectionDao.getHouseIdByConsumerId(consumerId);
            List<Map<String, String>> cityAndType2 = getCityAndTypeList(houseList);
            // 将收藏列表的城市和类型合并到相应列表中
            typeList.addAll(getTypeList(cityAndType2));
            provinceList.addAll(getProvinceList(cityAndType2));

            List<House> returnHouse = new ArrayList<>();

            // 获取类型列表中占比最高的类型
            String mostCommonType = findMostCommonValue(typeList, "type");
            // 获取随机的相应类型的房屋列表
            List<House> typeHouseList = houseDao.getHouseByTypeRand(mostCommonType, numTypeListToSelect);
            System.out.println(mostCommonType);
            System.out.println(typeHouseList);
            System.out.println(typeHouseList.size());
            System.out.println(numTypeListToSelect);
            // 如果房屋列表数量足够，添加到返回结果中；否则，添加随机房屋列表
            if (typeHouseList.size() >= numTypeListToSelect) {
                returnHouse = typeHouseList;
            } else {
                return new Result(Code.SEARCH_OK, updateCityId(houseDao.getHouseRand()));
            }

            // 获取省份列表中占比最高的城市
            String mostCommonProvince = findMostCommonValue(provinceList);
            // 获取省份对应的城市ID列表
            List<String> cityIds = cityDao.getCityIdListByProvinceZh(mostCommonProvince);
            List<House> provinceHouseList = new ArrayList<>();
            // 获取选定城市ID列表对应的房屋列表
            for (String id : cityIds) {
                provinceHouseList.addAll(houseDao.getHouseByCityId(id));
            }
            // 随机打乱城市房屋列表
            Collections.shuffle(provinceHouseList);

            // 取前numProvinceListToSelect条数据（如果有numProvinceListToSelect条以上的数据）
            List<House> selectedProvinceHouseList = new ArrayList<>();
            if (provinceHouseList.size() >= numProvinceListToSelect) {
                selectedProvinceHouseList = provinceHouseList.subList(0, numProvinceListToSelect);
            } else {
                return new Result(Code.SEARCH_OK, updateCityId(houseDao.getHouseRand()));
            }

            // 将选择的城市房屋列表添加到返回结果中
            returnHouse.addAll(selectedProvinceHouseList);
            returnHouse = updateCityId(returnHouse);
            Collections.shuffle(returnHouse);
            // 返回包含随机房屋列表的Result对象
            return new Result(Code.SEARCH_OK, returnHouse);
        } else {
            // 没有有效的用户ID，返回随机房屋列表
            return new Result(Code.SEARCH_OK, updateCityId(houseDao.getHouseRand()));
        }
    }


    private List<Map<String, String>> getCityAndTypeList(List<String> houseIds) {
        List<Map<String, String>> cityAndTypeList = new ArrayList<>();
        for (String id : houseIds) {
            cityAndTypeList.add(houseDao.getCityAndTypeById(id));
        }
        return cityAndTypeList;
    }

    private List<Map<String, String>> getTypeList(List<Map<String, String>> cityAndTypeList) {
        List<Map<String, String>> typeList = new ArrayList<>();
        for (Map<String, String> item : cityAndTypeList) {
            Map<String, String> newType = new HashMap<>();
            newType.put("type", item.get("type"));
            typeList.add(newType);
        }
        return typeList;
    }

    private List<String> getProvinceList(List<Map<String, String>> cityAndTypeList) {
        List<String> provinceList = new ArrayList<>();
        for (Map<String, String> item : cityAndTypeList) {
            List<String> province = cityDao.getAllProvinceZhById(String.valueOf(item.get("city_id")));
            provinceList.addAll(province);
        }
        return provinceList;
    }

    private static <T> T findMostCommonValue(List<T> list) {
        Map<T, Integer> frequencyMap = new HashMap<>();
        for (T item : list) {
            frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
        }

        return Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private static <T> T findMostCommonValue(List<Map<String, T>> list, String key) {
        List<T> values = new ArrayList<>();
        for (Map<String, T> item : list) {
            values.add(item.get(key));
        }
        return findMostCommonValue(values);
    }

    @Override
    public List<House> getHouseByIndex(String index) {
        return updateCityId(houseDao.getHouseByIndex(Integer.parseInt(index) * 25, 25));
    }

    @Override
    public List<House> getHouseByTypeAndIndex(String type, String index) {
        return updateCityId(houseDao.getHouseByTypeAndIndex(type, Integer.parseInt(index) * 25, 25));
    }

    @Override
    public List<House> getHouseBySearchValue(String consumerId, String searchValue, String index) {
        if (StringUtils.isNotEmpty(searchValue)) {
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setConsumer_id(consumerId);
            searchHistory.setKeyword(searchValue);
            if (consumerId != null && !consumerId.isEmpty()) {
                searchHistoryDao.InsertSearch(searchHistory);
                searchHistoryDao.deleteSearchKeyword(consumerId);
            }
            return updateCityId(houseDao.getHouseBySearchValue(searchValue, Integer.parseInt(index) * 25, 25));
        } else {
            return null;
        }
    }

    @Override
    public List<House> getHouseByCity(String city, String index) {
        if (StringUtils.isNotEmpty(city)) {
            return updateCityId(houseDao.getHouseByCity(city, Integer.parseInt(index) * 25, 25));
        } else {
            return null;
        }
    }

    private List<House> updateCityId(List<House> houseList) {
        for (House house : houseList) {
            Map<String, String> cityMap = cityDao.getCityNameById(house.getCity_id());
            String provinceZh = cityMap.get("provinceZh");
            String cityZh = cityMap.get("cityZh");
            house.setCity_id(provinceZh.equals(cityZh) ? cityZh : provinceZh + "-" + cityZh);
        }
        return houseList;
    }


    @Override
    public Map<String, Object> getHouseById(String houseId) {
        String houseKey = "house" + houseId;
        Object data = redisTemplate.opsForValue().get(houseKey);
        if (data != null) {
            System.out.println("6666");
            return (Map<String, Object>) data;
        } else {
            Map<String, Object> map = new HashMap<>();
            House house = houseDao.getHouseById(houseId);
            System.out.println("house:" + house);

            Field[] fields = house.getClass().getDeclaredFields();
            //将对象的属性作为键，将属性值作为值放入Map中。通过反射设置属性值的方式进行操作
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = null;
                if (fieldName.equals("city_id")) {
                    try {
                        Map<String, String> LatAndLon = cityDao.getLatAndLonById((String) field.get(house));
                        map.put("lon", LatAndLon.get("lon"));
                        map.put("lat", LatAndLon.get("lat"));
                        map.put("provinceZh", LatAndLon.get("provinceZh"));
                        map.put("leaderZh", LatAndLon.get("leaderZh"));
                        map.put("cityZh", LatAndLon.get("cityZh"));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (fieldName.equals("landlord_id")) {
                    try {
                        Map<String, String> landlord = landlordDao.getLandlordById((String) field.get(house));
                        map.put("img_url", landlord.get("img_url"));
                        map.put("LandlordIntroduce", landlord.get("introduce"));
                        map.put("register_time", landlord.get("register_time"));
                        map.put("landlord_name", landlord.get("landlord_name"));
                        map.put("landlord_tele", landlord.get("tele"));
                        map.put("landlord_id", field.get(house));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        fieldValue = field.get(house);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    map.put(fieldName, fieldValue);
                }
            }

            // 将查询结果存储到Redis中，以便下次查询时命中缓存
            redisTemplate.opsForValue().set(houseKey, map);
            redisTemplate.expire(houseKey, 60, TimeUnit.SECONDS); // 设置过期时间为60秒
            return map;
        }
    }

    @Override
    public Result getHouseByLandlordId(String landlordId) {
        List<Map<String, Object>> house = houseDao.getHouseByLandlordId(landlordId);
        if (house != null && house.size() != 0) {
            return new Result(Code.SEARCH_OK, house);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关民宿，请创建");
        }
    }

    @Override
    public Boolean InsertHouse(House house) {
        return houseDao.InsertHouse(house);
    }

    @Override
    public Result UpdateHouseById(House house) {
        if (house.getId() != null){
            System.out.println(house);
            boolean result = houseDao.UpdateHouseById(house);
            if (result) {
                redisTemplate.delete("house" + house.getId());
                return new Result(Code.UPDATE_OK, "更新成功");
            } else {
                return new Result(Code.UPDATE_ERR, "更新失败");
            }
        }else {
            return new Result(Code.UPDATE_ERR, "更新失败");
        }
    }

    @Override
    public Result UpdateHouseStatusById(String status, String id) {
        if (status.equals("0")){
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 格式化日期
            String formattedDate = currentDate.format(formatter);
            List<String> overHouse = houseDao.getOverHouseList2(formattedDate,id);
            if (overHouse.size() == 0){
                boolean flag = houseDao.UpdateHouseStatusById(status,id);
                if (flag){
                    return new Result(Code.UPDATE_OK,"更新成功");
                }
            }
        }
        if (status.equals("1")){
            boolean flag = houseDao.UpdateHouseStatusById(status,id);
            if (flag){
                return new Result(Code.UPDATE_OK,"更新成功");
            }
        }
        return new Result(Code.UPDATE_ERR,"更新失败");
    }
}