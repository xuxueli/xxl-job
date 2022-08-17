package com.xxl.job.admin.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static com.xxl.job.admin.core.util.JacksonUtil.writeValueAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonUtilTest {

    @Test
    public void shouldWriteValueAsString() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("aaa", "111");
        map.put("bbb", "222");

        //when
        String json = writeValueAsString(map);

        //then
        assertEquals(json, "{\"aaa\":\"111\",\"bbb\":\"222\"}");
    }

    @Test
    public void shouldReadValueAsObject() {
        //given
        String jsonString = "{\"aaa\":\"111\",\"bbb\":\"222\"}";

        //when
        Map result = JacksonUtil.readValue(jsonString, Map.class);

        //then
        assertEquals(result.get("aaa"), "111");
        assertEquals(result.get("bbb"),"222");

    }

    @Test
    @DisplayName("should write null value as 'null' string")
    public void testShouldWriteValueAsString(){
        //given null
        Map<String, String> map = null;

        //when
        String json = writeValueAsString(map);

        //then
        assertThat(json).isEqualTo("null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"","  ","{","}","{a:}"})
    @DisplayName("should read invalid content as null objects")
    public void testWhenThatContentIsInvalid(String invalidJsonString) {
        //given invalidJsonString

        //when
        Map result = JacksonUtil.readValue(invalidJsonString, Map.class);

        //then
        assertThat(result).isNull();

    }

    @Test
    @DisplayName("should read null content as null objects")
    public void testWhenThatContentIsNull() {
        //given
        String jsonString = null;

        //when
        Map result = JacksonUtil.readValue(jsonString, Map.class);

        //then
        assertThat(result).isNull();

    }

    @Test
    public void testWhenClazzIsNull(){
        //given
        String jsonString = "{\"aaa\":\"111\",\"bbb\":\"222\"}";
        Class<JacksonUtilTest> clazz = null;

        //when
        JacksonUtilTest result = JacksonUtil.readValue(jsonString, clazz);

        //then
        assertThat(result).isNull();
    }
}
