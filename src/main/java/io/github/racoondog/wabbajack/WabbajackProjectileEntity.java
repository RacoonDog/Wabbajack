package io.github.racoondog.wabbajack;

import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionImpl;

import java.util.concurrent.ThreadLocalRandom;

public class WabbajackProjectileEntity extends ProjectileEntity implements FlyingItemEntity {
    private static final DustParticleEffect TRAIL_EFFECT = new DustParticleEffect(Colors.RED, 1f);

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

            if (this.getWorld().isClient() && (this.age & 1) == 0) {
                this.getWorld().addParticleClient(
                    TRAIL_EFFECT,
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
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity hit = null;
            if (hitResult instanceof EntityHitResult entityHitResult) {
                hit = entityHitResult.getEntity();
            }

            for (Entity entity : serverWorld.getOtherEntities(this, this.getBoundingBox().expand(3f))) {
                if (!(entity instanceof LivingEntity)
                    || entity instanceof PlayerEntity
                    || entity.isInvulnerable()) continue;

                if (entity == hit || (entity.squaredDistanceTo(this) < 3f * 3f && ExplosionImpl.calculateReceivedDamage(this.getPos(), entity) > 0.1f)) {
                    entity.discard();

                    getRandomEntity(entity.getType()).spawn(serverWorld, null, entity.getBlockPos(), SpawnReason.MOB_SUMMONED, false, false);
                }
            }

            this.discard();
        }
    }

    private static EntityType<? extends Entity> getRandomEntity(EntityType<?> current) {
        EntityType<?> entityType = null;

        int depth = 0;
        while (entityType == null || !entityType.isIn(DataTags.CAN_TRANSFORM_INTO) || entityType == current) {
            if (depth++ == 10) {
                return EntityType.TADPOLE;
            }

            entityType = Registries.ENTITY_TYPE.get(ThreadLocalRandom.current().nextInt(Registries.ENTITY_TYPE.size()));
        }

        return entityType;
    }

    @Override
    public ItemStack getStack() {
        return ModRegistry.WABBAJACK_PROJECTILE_ITEM.getDefaultStack();
    }

    @Override
    public boolean shouldRender(double distance) {
        return this.age < 4 && distance < 12.25 ? false : super.shouldRender(distance);
    }
}
