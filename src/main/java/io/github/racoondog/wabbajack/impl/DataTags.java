package io.github.racoondog.wabbajack.impl;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public final class DataTags {
    public static final TagKey<EntityType<?>> CAN_BE_WABBAJACKED = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Wabbajack.MOD_ID, "can_be_wabbajacked"));
    public static final TagKey<EntityAttribute> CAN_SCRAMBLE = TagKey.of(RegistryKeys.ATTRIBUTE, Identifier.of(Wabbajack.MOD_ID, "can_scramble"));

    /**
     * Returns a random entry from within a tag, but with performance
     *
     * @param registry the registry
     * @param tag the tag
     * @param random the random
     * @param defaultEntry the entry to return if a random entry could not be gotten
     * @param except the entry to skip, or null
     * @return a random entry
     * @param <T> the type
     */
    public static <T> RegistryEntry<T> getRandom(Registry<T> registry, TagKey<T> tag, Random random, Supplier<RegistryEntry<T>> defaultEntry, @Nullable RegistryEntry<T> except) {
        Iterable<RegistryEntry<T>> entries = registry.iterateEntries(tag);
        if (entries instanceof RegistryEntryList<T> registryEntryList) {

            if (registryEntryList.size() == 0) {
                return defaultEntry.get();
            }

            if (except != null && registryEntryList.size() == 1 && registryEntryList.contains(except)) {
                return defaultEntry.get();
            }

            RegistryEntry<T> entry;
            do {
                entry = registryEntryList.get(random.nextInt(registryEntryList.size()));
            } while (entry.value() == except);

            return entry;
        } else { // fallback for potentially non-standard registries
            @SuppressWarnings({"unchecked", "rawtypes"})
            List<RegistryEntry<T>> entryList = TAG_ENTRIES.computeIfAbsent((Iterable) entries, _entries ->
                StreamSupport.stream(_entries.spliterator(), false).toList());

            if (entryList.isEmpty()) {
                return defaultEntry.get();
            }

            if (except != null && entryList.size() == 1 && entryList.contains(except)) {
                return defaultEntry.get();
            }

            RegistryEntry<T> entry;
            do {
                entry = entryList.get(random.nextInt(entryList.size()));
            } while (entry == except);

            return entry;
        }
    }

    private static final WeakHashMap<Iterable<RegistryEntry<?>>, List<?>> TAG_ENTRIES = new WeakHashMap<>();
}
