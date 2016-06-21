/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.openapi.utils.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author runarbe
 */
@WebListener
public class BackgroundJobManager implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
//        scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(new CheckIndexQueueJob(), 0, 10, TimeUnit.SECONDS);
//        System.out.println(Logger.getCurrentTimeStamp() + " - Started background job manager");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
//        scheduler.shutdownNow();
//        System.out.println("Shut down background job manager");
    }
}
