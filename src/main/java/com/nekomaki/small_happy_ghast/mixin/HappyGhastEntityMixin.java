package com.nekomaki.small_happy_ghast.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.nekomaki.small_happy_ghast.GrowingGhast;

@Mixin(HappyGhastEntity.class)
public abstract class HappyGhastEntityMixin extends PassiveEntity implements GrowingGhast {

    @Unique
    private boolean growing = false;

    protected HappyGhastEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void smallHappyGhast$writeCustomData(WriteView nbt, CallbackInfo ci) {
        nbt.putBoolean("Growing", this.growing);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void smallHappyGhast$readCustomData(ReadView nbt, CallbackInfo ci) {
        this.growing = nbt.getBoolean("Growing", false);
    }

    @Unique
    public boolean isGrowing() {
        return this.growing;
    }

    @Unique
    public void setGrowing(boolean value) {
        this.growing = value;
    }

    @Inject(method = "onGrowUp", at = @At("HEAD"), cancellable = true)
    private void smallHappyGhast$onGrowUp(CallbackInfo ci) {
        HappyGhastEntity hg = (HappyGhastEntity) (Object) this;

        if (hg instanceof GrowingGhast gg && !gg.isGrowing()) {
            hg.setBreedingAge(-24000);
            ci.cancel();
        }
    }
}
