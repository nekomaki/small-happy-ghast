package com.nekomaki.small_happy_ghast;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class SmallHappyGhast implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            HappyGhastControl.init();
        });
    }
}
