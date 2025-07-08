//package net.electrisoma.resotech.data.providers;
//
//import net.electrisoma.visceralib.api.registration.builders.*;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.data.CachedOutput;
//import net.minecraft.data.PackOutput;
//import net.minecraft.network.chat.contents.TranslatableContents;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffect;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.item.*;
//import net.minecraft.world.item.enchantment.Enchantment;
//import net.minecraft.world.level.ItemLike;
//import net.minecraft.world.level.block.Block;
//import net.neoforged.neoforge.common.data.LanguageProvider;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
//public class VisceralLangProvider extends LanguageProvider {
//    private final VisceralLangProvider upsideDownLang;
//    private final Set<String> addedKeys = new HashSet<>();
//    private final String modid;
//
//    public VisceralLangProvider(PackOutput output, String modid) {
//        super(output, modid, "en_us");
//        this.modid = modid;
//        this.upsideDownLang = new VisceralLangProvider(output, modid, "en_ud");
//    }
//
//    public VisceralLangProvider(PackOutput output, String modid, String locale) {
//        super(output, modid, locale);
//        this.modid = modid;
//        this.upsideDownLang = null;
//    }
//
//    private <T> void addLangEntries(
//            List<? extends AbstractBuilder<T, ?, ?>> builders,
//            Function<T, ResourceLocation> idGetter,
//            String typePrefix) {
//        for (AbstractBuilder<T, ?, ?> builder : builders) {
//            builder.getRegisteredSupplier().ifPresent(supplier -> {
//                T object = supplier.get();
//                ResourceLocation id = idGetter.apply(object);
//                String langKey = typePrefix + id.getNamespace() + "." + id.getPath();
//                String langName = builder.getLangEntry().orElse(toEnglishName(id.getPath()));
//                add(langKey, langName);
//            });
//        }
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    protected void addTranslations() {
//        addLangEntries((List<AbstractBuilder<Block, ?, ?>>)(List<?>) BlockBuilder.getAllBuilders(),
//                BuiltInRegistries.BLOCK::getKey, "block.");
//
//        addLangEntries((List<AbstractBuilder<Item, ?, ?>>)(List<?>) ItemBuilder.getAllBuilders(),
//                BuiltInRegistries.ITEM::getKey, "item.");
//
//        for (TabBuilder tab : TabBuilder.getAllBuilders()) {
//            String key = "itemGroup." + modid + "." + tab.getName();
//            String fallbackName = toEnglishName(tab.getName());
//
//            tab.getTitle().ifPresentOrElse(
//                    component -> add(key, component.getString()),
//                    () -> add(key, fallbackName)
//            );
//        }
//    }
//
//    public void addBlock(Supplier<? extends Block> block) {
//        add(block.get(), autoName(block.get()));
//    }
//
//    public void addBlock(Supplier<? extends Block> block, @NotNull String name) {
//        add(block.get(), name);
//    }
//
//    public void addBlockWithTooltip(Supplier<? extends Block> block, String tooltip) {
//        addBlock(block);
//        addTooltip(block, tooltip);
//    }
//
//    public void addBlockWithTooltip(Supplier<? extends Block> block, String name, String tooltip) {
//        addBlock(block, name);
//        addTooltip(block, tooltip);
//    }
//
//    public void addItem(Supplier<? extends Item> item) {
//        add(item.get(), autoName(item.get()));
//    }
//
//    public void addItem(Supplier<? extends Item> item, @NotNull String name) {
//        add(item.get(), name);
//    }
//
//    public void addItemWithTooltip(Supplier<? extends Item> item, String tooltip) {
//        addItem(item);
//        addTooltip(item, tooltip);
//    }
//
//    public void addItemWithTooltip(Supplier<? extends Item> item, String name, String tooltip) {
//        addItem(item, name);
//        addTooltip(item, tooltip);
//    }
//
//    public void addTooltip(Supplier<? extends ItemLike> item, String tooltip) {
//        add(item.get().asItem().getDescriptionId() + ".desc", tooltip);
//    }
//
//    public void addTooltip(Supplier<? extends ItemLike> item, List<String> lines) {
//        for (int i = 0; i < lines.size(); i++) {
//            add(item.get().asItem().getDescriptionId() + ".desc." + i, lines.get(i));
//        }
//    }
//
//    public void addTab(Supplier<CreativeModeTab> tab, String name) {
//        var contents = tab.get().getDisplayName().getContents();
//        if (contents instanceof TranslatableContents lang) {
//            add(lang.getKey(), name);
//        } else {
//            System.err.println("Creative tab has non-translatable name: " + tab.get().getDisplayName());
//        }
//    }
//
//    public void addEntity(Supplier<? extends EntityType<?>> entity) {
//        add(entity.get(), autoName(entity.get()));
//    }
//
//    public void addEntity(Supplier<? extends EntityType<?>> entity, String name) {
//        add(entity.get(), name);
//    }
//
//    public void addEnchantment(Supplier<? extends Enchantment> ench) {
//        add(ench.get(), autoName(ench.get()));
//    }
//
//    public void addEnchantment(Supplier<? extends Enchantment> ench, String name) {
//        add(ench.get(), name);
//    }
//
//    public void addEffect(Supplier<? extends MobEffect> effect) {
//        add(effect.get(), autoName(effect.get()));
//    }
//
//    public void addEffect(Supplier<? extends MobEffect> effect, @NotNull String name) {
//        add(effect.get(), name);
//    }
//
//    public void add(Item item, @NotNull String name) {
//        add(item.getDescriptionId(), name);
//    }
//
//    public void add(Block block, @NotNull String name) {
//        add(block.getDescriptionId(), name);
//    }
//
//    public void add(EntityType<?> entity, @NotNull String name) {
//        add(entity.getDescriptionId(), name);
//    }
//
//    public void add(Enchantment ench, String name) {
//        ResourceLocation key = Registries.ENCHANTMENT.registry();
//        add("enchantment." + key.getNamespace() + "." + key.getPath(), name);
//    }
//
//    public void add(MobEffect effect, @NotNull String name) {
//        add(effect.getDescriptionId(), name);
//    }
//
//    public void add(ItemStack stack, @NotNull String name) {
//        add(stack.getDescriptionId(), name);
//    }
//
//    @Override
//    public void add(@NotNull String key, @NotNull String value) {
//        if (!addedKeys.add(key)) {
//            System.err.println("Duplicate lang key detected (ignored): " + key);
//            return;
//        }
//        super.add(key, value);
//        if (upsideDownLang != null) {
//            upsideDownLang.add(key, toUpsideDown(value));
//        }
//    }
//
//    private String autoName(Item item) {
//        return toEnglishName(BuiltInRegistries.ITEM.getKey(item).getPath());
//    }
//
//    private String autoName(Block block) {
//        return toEnglishName(BuiltInRegistries.BLOCK.getKey(block).getPath());
//    }
//
//    private String autoName(EntityType<?> entity) {
//        return toEnglishName(BuiltInRegistries.ENTITY_TYPE.getKey(entity).getPath());
//    }
//
//    private String autoName(Enchantment ench) {
//        ResourceLocation key = Registries.ENCHANTMENT.registry();
//        return toEnglishName(key.getPath());
//    }
//
//    private String autoName(MobEffect effect) {
//        return toEnglishName(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(effect)).getPath());
//    }
//
//    private static String toEnglishName(String id) {
//        return Arrays.stream(id.split("_"))
//                .map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1))
//                .collect(Collectors.joining(" "));
//    }
//
//    private String toUpsideDown(String str) {
//        StringBuilder result = new StringBuilder(str.length());
//        for (int i = str.length() - 1; i >= 0; i--) {
//            char c = str.charAt(i);
//            int index = NORMAL.indexOf(c);
//            result.append(index >= 0 ? UPSIDE.charAt(index) : c);
//        }
//        return result.toString();
//    }
//
//    private static final String NORMAL =
//            "abcdefghijklmnopqrstuvwxyz" +
//                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
//                    "0123456789" +
//                    ".,'?!_;/\\()[]{}<>";
//
//    private static final String UPSIDE =
//            "ɐqɔpǝɟƃɥᴉɾʞןɯuodbɹsʇnʌʍxʎz" +
//                    "∀ᙠƆᗡƎℲ⅁HΙſʞWɯNOԀӨᴚS⊥∩ΛMX⅄Z" +
//                    "0ƖᄅƐㄣϛ9ㄥ86" +
//                    "˙‘‚¿¡‾؛/\\)(][}{><";
//
//    @Override
//    public CompletableFuture<?> run(@NotNull CachedOutput output) {
//        var mainRun = super.run(output);
//        if (upsideDownLang != null) {
//            return CompletableFuture.allOf(mainRun, upsideDownLang.run(output));
//        }
//        return mainRun;
//    }
//
//    @Override
//    public String getName() {
//        return modid + " Lang";
//    }
//}
