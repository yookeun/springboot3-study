package com.example.study.member.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
public class AccessTestController {

    @GetMapping("/item")
    @PreAuthorize("hasAuthority('ITEM2')")
    public String accessItem() {
        return "Hello Item";
    }

    @GetMapping("/order")
    @PreAuthorize("hasAuthority('ORDER')")
    public String accessOrder() {
        return "Hello Order";
    }

    @GetMapping("/item/order")
    @PreAuthorize("hasAnyAuthority('ITEM','ORDER')")
    public String accessOrderOrItem() {
        return "Hello Item and Order";
    }

    @GetMapping("/anyone")
    public String accessAnyone() {
        return "Hello Anyone";
    }

}
