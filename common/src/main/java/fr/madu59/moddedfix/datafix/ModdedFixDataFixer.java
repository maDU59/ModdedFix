package fr.madu59.moddedfix.datafix;

import fr.madu59.moddedfix.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModdedFixDataFixer {
    public static final Map<String, String> BLOCKS = new HashMap<>();
    public static final Map<String, String> BLOCK_ENTITIES = new HashMap<>();
    public static final Map<String, String> ITEMS = new HashMap<>();

    public static void init() {
        registerBlockConversion("minecraft:iron_chain", "minecraft:chain");
        registerBlockConversion("minecraft:soul_lantern", "minecraft:soul_fire_lantern");
        registerItemConversion("minecraft:totem_of_undying", "minecraft:totem");

        if(!Services.PLATFORM.isModLoaded("lootr")) {
            registerBlockEntityAndBlockConversion("lootr:lootr_chest", "minecraft:chest");
            registerBlockEntityAndBlockConversion("lootr:lootr_trapped_chest", "minecraft:trapped_chest");
            registerBlockEntityAndBlockConversion("lootr:lootr_barrel", "minecraft:barrel");
            registerBlockEntityAndBlockConversion("lootr:lootr_shulker_box", "minecraft:shulker_box");
            registerBlockEntityAndBlockConversion("lootr:lootr_decorated_pot", "minecraft:lootr_decorated_pot");
            registerBlockEntityAndBlockConversion("lootr:lootr_suspicious_sand", "minecraft:lootr_suspicious_sand");
            registerBlockEntityAndBlockConversion("lootr:lootr_suspicious_gravel", "minecraft:lootr_suspicious_gravel");
            registerBlockEntityAndBlockConversion("lootr:lootr_copper_chest", "minecraft:lootr_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:lootr_oxidized_copper_chest", "minecraft:lootr_oxidized_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:lootr_exposed_copper_chest", "minecraft:lootr_exposed_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:lootr_weathered_copper_chest", "minecraft:lootr_weathered_copper_chest");

            registerBlockEntityAndBlockConversion("lootr:chest", "minecraft:chest");
            registerBlockEntityAndBlockConversion("lootr:trapped_chest", "minecraft:trapped_chest");
            registerBlockEntityAndBlockConversion("lootr:barrel", "minecraft:barrel");
            registerBlockEntityAndBlockConversion("lootr:shulker_box", "minecraft:shulker_box");
            registerBlockEntityAndBlockConversion("lootr:decorated_pot", "minecraft:lootr_decorated_pot");
            registerBlockEntityAndBlockConversion("lootr:suspicious_sand", "minecraft:lootr_suspicious_sand");
            registerBlockEntityAndBlockConversion("lootr:suspicious_gravel", "minecraft:lootr_suspicious_gravel");
            registerBlockEntityAndBlockConversion("lootr:copper_chest", "minecraft:lootr_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:oxidized_copper_chest", "minecraft:lootr_oxidized_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:exposed_copper_chest", "minecraft:lootr_exposed_copper_chest");
            registerBlockEntityAndBlockConversion("lootr:weathered_copper_chest", "minecraft:lootr_weathered_copper_chest");
        }
    }

    public static void registerBlockConversion(String oldBlockId, String newBlockId) {
        BLOCKS.put(oldBlockId, newBlockId);
        registerItemConversion(oldBlockId, newBlockId);
    }

    public static void registerBlockOnlyConversion(String oldBlockId, String newBlockId) {
        BLOCKS.put(oldBlockId, newBlockId);
    }

    public static void registerBlockEntityConversion(String oldBlockEntityId, String newBlockEntityId) {
        BLOCK_ENTITIES.put(oldBlockEntityId, newBlockEntityId);
    }

    public static void registerBlockEntityAndBlockConversion(String oldBlockEntityId, String newBlockId) {
        registerBlockEntityConversion(oldBlockEntityId, newBlockId);
        registerBlockConversion(oldBlockEntityId, newBlockId);
    }

    public static void registerItemConversion(String oldItemId, String newItemId) {
        ITEMS.put(oldItemId, newItemId);
    }

    public static void remapChunkBlocks(CompoundTag chunkData){
        if (ModdedFixDataFixer.BLOCKS.isEmpty()) return;

        ListTag sections = chunkData.getListOrEmpty("sections");
        for (int i = 0; i < sections.size(); i++) {
            sections.getCompound(i)
                    .flatMap(s -> s.getCompound("block_states"))
                    .flatMap(bs -> bs.getList("palette"))
                    .ifPresent(palette -> {
                        for (int j = 0; j < palette.size(); j++) {
                            Tag element = palette.get(j);
                            if (element instanceof StringTag str) {
                                String replacement = remapBlock(str.value());
                                if (replacement != null) palette.set(j, StringTag.valueOf(replacement));
                            } else if (element instanceof CompoundTag entry) {
                                String replacement = remapBlock(entry.getStringOr("Name", ""));
                                if (replacement != null) entry.putString("Name", replacement);
                            }
                        }
                    });
        }
    }

    private static String remapBlock(String name) {
        Identifier id = Identifier.tryParse(name);
        if (id == null || BuiltInRegistries.BLOCK.containsKey(id)) return null;
        String newName = ModdedFixDataFixer.BLOCKS.get(name);
        Identifier newId = Identifier.tryParse(newName);
        if(newId == null || !BuiltInRegistries.BLOCK.containsKey(newId)) return null;
        return newName;
    }

    public static void remapChunkBlockEntities(CompoundTag chunkData) {
        if (ModdedFixDataFixer.BLOCK_ENTITIES.isEmpty()) return;

        chunkData.getList("block_entities").ifPresent(list ->
                list.compoundStream().forEach(be -> {
                    String name = be.getStringOr("id", "");
                    String replacement = remapBlockEntity(name);
                    if(replacement != null) be.putString("id", replacement);
                })
        );
    }

    private static String remapBlockEntity(String name) {
        Identifier id = Identifier.tryParse(name);
        if (id == null || BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(id)) return null;
        String newName = ModdedFixDataFixer.BLOCK_ENTITIES.get(name);
        Identifier newId = Identifier.tryParse(newName);
        if(newId == null || !BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(newId)) return null;
        return newName;
    }

    public static void remapInventoryBlocks(CompoundTag playerData) {
        if (ModdedFixDataFixer.ITEMS.isEmpty()) return;

        playerData.getList("Inventory").ifPresent(list ->
                list.compoundStream().forEach(item -> {
                    String name = item.getStringOr("id", "");
                    String replacement = remapItem(name);
                    if (replacement != null) item.putString("id", replacement);
                })
        );
    }

    private static String remapItem(String name) {
        Identifier id = Identifier.tryParse(name);
        if (id == null || BuiltInRegistries.ITEM.containsKey(id)) return null;
        String newName = ModdedFixDataFixer.ITEMS.get(name);
        Identifier newId = Identifier.tryParse(newName);
        if(newId == null || !BuiltInRegistries.ITEM.containsKey(newId)) return null;
        return newName;
    }
}
