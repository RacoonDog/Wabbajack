package io.github.racoondog.wabbajack.impl.compat.arealib;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class WabbajackAreaComponent implements AreaDataComponent {
    public boolean disabled = false;
    public TriState pvp = TriState.DEFAULT;
    public Set<EntityType<?>> canBeWabbajacked = new ReferenceOpenHashSet<>();
    public Set<EntityType<?>> cannotBeWabbajacked = new ReferenceOpenHashSet<>();

    @Override
    public void load(AreaSavedData areaSavedData, NbtCompound nbtCompound) {
        disabled = nbtCompound.getBoolean("disabled", false);
        pvp = nbtCompound.getBoolean("pvp").map(TriState::of).orElse(TriState.DEFAULT);
        canBeWabbajacked = deserializeSet(new ReferenceOpenHashSet<>(), nbtCompound.getList("canBeWabbajacked"),
            nbtElement -> nbtElement.asString().map(Identifier::tryParse).map(Registries.ENTITY_TYPE::get));
        cannotBeWabbajacked = deserializeSet(new ReferenceOpenHashSet<>(), nbtCompound.getList("cannotBeWabbajacked"),
            nbtElement -> nbtElement.asString().map(Identifier::tryParse).map(Registries.ENTITY_TYPE::get));
    }

    @Override
    public NbtCompound save() {
        NbtCompound compound = new NbtCompound();

        compound.putBoolean("disabled", disabled);
        if (pvp != TriState.DEFAULT) compound.putBoolean("pvp", pvp.get());
        if (!canBeWabbajacked.isEmpty()) compound.put("canBeWabbajacked", serializeSet(canBeWabbajacked,
            entityType -> Registries.ENTITY_TYPE.getKey(entityType).map(key -> key.getValue().toString())));
        if (!cannotBeWabbajacked.isEmpty()) compound.put("cannotBeWabbajacked", serializeSet(cannotBeWabbajacked,
            entityType -> Registries.ENTITY_TYPE.getKey(entityType).map(key -> key.getValue().toString())));

        return compound;
    }

    // serialization

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalIsPresent"})
    private static <T> Set<T> deserializeSet(Set<T> set, Optional<NbtList> list, Function<NbtElement, Optional<T>> deserializer) {
        if (list.isPresent()) list.get().stream().map(deserializer)
            .filter(Optional::isPresent)
            .map(Optional::get).forEach(set::add);
        return set;
    }

    private static <T> NbtList serializeSet(Set<T> set, Function<T, Optional<String>> serializer) {
        NbtList list = new NbtList();
        set.stream().map(serializer).filter(Optional::isPresent)
            .map(optional -> NbtString.of(optional.get())).forEach(list::add);
        return list;
    }

    // helper methods

    private static Optional<WabbajackAreaComponent> getArea(World world, Vec3d pos) {
        return AreaSavedData.getServerData(world.getServer()).findTrackedAreasContaining(world, pos).stream()
            .filter(area -> area.has(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT))
            .min(Comparator.comparingDouble(area -> area.getBoundingBox().getAverageSideLength()))
            .map(area -> area.get(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT));
    }

    public static boolean shouldDisableWabbajack(World world, Vec3d pos) {
        return getArea(world, pos).map(component -> component.disabled).orElse(false);
    }

    public static TriState canPvp(World world, Vec3d pos) {
        return getArea(world, pos).map(component -> component.pvp).orElse(TriState.DEFAULT);
    }

    public static Set<EntityType<?>> canBeWabbajacked(World world, Vec3d pos) {
        return getArea(world, pos).map(component -> component.canBeWabbajacked).orElse(Set.of());
    }

    public static Set<EntityType<?>> cannotBeWabbajacked(World world, Vec3d pos) {
        return getArea(world, pos).map(component -> component.cannotBeWabbajacked).orElse(Set.of());
    }
}
