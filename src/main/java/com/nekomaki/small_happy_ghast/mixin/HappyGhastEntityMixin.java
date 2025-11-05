package com.nekomaki.small_happy_ghast.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.nekomaki.small_happy_ghast.GrowingGhast;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(HappyGhast.class)
public abstract class HappyGhastEntityMixin extends AgeableMob implements GrowingGhast {

    @Unique
    private boolean growing = false;

    protected HappyGhastEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    public boolean isGrowing() {
        return this.growing;
    }

    @Unique
    public void setGrowing(boolean value) {
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
        HappyGhast hg = (HappyGhast) (Object) this;

        if (hg instanceof GrowingGhast gg && !gg.isGrowing()) {
            hg.setAge(-24000);
            ci.cancel();
        }
    }
}
