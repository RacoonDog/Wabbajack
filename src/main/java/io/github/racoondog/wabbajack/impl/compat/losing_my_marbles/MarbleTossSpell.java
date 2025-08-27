package io.github.racoondog.wabbajack.impl.compat.losing_my_marbles;

import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import io.github.racoondog.wabbajack.impl.DataTags;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesDataComponents;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesEntities;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesRegistries;
import one.devos.nautical.losing_my_marbles.content.marble.MarbleEntity;
import one.devos.nautical.losing_my_marbles.content.marble.data.MarbleInstance;
import one.devos.nautical.losing_my_marbles.content.marble.data.MarbleType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MarbleTossSpell extends WabbajackSpell {
    private static final TagKey<MarbleType> CAN_BE_TOSSED = TagKey.of(LosingMyMarblesRegistries.MARBLE_TYPE, Identifier.of(Wabbajack.MOD_ID, "can_be_tossed"));
    private static final ProjectileItem.Settings PROJECTILE_SETTINGS = ProjectileItem.Settings.builder()
        .power(1.5f)
        .uncertainty(1.0f)
        .build();
    private final MarbleTossConfig config;

    public MarbleTossSpell(MarbleTossConfig config) {
        this.config = config;
    }

    @Override
    public void onItemUse(ServerWorld world, PlayerEntity user, ItemStack stack) {
        // ProjectileEntity#setVelocity
        float f = -MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));
        float g = -MathHelper.sin(user.getPitch() * (float) (Math.PI / 180.0));
        float h = MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0));

        spawn(world, user, user.getEyePos(), f, g, h);
        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public void onDispense(ServerWorld world, Direction direction, Position position, ItemStack stack) {
        spawn(world, null, position, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    private void spawn(ServerWorld world, @Nullable PlayerEntity owner, Position position, double vx, double vy, double vz) {
        double power = PROJECTILE_SETTINGS.power();
        double uncertainty = PROJECTILE_SETTINGS.uncertainty();

        Registry<MarbleType> marbleTypeRegistry = world.getRegistryManager().getOrThrow(LosingMyMarblesRegistries.MARBLE_TYPE);
        Supplier<RegistryEntry<MarbleType>> defaultTypeSupplier = () -> marbleTypeRegistry.getOptional(MarbleType.DEFAULT)
            .or(() -> marbleTypeRegistry.streamEntries().findFirst()).orElseThrow();

        for (int i = 0; i < this.config.marbles; i++) {
            MarbleEntity marble = new MarbleEntity(
                LosingMyMarblesEntities.MARBLE,
                world,
                new MarbleInstance(
                    DataTags.getRandom(marbleTypeRegistry, CAN_BE_TOSSED, world.random, defaultTypeSupplier, null),
                    ComponentChanges.builder().add(LosingMyMarblesDataComponents.NO_PICKUP, Unit.INSTANCE).build()
                )
            );
            marble.setPosition(position.getX(), position.getY(), position.getZ());
            marble.setOwner(owner);

            // ProjectileEntity#setVelocity
            Vec3d velocity = new Vec3d(vx, vy, vz).normalize().add(
                marble.getRandom().nextTriangular(0.0, 0.0172275 * uncertainty),
                marble.getRandom().nextTriangular(0.0, 0.0172275 * uncertainty),
                marble.getRandom().nextTriangular(0.0, 0.0172275 * uncertainty)
            ).multiply(power);

            marble.setVelocity(velocity);
            marble.velocityDirty = true;
            double d = velocity.horizontalLength();
            marble.setYaw((float)(MathHelper.atan2(velocity.x, velocity.z) * 57.2957763671875));
            marble.setPitch((float)(MathHelper.atan2(velocity.y, d) * 57.2957763671875));
            marble.lastYaw = marble.getYaw();
            marble.lastPitch = marble.getPitch();

            world.spawnEntity(marble);
        }
    }

    @Override
    public boolean isEnabled() {
        return this.config.enabled;
    }

    @Override
    public int getWeight() {
        return this.config.weight;
    }
}
