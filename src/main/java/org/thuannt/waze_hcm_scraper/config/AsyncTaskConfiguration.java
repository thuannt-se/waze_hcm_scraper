package org.thuannt.waze_hcm_scraper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncTaskConfiguration {

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Set the initial number of threads in the pool
        executor.setMaxPoolSize(20); // Set the maximum number of threads in the pool
        executor.setQueueCapacity(35); // Set the queue capacity for holding pending tasks
        executor.setThreadNamePrefix("AsyncTask-"); // Set a prefix for thread names

        // Set the RejectedExecutionHandler to log an error message
        executor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor e) -> {
            // Log the error message indicating the task has been rejected due to the full queue
            // You can customize this message based on your requirements
            log.error("Task Rejected: Thread pool is full. Increase the thread pool size.");
        });

        executor.initialize();
        return executor;
    }

}
