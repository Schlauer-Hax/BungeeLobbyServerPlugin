package com.hax.bungeelobbyserverplugin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class XaeroWorldMapListener implements Listener {

    @EventHandler
    public void onPostLogin(ServerConnectedEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ByteBuf buf = Unpooled.buffer(5);
                buf.writeByte(0);

                String[] split = event.getServer().getInfo().getName().split("-");
                String name = String.join(" ", Arrays.copyOf(split, (split.length == 1) ? 1 : split.length-1));
                byte[] bytes = name.getBytes();
                int[] arr = new int[bytes.length];
                for(int i = 0; i < bytes.length; i++) {
                    arr[i] = bytes[i];
                }
                int sum = Math.toIntExact(Math.round(IntStream.of(arr).average().orElse(1)*10000));
                System.out.println("ID: "+sum);
                buf.writeInt(sum);

                event.getPlayer().sendData("xaeroworldmap:main", buf.array());
                System.out.println("Sent data");
            }
        }, 1000);

    }

}
