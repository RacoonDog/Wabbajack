package io.github.racoondog.wabbajack;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WabbajackProjectileEntity extends ProjectileEntity implements FlyingItemEntity {
    public WabbajackProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public WabbajackProjectileEntity(World world, double posX, double posY, double posZ) {
        this(ModRegistry.WABBAJACK_PROJECTILE, world);
        this.setPosition(posX, posY, posZ);
    }

    public WabbajackProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
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
        } else {
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.createExplosion(
                this,
                this.getDamageSources().indirectMagic(this, this.getOwner()),
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                3,
                false,
                World.ExplosionSourceType.MOB
            );

            this.discard();
        }
    }

    @Override
    public ItemStack getStack() {
        return ModRegistry.WABBAJACK_PROJECTILE_ITEM.getDefaultStack();
    }
}
