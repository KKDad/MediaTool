package org.westfield.http;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ParameterBuilderTest
{
    @Test
    public void oneParameterTest() {
        try {

            Map input = ImmutableMap.of("key", "value");

            String result = ParameterBuilder.build(input);
            Assert.assertEquals("key=value", result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void twoParametersTest() {
        try {

            Map input = ImmutableMap.of("key1", "value1", "key2", "value2");

            String result = ParameterBuilder.build(input);
            Assert.assertEquals("key1=value1&key2=value2", result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void urlEncodedTest() {
        try {
            Map input = ImmutableMap.of("key with space", "value with space");

            String result = ParameterBuilder.build(input);
            Assert.assertEquals("key+with+space=value+with+space", result);
        } catch (Exception ex) {
            fail();
        }
    }
}