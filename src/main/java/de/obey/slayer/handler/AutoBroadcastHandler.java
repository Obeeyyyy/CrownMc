// Made by Richard


package de.obey.slayer.handler;

import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.util.MessageUtil;

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
