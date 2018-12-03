package org.springframework.samples.petclinic.system;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class WelcomeController {

    @GetMapping("/")
    public String welcome() throws InterruptedException {
        Thread.sleep(10000);
        return "welcome";
    }
}
