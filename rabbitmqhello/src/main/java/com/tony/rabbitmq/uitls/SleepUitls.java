package com.tony.rabbitmq.uitls;

public class SleepUitls {



//秒
    public static void sleepSecond(int second){

        try {
            Thread.sleep(second *1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }





}
