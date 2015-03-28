package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.service.HelloWorldService;

import java.util.concurrent.Callable;

@RestController
public class HelloWorldController {
    @Autowired
    private HelloWorldService helloWorldService;

    @RequestMapping("/")
    public String helloWorld() {
        return this.helloWorldService.getHelloMessage();
    }

    @RequestMapping("/async")
    public Callable<String> helloWorldAsync() {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "async: "
                        + HelloWorldController.this.helloWorldService.getHelloMessage();
            }
        };
    }

    private Log log = LogFactory.getLog(HelloWorldController.class);
}
