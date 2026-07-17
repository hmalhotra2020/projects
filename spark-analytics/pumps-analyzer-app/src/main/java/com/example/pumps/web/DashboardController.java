package com.example.pumps.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC controller that serves the live analytics dashboard Thymeleaf template.
 */
@Controller
public class DashboardController {

    /**
     * Serves the main dashboard page at the application root.
     *
     * @return Thymeleaf template name {@code dashboard}
     */
    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }
}
