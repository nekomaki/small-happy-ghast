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
public abstract class MixinHappyGhastEntity extends PassiveEntity implements GrowingGhast {

    @Unique
    private boolean ghGrowing = false;

    protected MixinHappyGhastEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void hg$writeCustomData(WriteView nbt, CallbackInfo ci) {
        nbt.putBoolean("Growing", this.ghGrowing);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void hg$readCustomData(ReadView nbt, CallbackInfo ci) {
        this.ghGrowing = nbt.getBoolean("Growing", false);
    }

    @Unique
    public boolean isGrowing() {
        return this.ghGrowing;
    }

    @Unique
    public void setGrowing(boolean value) {
        this.ghGrowing = value;
    }

    @Inject(method = "onGrowUp", at = @At("HEAD"), cancellable = true)
    private void hg$onGrowUp(CallbackInfo ci) {
        HappyGhastEntity hg = (HappyGhastEntity) (Object) this;

        if (hg instanceof GrowingGhast gg && !gg.isGrowing()) {
            hg.setBreedingAge(-24000);
            ci.cancel();
        }
    }
}
