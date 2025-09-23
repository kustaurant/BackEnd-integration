//package com.kustaurant.mainapp.common.discordAlert;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.server.ResponseStatusException;
//
//@RestController
//public class OpsTestController {
//    @GetMapping("/test/boom")
//    public String boom() { throw new RuntimeException("test boom - discord"); }
//
//    @GetMapping("/test/boom501")
//    public String boom2() { throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,"test 501 - discord"); }
//    @GetMapping("/test/boom502")
//    public String boom502() {
//        throw new org.springframework.web.server.ResponseStatusException(
//                HttpStatus.BAD_GATEWAY, "test 502 - discord");
//    }
//    @GetMapping("/test/boom503")
//    public String boom503() {
//        throw new org.springframework.web.server.ResponseStatusException(
//                HttpStatus.SERVICE_UNAVAILABLE, "test 503 - discord");
//    }
//
//
//}
