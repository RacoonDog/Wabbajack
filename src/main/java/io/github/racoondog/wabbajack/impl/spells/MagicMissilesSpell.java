package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.impl.MagicMissileProjectileEntity;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.WeakHashMap;

public class MagicMissilesSpell extends WabbajackSpell {
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

        int missiles = Wabbajack.CONFIG.magicMissilesSpell.projectiles;
        spawnProjectile(world, user, stack);
        if (missiles > 1) {
            RUNNING_MAGIC_MISSILES.computeIfAbsent(world, key -> new ObjectArrayList<>()).add(new MagicMissiles(() -> spawnProjectile(world, user, stack), missiles - 1));
        }
    }

    @Override
    public void onDispense(ServerWorld world, Direction direction, Position position, ItemStack stack) {
        int missiles = Wabbajack.CONFIG.magicMissilesSpell.projectiles;
        spawnFromDispenser(world, direction, position, stack);
        if (missiles > 1) {
            RUNNING_MAGIC_MISSILES.computeIfAbsent(world, key -> new ObjectArrayList<>()).add(new MagicMissiles(() -> spawnFromDispenser(world, direction, position, stack), missiles - 1));
        }
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.magicMissiles;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.magicMissiles;
    }

    private static void spawnProjectile(ServerWorld world, PlayerEntity user, ItemStack stack) {
        world.playSoundFromEntity(null, user, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.PLAYERS, 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
        ProjectileEntity.spawnWithVelocity(MagicMissileProjectileEntity::new, world, stack, user, 0.0F, PROJECTILE_SETTINGS.power(), PROJECTILE_SETTINGS.uncertainty());
    }

    private static void spawnFromDispenser(ServerWorld world, Direction direction, Position position, ItemStack stack) {
        ProjectileEntity.spawnWithVelocity(
            new MagicMissileProjectileEntity(world, position.getX(), position.getY(), position.getZ()),
            world, stack,
            direction.getOffsetX(),
            direction.getOffsetY(),
            direction.getOffsetZ(),
            PROJECTILE_SETTINGS.power(),
            PROJECTILE_SETTINGS.uncertainty()
        );
    }

    public static class MagicMissiles {
        private final Runnable spawner;
        private int missilesRemaining;

        public MagicMissiles(Runnable spawner, int missiles) {
            this.spawner = spawner;
            this.missilesRemaining = missiles;
        }

        public void tick() {
            this.spawner.run();
            this.missilesRemaining--;
        }

        public boolean shouldRemove() {
            return this.missilesRemaining < 1;
        }
    }
}
