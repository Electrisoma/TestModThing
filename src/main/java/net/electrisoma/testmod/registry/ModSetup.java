package net.electrisoma.testmod.registry;

public class ModSetup {
    public static void register() {
        TestTabs.init();
        TestBlocks.init();
        TestItems.init();
        //ResoTechFluids.init();
        TestTags.init();
    }
}

