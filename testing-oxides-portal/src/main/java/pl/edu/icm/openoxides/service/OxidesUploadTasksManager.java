package pl.edu.icm.openoxides.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class OxidesUploadTasksManager {
    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public OxidesUploadTasksManager(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
