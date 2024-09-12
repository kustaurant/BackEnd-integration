package com.kustaurant.restauranttier.crawling;

public abstract class SelectorConst {
    public static String entryIframe = "#entryIframe";

    // 식당 정보
    public static String restaurantName = ".GHAhO";
    public static String restaurantType = ".lnJFt";
    public static String restaurantAddress = ".LDgIH";
    public static String restaurantTel = ".LDgIH";
    public static String restaurantImgUrl = "#ibu_1";

    // 메뉴 관련
    public static String menuBar = ".flicking-camera > a";

    public static String type1and2Menus = "div.item_info > a.info_link";
    public static String type1and2MenuName = "div.info_detail > div.tit";
    public static String type1and2MenuPrice = "div.info_detail > div.price";
    public static String type1and2isMenuImgExist = "div.info_img";
    public static String type1and2MenuImgUrl = "div.info_img > span.img_box > img, div.info_img > span.img_box > svg.icon";


    public static String type3and4Menus = "a.xPf1B";
    public static String type3and4MenuName = "div.yQlqY > span.lPzHi";
    public static String type3and4MenuPrice = "div.GXS1X";
    public static String type3and4isMenuImgExist = "div.YBmM2";
    public static String type3and4MenuImgUrl = "div.place_thumb > div.K0PDV, div.place_thumb > div.I2Jkv > img";

}
