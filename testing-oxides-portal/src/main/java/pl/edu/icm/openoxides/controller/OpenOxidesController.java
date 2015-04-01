package pl.edu.icm.openoxides.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.icm.openoxides.service.input.OxidesPortalData;

//@RestController
@RequestMapping("/oxides")
public class OpenOxidesController {

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public OxidesPortalData saveDataOnGrid(
            @RequestParam(value = "message", defaultValue = "OpenOxides") String message
    ) {
        return new OxidesPortalData(message);
    }
}
