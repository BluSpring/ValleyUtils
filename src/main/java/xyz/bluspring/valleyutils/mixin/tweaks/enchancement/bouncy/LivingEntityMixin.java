package xyz.bluspring.valleyutils.mixin.tweaks.enchancement.bouncy;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import moriyashiine.enchancement.common.component.entity.BouncyComponent;
import moriyashiine.enchancement.common.init.ModEnchantments;
import moriyashiine.enchancement.common.init.ModEntityComponents;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1100)
@IfModLoaded("enchancement")
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /*

    @SuppressWarnings("UnresolvedMixinReference")
    @TargetHandler(mixin = "moriyashiine.enchancement.mixin.bouncy.LivingEntityMixin", name = "enchancement$bouncy(FFLnet/minecraft/world/damagesource/DamageSource;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSuppressingBounce()Z"))
    private boolean disableDefaultBounce(LivingEntity instance, Operation<Boolean> original) {
        if (instance instanceof Player player) {
            return ValleyUtils.Companion.getHasValleyUtilsPlayers().contains(player) || original.call(instance);
        }

        return original.call(instance);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void doBounceOnClientSide(CallbackInfo ci) {
        if (this.level().isClientSide()) {
            if (!this.valleyutils$wasOnGround && this.onGround()) {
                this.sendSystemMessage(Component.literal("Fall distance: " + fallDistance));
                if (!this.getFeetBlockState().is(Blocks.POINTED_DRIPSTONE) && fallDistance > (float) this.getMaxFallDistance() && EnchancementUtil.hasEnchantment(ModEnchantments.BOUNCY, this)) {
                    this.level().playSound(null, this, SoundEvents.SLIME_BLOCK_FALL, this.getSoundSource(), 1.0F, 1.0F);
                    if (!this.isSuppressingBounce()) {
                        ModEntityComponents.AIR_MOBILITY.get(this).enableResetBypass();
                        BouncyComponent bouncyComponent = ModEntityComponents.BOUNCY.getNullable(this);
                        if (bouncyComponent != null && bouncyComponent.grappleTimer > 0) {
                            fallDistance = 30.0F;
                        }

                        double bounceStrength = Math.log(fallDistance / 7.0F + 1.0F) / Math.log(1.05) / (double)16.0F;
                        this.setDeltaMovement(this.getDeltaMovement().x(), bounceStrength, this.getDeltaMovement().z());
                        this.hurtMarked = true;
                    }
                }
            }

            this.valleyutils$wasOnGround = this.onGround();
        }
    }*/

    @Unique private boolean valleyutils$wasOnGround = false;
    @Unique private float valleyutils$clientFallDistance = 0f;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void calculateClientSidedFallDistance(CallbackInfo ci) {
        if (this.level().isClientSide()) {
            if (!this.valleyutils$wasOnGround && this.onGround()) {
                //this.sendSystemMessage(Component.literal("Fall distance: " + fallDistance + ", Y delta" + this.getDeltaMovement().y + ", On Ground: " + this.onGround() + ", Was On Ground: " + this.valleyutils$wasOnGround));

                if (!this.getFeetBlockState().is(Blocks.POINTED_DRIPSTONE) && valleyutils$clientFallDistance > (float) this.getMaxFallDistance() && EnchancementUtil.hasEnchantment(ModEnchantments.BOUNCY, this)) {
                    this.level().playSound(null, this, SoundEvents.SLIME_BLOCK_FALL, this.getSoundSource(), 1.0F, 1.0F);
                    if (!this.isSuppressingBounce()) {
                        ModEntityComponents.AIR_MOBILITY.get(this).enableResetBypass();
                        BouncyComponent bouncyComponent = ModEntityComponents.BOUNCY.getNullable(this);
                        if (bouncyComponent != null && bouncyComponent.grappleTimer > 0) {
                            fallDistance = 30.0F;
                        }

                        double bounceStrength = Math.log(valleyutils$clientFallDistance / 7.0F + 1.0F) / Math.log(1.05) / (double) 16.0F;
                        this.setDeltaMovement(this.getDeltaMovement().x(), bounceStrength, this.getDeltaMovement().z());
                        this.hurtMarked = true;
                    }
                }
            }

            this.valleyutils$wasOnGround = this.onGround();

            if (this.getDeltaMovement().y < 0 && !this.onGround()) {
                this.fallDistance -= (float) this.getDeltaMovement().y / 8f;
            } else if (this.onGround() && this.fallDistance > 0) {
                this.fallDistance = 0;
            }

            this.valleyutils$clientFallDistance = this.fallDistance;
        }
    }
}
