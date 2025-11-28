package com.kustaurant.crawler.global.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SystemMemoryUtilsTest {

    @Test
    void 시스템_메모리_사용량() {
        double usage = SystemMemoryUtils.getSystemMemoryUsage();
        System.out.println(usage);
    }

    @Test
    void 시스템_사용_가능_메모리() {
        long avail = SystemMemoryUtils.getAvailableMemory();
        System.out.println(avail);
    }
}