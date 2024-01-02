package com.example.rental;

import com.example.rental.controller.HouseController;
import com.example.rental.service.HouseService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class RentalApplicationTests {

    @InjectMocks
    private HouseController houseController;

    @Mock
    private HouseService houseService;  // 假设你的 HouseService 被注入到 Controller 中

    @Test
    void getHouseRandTest() {
        // 假设你的 houseService.getHouseRand 方法会返回一个 Result 对象
        Result expectedResult = new Result(Code.SEARCH_OK, "Random house data");

        // 使用 Mockito 框架模拟 houseService 的行为
        when(houseService.getHouseRand(anyString())).thenReturn(expectedResult);

        // 调用控制器方法
        Result actualResult = houseController.getHouseRand("3");

    }

}
