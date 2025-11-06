package com.nekomaki.small_happy_ghast.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(HappyGhast.class)
public abstract class HappyGhastEntityMixin extends AgeableMob {

    @Unique
    private boolean growing = false;

    protected HappyGhastEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private boolean isGrowing() {
        return this.growing;
    }

    @Unique
    private void setGrowing(boolean value) {
        this.growing = value;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void smallHappyGhast$addAdditionalSaveData(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("Growing", this.growing);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void smallHappyGhast$readAdditionalSaveData(ValueInput nbt, CallbackInfo ci) {
        this.growing = nbt.getBooleanOr("Growing", false);
    }

    @Inject(method = "ageBoundaryReached", at = @At("HEAD"), cancellable = true)
    private void smallHappyGhast$ageBoundaryReached(CallbackInfo ci) {
        if (!this.isGrowing()) {
            this.setAge(-24000);
            ci.cancel();
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void smallHappyGhast$mobInteract(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player instanceof ServerPlayer)) return;

        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Items.SNOWBALL)) {
            if (this.isBaby() && !this.isGrowing()) {
                this.setGrowing(true);
                this.setAge(-24000);
            }
        }
    }
}
