package io.netty.cases.chapter.demo1;

import java.util.concurrent.TimeUnit;

/**
 * Created by 李林峰 on 2018/8/3.
 */
public class SignalHandlerTest {

    public static void main(String[] args) throws Exception {
        //1  SIGHUP 挂起进程
        //
        //2  SIGINT 终止进程
        //
        //3  SIGGQUIT 停止进程
        //
        //9  SIGKILL 无条件终止进程
        //
        //15  SIGTERM 尽可能终止进程
        //
        //17  SIGSTOP 无条件停止进程，但不是终止
        //
        //18  SIGTSTP 停止或者暂停进程，但不终止进程
        //
        //19  SIGCONT 继续运行停止的进程

        //以windows操作系统为例 INT
        //kill -15 TERM
//        Signal sig = new Signal("TERM");
//        Signal.handle(sig, (s) -> {
//            System.out.println("Signal handle start...");
//            try {
//                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("ShutdownHook execute start...");
            System.out.println("Netty NioEventLoopGroup shutdownGracefully...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ShutdownHook execute end...");
        }, ""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.DAYS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Daemon-T").start();
    }
}
