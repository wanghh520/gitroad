package com.daway;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.daway.service.RoadLedPush;

@Component
@ServletComponentScan
public class MyApplicationRunner implements ApplicationRunner {
	@Autowired
	RoadLedPush roadledpush = new RoadLedPush();
	
	@Override
    public void run(ApplicationArguments var1) throws Exception{
        System.out.println("MyApplicationRunner class will be execute when the project was started!");
        roadledpush.pushLedColor(); 
    }
 
}
