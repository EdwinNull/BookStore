package com.weblearning.bookstore.pojo;

public class CreditLevel {
    public static final int LEVEL_1 = 1; // 10% 折扣，不能透支
    public static final int LEVEL_2 = 2; // 15% 折扣，不能透支
    public static final int LEVEL_3 = 3; // 15% 折扣，可透支有限额
    public static final int LEVEL_4 = 4; // 20% 折扣，可透支有限额
    public static final int LEVEL_5 = 5; // 25% 折扣，无透支限制
}
