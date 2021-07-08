package com.hax.bungeelobbyserverplugin;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent e){
        ServerPing serverPing = e.getResponse();
        serverPing.setDescriptionComponent(TextComponent.fromLegacyText("BBN ist die Gang - "+ServerHelper.getServers().size()+" Servers online")[0]);
        e.setResponse(serverPing);
    }

}
