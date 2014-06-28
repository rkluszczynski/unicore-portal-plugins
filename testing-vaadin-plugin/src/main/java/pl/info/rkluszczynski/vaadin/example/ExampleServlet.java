package pl.info.rkluszczynski.vaadin.example;

import com.vaadin.server.VaadinServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(
        asyncSupported = false,
        urlPatterns = {"/*", "/VAADIN/*"},
        initParams = {
                @WebInitParam(name = "ui", value = "pl.info.rkluszczynski.vaadin.example.ExampleUI")
        })
public class ExampleServlet extends VaadinServlet {
}
