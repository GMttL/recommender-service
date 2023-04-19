package com.gmitit01.recommenderservice.listener;

import com.gmitit01.recommenderservice.logic.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/***
 * This listener is applied at the startup of the application.
 * It checks whether the model is expired, and if so retrains it.
 *
 * (Expired is defined as the model being older than 24 hours)
 */

@RequiredArgsConstructor
@Component
public class StartupModelExpirationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Model model;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (model.isModelExpired()) {
            model.retrainModel();
        }
    }
}
