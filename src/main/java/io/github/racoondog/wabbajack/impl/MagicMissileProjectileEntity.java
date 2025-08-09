package io.github.racoondog.wabbajack.impl;

import io.github.racoondog.wabbajack.api.ParticleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class MagicMissileProjectileEntity extends ProjectileEntity implements FlyingItemEntity {
    private static final DustParticleEffect EFFECT = new DustParticleEffect(Formatting.DARK_PURPLE.getColorValue(), 1f);

    public MagicMissileProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagicMissileProjectileEntity(World world, double posX, double posY, double posZ) {
        this(ModRegistry.MAGIC_MISSILE_PROJECTILE, world);
        this.setPosition(posX, posY, posZ);
    }

    public MagicMissileProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        this(world, owner.getX(), owner.getEyeY() - 0.1f, owner.getZ());
        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient() || this.getWorld().isChunkLoaded(this.getBlockPos())) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, RaycastContext.ShapeType.COLLIDER);
            Vec3d position = hitResult.getType() != HitResult.Type.MISS ? hitResult.getPos() : this.getPos().add(this.getVelocity());

            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            this.setPosition(position);
            this.tickBlockCollision();
            super.tick();

            if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
                this.hitOrDeflect(hitResult);
            }

            if (this.getWorld().isClient() && (this.age & 1) == 0) {
                this.getWorld().addParticleClient(
                    EFFECT,
                    this.getX(), this.getY(), this.getZ(),
                    this.getVelocity().getX(),
                    this.getVelocity().getY(),
                    this.getVelocity().getZ()
                );
            }
        } else {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity attacker = this.getOwner();
        Entity target = entityHitResult.getEntity();

        DamageSource damageSource = this.getDamageSources().indirectMagic(this, this.getOwner());
        float damage = Wabbajack.CONFIG.magicMissilesSpell.damage;

        if (attacker instanceof LivingEntity livingEntity) {
            livingEntity.onAttacking(target);
        }

        if (target.sidedDamage(damageSource, damage)) {
            this.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }

        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            ParticleHelper.spawnAreaParticles(serverWorld, blockHitResult, 1.5f, EFFECT);
        }

        this.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0f, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.discard();
    }

    @Override
    public ItemStack getStack() {
        return ModRegistry.MAGIC_MISSILE_PROJECTILE_ITEM.getDefaultStack();
    }

    @Override
    public boolean shouldRender(double distance) {
        return this.age < 4 && distance < 12.25 ? false : super.shouldRender(distance);
    }
}
