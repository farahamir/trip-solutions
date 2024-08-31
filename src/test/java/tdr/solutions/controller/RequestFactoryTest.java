package tdr.solutions.controller;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RequestFactoryTest {

    public static MockHttpServletRequestBuilder myFactoryRequestGet(String url) {
        return MockMvcRequestBuilders.get(url)
                .header("X-API-KEY", "AMIR");
    }
    public static MockHttpServletRequestBuilder myFactoryRequestGetWithParam(String url, String param) {
        return MockMvcRequestBuilders.get(url,param)
                .header("X-API-KEY", "AMIR");
    }
    public static MockHttpServletRequestBuilder myFactoryRequestPost(String url) {
        return MockMvcRequestBuilders.post(url)
                .header("X-API-KEY", "AMIR");
    }
}