package com.nekomaki.small_happy_ghast;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HappyGhastControl {

    private static final Map<UUID, Long> LAST_INTERACT_TICK = new ConcurrentHashMap<>();

    public static void init() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {

            if (!canProcess(player, world, hand, entity)) {
                return ActionResult.PASS;
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            HappyGhastEntity hg = (HappyGhastEntity) entity;
            ItemStack stack = player.getStackInHand(hand);

            // Handle snowball feeding
            if (stack.isOf(Items.SNOWBALL)) {
                return handleSnowball(hg, stack, serverPlayer, player, hand);
            }

            return ActionResult.PASS;
        });
    }

    // Basic validation before processing
    private static boolean canProcess(net.minecraft.entity.player.PlayerEntity p, net.minecraft.world.World w, Hand h, net.minecraft.entity.Entity e) {
        return !w.isClient() && h == Hand.MAIN_HAND && p instanceof ServerPlayerEntity && e instanceof HappyGhastEntity;
    }

    // Handles feeding a snowball to the Happy Ghast
    private static ActionResult handleSnowball(
            HappyGhastEntity hg,
            ItemStack stack,
            ServerPlayerEntity player,
            net.minecraft.entity.player.PlayerEntity p,
            Hand hand) {

        // If not already growing, start the growth process
        if (hg instanceof GrowingGhast gg) {
            if (!gg.isGrowing()) {
                hg.setBreedingAge(-24000);
                gg.setGrowing(true);
            }
        }

        // Otherwise, pass interaction to default handler
        return hg.interact(p, hand);
    }
}
