package net.electrisoma.testmod.client.neoforge;

import net.electrisoma.testmod.TestMod;
//import net.electrisoma.resotech.api.registration.FluidBuilder;
import net.electrisoma.testmod.client.TestModClient;
//import net.electrisoma.resotech.registry.ResoTechFluids;
//
//import net.minecraft.resources.ResourceLocation;

import net.electrisoma.testmod.registry.TestItems;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class TestModClientImpl {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        TestModClient.init();
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new TauCannonItemRenderer(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels()
            );

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, TestItems.TAU_CANNON.get());
    }
}

//    @SubscribeEvent
//    private static void initializeClient(RegisterClientExtensionsEvent event) {
//        FluidBuilder.getAllAttributes().forEach((attributes ->
//                event.registerFluidType(new IClientFluidTypeExtensions() {
//            @Override
//            public @NotNull ResourceLocation getStillTexture() {
//                return attributes.getSourceTexture();
//            }
//
//            @Override
//            public @NotNull ResourceLocation getFlowingTexture() {
//                return attributes.getFlowingTexture();
//            }
//        }, attributes.getFlowingFluid().getFluidType())));
