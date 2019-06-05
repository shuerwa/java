package com;

import com.sun.tools.javac.util.ServiceLoader;
import org.junit.Test;

public class TestSPI {
    @Test
    public void test(){
        ServiceLoader<UseSPI> loader =ServiceLoader.load(UseSPI.class);
        for (UseSPI useSPI:
             loader) {
            System.out.printf(useSPI.getClass().toString()+"            ");
            useSPI.say();
        }
    }
}
