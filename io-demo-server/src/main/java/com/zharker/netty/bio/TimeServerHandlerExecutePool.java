package com.zharker.netty.bio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize){
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
    }
    public void execute(Runnable task){
        executor.execute(task);
    }
    public void close(){
        if(executor != null && !executor.isTerminated()){
            executor.shutdown();
            executor = null;
        }
    }
}
