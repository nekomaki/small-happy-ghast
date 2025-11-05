package com.nekomaki.small_happy_ghast;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HappyGhastControl {

    private static final Map<UUID, Long> LAST_INTERACT_TICK = new ConcurrentHashMap<>();

    public static void init() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {

            if (!canProcess(player, world, hand, entity)) {
                return InteractionResult.PASS;
            }

            ServerPlayer serverPlayer = (ServerPlayer) player;
            HappyGhast hg = (HappyGhast) entity;
            ItemStack stack = player.getItemInHand(hand);

            // Handle snowball feeding
            if (stack.is(Items.SNOWBALL)) {
                return handleSnowball(hg, stack, serverPlayer, player, hand);
            }

            return InteractionResult.PASS;
        });
    }

    // Basic validation before processing
    private static boolean canProcess(net.minecraft.world.entity.player.Player p, net.minecraft.world.level.Level w, InteractionHand h, net.minecraft.world.entity.Entity e) {
        return !w.isClientSide() && h == InteractionHand.MAIN_HAND && p instanceof ServerPlayer && e instanceof HappyGhast;
    }

    // Handles feeding a snowball to the Happy Ghast
    private static InteractionResult handleSnowball(
            HappyGhast hg,
            ItemStack stack,
            ServerPlayer player,
            net.minecraft.world.entity.player.Player p,
            InteractionHand hand) {

        // If not already growing, start the growth process
        if (hg.isBaby()) {
            if (hg instanceof GrowingGhast gg && !gg.isGrowing()) {
                hg.setAge(-24000);
                gg.setGrowing(true);
            }
        }

        // Otherwise, pass interaction to default handler
        return hg.interact(p, hand);
    }
}
