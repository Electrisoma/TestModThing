package net.electrisoma.testmod.registry.items.util.renderers;

import java.util.function.Supplier;

public class SimpleBEWLR extends AbstractItemBEWLR {
    private final Supplier<? extends AbstractItemRenderer> supplier;

    public SimpleBEWLR(Supplier<? extends AbstractItemRenderer> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected AbstractItemRenderer createRenderer() {
        return supplier.get();
    }
}
