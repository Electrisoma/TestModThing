package net.electrisoma.testmod.data;

import net.electrisoma.testmod.registry.TestBlocks;

import net.electrisoma.visceralib.api.registration.builders.BlockBuilder;
import net.electrisoma.visceralib.api.registration.builders.ItemBuilder;
import net.electrisoma.visceralib.api.registration.builders.TabBuilder;
import net.electrisoma.visceralib.data.providers.VisceralLangProvider;
import net.electrisoma.visceralib.data.providers.VisceralLootProvider;
import net.electrisoma.visceralib.data.providers.VisceralibTagProvider;
import net.electrisoma.visceralib.data.providers.neoforge.VisceralBlockstateProviderImpl;
import net.electrisoma.visceralib.data.providers.neoforge.VisceralItemModelProviderImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static net.electrisoma.testmod.TestMod.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class TestData {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();

        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        VisceralLangProvider langProvider = new VisceralLangProvider(output, MOD_ID, "en_us");

        if (includeClient) {
            langGen(langProvider, gen);
            gen.addProvider(true, new VisceralBlockstateProviderImpl.NeoForgeBlockStates(MOD_ID, output, existingFileHelper));
            gen.addProvider(true, new VisceralItemModelProviderImpl(MOD_ID, output, existingFileHelper));
        }
        if (includeServer) {
            tagGen(event, output, lookupProvider, existingFileHelper);
            gen.addProvider(true, new VisceralLootProvider(output, lookupProvider, MOD_ID));

//            gen.addProvider(true, new ResoTechAdvancements(output, lookupProvider));
        }
    }

    private static void tagGen(GatherDataEvent event, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
        event.getGenerator().addProvider(true,
                VisceralibTagProvider.genBlockTags(output, lookupProvider, MOD_ID, helper));
        event.getGenerator().addProvider(true,
                VisceralibTagProvider.genItemTags(output, lookupProvider, MOD_ID, helper));

//      event.getGenerator().addProvider(true, new TagGen.FluidTagGen(output, lookupProvider, helper));
    }
    private static void langGen(VisceralLangProvider langProvider, DataGenerator gen) {
        langProvider.addBlock(TestBlocks.MACHINE_BLOCK.get(), "Machine Thing");
        BlockBuilder.provideLang(langProvider);
        ItemBuilder.provideLang(langProvider);
        TabBuilder.provideLang(langProvider);
        //FluidBuilder.provideLang(langProvider);

        gen.addProvider(true, langProvider);
    }
}
