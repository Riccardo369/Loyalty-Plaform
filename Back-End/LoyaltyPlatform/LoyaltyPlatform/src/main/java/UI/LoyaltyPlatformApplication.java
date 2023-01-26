package UI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class LoyaltyPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyPlatformApplication.class, args);
    }

    @RequestMapping(value = "/")
    public String Hello(){
        return "Ok champ";
    }

}
