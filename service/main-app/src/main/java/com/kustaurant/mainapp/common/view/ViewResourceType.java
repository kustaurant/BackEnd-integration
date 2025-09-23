package com.kustaurant.mainapp.common.view;

public enum ViewResourceType {
        POST("post"), RESTAURANT("restaurant");

        private final String key;
        ViewResourceType(String key) { this.key = key; }
        public String key() { return key; }

        public String slot(long id) { return "{%s:%d}".formatted(key, id); }

        public static ViewResourceType fromKey(String key) {
                return switch (key) {
                        case "post" -> POST;
                        case "restaurant" -> RESTAURANT;
                        default -> throw new IllegalArgumentException("unknown type: " + key);
                };
        }
}
