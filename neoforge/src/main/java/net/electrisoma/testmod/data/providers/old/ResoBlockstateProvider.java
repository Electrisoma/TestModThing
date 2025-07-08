//package net.electrisoma.resotech.data.providers;
//
//import net.electrisoma.resotech.ResoTech;
//import net.electrisoma.visceralib.api.registration.VisceralDeferredRegister;
//import net.electrisoma.visceralib.api.registration.VisceralRegistries;
//import net.electrisoma.visceralib.api.registration.VisceralRegistrySupplier;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.data.PackOutput;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.world.level.block.Block;
//import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
//import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
//import net.neoforged.neoforge.common.data.ExistingFileHelper;
//
//public class ResoBlockstateProvider extends BlockStateProvider {
//    public ResoBlockstateProvider(PackOutput output, ExistingFileHelper fileHelper) {
//        super(output, ResoTech.MOD_ID, fileHelper);
//    }
//
//    @Override
//    protected void registerStatesAndModels() {
//        VisceralRegistries.getAllForMod(ResoTech.MOD_ID).forEach(deferred -> {
//            if (deferred.getRegistryKey().equals(Registries.BLOCK)) {
//                VisceralDeferredRegister<Block> blockRegister = (VisceralDeferredRegister<Block>) deferred;
//
//                for (VisceralRegistrySupplier<Block> blockSupplier : blockRegister.getEntries().values()) {
//                    Block block = blockSupplier.get();
//                    ResourceKey<Block> key = blockSupplier.getKey();
//                    String blockName = key.location().getPath();
//
//                    if (isFluidBlock(block)) {
//                        BlockModelBuilder model = models().getBuilder(blockName)
//                                .texture("particle", modLoc("fluid/" + blockName + "_still"));
//                        simpleBlock(block, model);
//                    } else {
//                        simpleBlock(block);
//                    }
//                }
//            }
//        });
//    }
//
//    private boolean isFluidBlock(Block block) {
//        String className = block.getClass().getSimpleName();
//        return className.contains("LiquidBlock") || className.contains("Fluid");
//    }
//
//    @Override
//    public String getName() {
//        return ResoTech.NAME + " Blockstates";
//    }
//}
