package com.mmall.util;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2019/3/16/016.
 */
public class BigDecimalUtil {

    public BigDecimalUtil() { }

    public static BigDecimal add(double a1, double a2){
        BigDecimal d1 = new BigDecimal(Double.toString(a1));
        BigDecimal d2 = new BigDecimal(Double.toString(a2));
        return d1.add(d2);
    }

    public static BigDecimal sub(double a1, double a2){
        BigDecimal d1 = new BigDecimal(Double.toString(a1));
        BigDecimal d2 = new BigDecimal(Double.toString(a2));
        return d1.subtract(d2);
    }

    public static BigDecimal mul(double a1, double a2){
        BigDecimal d1 = new BigDecimal(Double.toString(a1));
        BigDecimal d2 = new BigDecimal(Double.toString(a2));
        return d1.multiply(d2);
    }

    public static BigDecimal div(double a1, double a2){
        BigDecimal d1 = new BigDecimal(Double.toString(a1));
        BigDecimal d2 = new BigDecimal(Double.toString(a2));
        return d1.divide(d2,2,BigDecimal.ROUND_HALF_DOWN);
    }

}
