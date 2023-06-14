// Made by Richard


package de.obey.crownmc.handler;

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.util.MessageUtil;

import java.util.List;
import java.util.Random;

public class AutoBroadcastHandler {

    private final ServerConfig serverConfig;
    private final MessageUtil messageUtil;

    public AutoBroadcastHandler(final ServerConfig serverConfig, final MessageUtil messageUtil) {
        this.serverConfig = serverConfig;
        this.messageUtil = messageUtil;
    }

    private int ticked = 0;
    public void checkBroadcast() {
        if(ticked >= serverConfig.getAutoBroadcastDelay()*10) {
            ticked = 0;

            final List<String> messages = serverConfig.getAutoBroadcastMessages();
            if(!messages.isEmpty())
                messageUtil.broadcast(messages.get(new Random().nextInt(messages.size())));

        }
        ticked++;
    }
}
