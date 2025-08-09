package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.MagicMissileProjectileEntity;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.WabbajackEffect;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.WeakHashMap;

public class MagicMissilesEffect extends WabbajackEffect {
    private static final WeakHashMap<ServerWorld, List<MagicMissiles>> RUNNING_MAGIC_MISSILES = new WeakHashMap<>();
    private static final ProjectileItem.Settings PROJECTILE_SETTINGS = ProjectileItem.Settings.builder()
        .power(1.5f)
        .uncertainty(1.0f)
        .build();

    public static void tick(ServerWorld world) {
        @Nullable List<MagicMissiles> list = RUNNING_MAGIC_MISSILES.get(world);
        if (list != null) {
            list.forEach(MagicMissiles::tick);
            list.removeIf(MagicMissiles::shouldRemove);
        }
    }

    @Override
    public void onItemUse(ServerWorld world, PlayerEntity user, ItemStack stack) {
        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));

        int missiles = Wabbajack.CONFIG.magicMissilesEffect.projectiles;
        spawnProjectile(world, user, stack);
        if (missiles > 1) {
            RUNNING_MAGIC_MISSILES.computeIfAbsent(world, key -> new ObjectArrayList<>()).add(new MagicMissiles(world, user, stack, missiles - 1));
        }
    }

    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {}

    private static void spawnProjectile(ServerWorld world, PlayerEntity user, ItemStack stack) {
        world.playSoundFromEntity(null, user, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.PLAYERS, 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
        ProjectileEntity.spawnWithVelocity(MagicMissileProjectileEntity::new, world, stack, user, 0.0F, PROJECTILE_SETTINGS.power(), PROJECTILE_SETTINGS.uncertainty());
    }

    public static class MagicMissiles {
        private final ServerWorld world;
        private final PlayerEntity caster;
        private final ItemStack stack;
        private int missilesRemaining;

        public MagicMissiles(ServerWorld world, PlayerEntity caster, ItemStack stack, int missiles) {
            this.world = world;
            this.caster = caster;
            this.stack = stack;
            this.missilesRemaining = missiles;
        }

        public void tick() {
            spawnProjectile(this.world, this.caster, this.stack);
            this.missilesRemaining--;
        }

        public boolean shouldRemove() {
            return this.missilesRemaining < 1;
        }
    }
}
