//package net.electrisoma.resotech.data.providers;
//
//import net.electrisoma.resotech.ResoTech;
//import net.electrisoma.visceralib.api.registration.builders.BlockBuilder;
//import net.electrisoma.visceralib.api.registration.builders.ItemBuilder;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.data.PackOutput;
//import net.minecraft.data.tags.TagsProvider;
//import net.minecraft.tags.TagKey;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.neoforged.neoforge.common.data.ExistingFileHelper;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.concurrent.CompletableFuture;
//
//@SuppressWarnings("deprecation")
//public class VisceralibTagProvider {
//    public static class BlockTags extends TagsProvider<Block> {
//        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
//            super(output, Registries.BLOCK, lookupProvider, ResoTech.MOD_ID, fileHelper);
//        }
//
//        @Override
//        protected void addTags(@NotNull HolderLookup.Provider provider) {
//            var blockLookup = provider.lookupOrThrow(Registries.BLOCK);
//
//            for (BlockBuilder<?, ?> builder : BlockBuilder.getAllBuilders()) {
//                builder.getRegisteredSupplier().ifPresent(supplier -> {
//                    Block block = supplier.get();
//
//                    blockLookup.get(block.builtInRegistryHolder().key()).ifPresent(holder -> {
//                        for (TagKey<Block> tag : builder.getTags()) {
//                            tag(tag).add(holder.key());
//                        }
//                    });
//                });
//            }
//        }
//
//        @Override
//        public String getName() {
//            return ResoTech.NAME + " Block Tags";
//        }
//    }
//
//    public static class ItemTags extends TagsProvider<Item> {
//        public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
//            super(output, Registries.ITEM, lookupProvider, ResoTech.MOD_ID, fileHelper);
//        }
//
//        @Override
//        protected void addTags(@NotNull HolderLookup.Provider provider) {
//            var itemLookup = provider.lookupOrThrow(Registries.ITEM);
//
//            for (ItemBuilder<?, ?> builder : ItemBuilder.getAllBuilders()) {
//                builder.getRegisteredSupplier().ifPresent(supplier -> {
//                    Item item = supplier.get();
//
//                    itemLookup.get(item.builtInRegistryHolder().key()).ifPresent(holder -> {
//                        for (TagKey<Item> tag : builder.getTags()) {
//                            tag(tag).add(holder.key());
//                        }
//                    });
//                });
//            }
//        }
//
//        @Override
//        public String getName() {
//            return ResoTech.NAME + " Item Tags";
//        }
//    }
//
////    public static class FluidTags extends TagsProvider<Fluid> {
////        public FluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
////            super(output, Registries.FLUID, lookupProvider, ResoTech.MOD_ID, fileHelper);
////        }
////
////        @Override
////        protected void addTags(@NotNull HolderLookup.Provider provider) {
////            var fluidLookup = provider.lookupOrThrow(Registries.FLUID);
////
////            for (FluidBuilder builder : FluidBuilder.getAllBuilders()) {
////                builder.getStillSupplier().flatMap(stillSupplier ->
////                        builder.getFlowingSupplier().map(flowingSupplier -> new Fluid[] {
////                                stillSupplier.get(),
////                                flowingSupplier.get()
////                        })
////                ).ifPresent(fluids -> {
////                    var stillKey = fluids[0].builtInRegistryHolder().key();
////                    var flowingKey = fluids[1].builtInRegistryHolder().key();
////
////                    fluidLookup.get(stillKey).ifPresent(stillHolder ->
////                            fluidLookup.get(flowingKey).ifPresent(flowingHolder -> {
////                                for (TagKey<Fluid> tag : builder.getFluidTags()) {
////                                    tag(tag).add(stillHolder.key()).add(flowingHolder.key());
////                                }
////                            })
////                    );
////                });
////            }
////        }
////
////        @Override
////        public String getName() {
////            return ResoTech.NAME + " Fluid Tags";
////        }
////    }
//}
