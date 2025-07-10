package net.electrisoma.testmod.registry.items.tau_cannon;

import net.electrisoma.testmod.registry.items.util.renderers.AbstractItemBEWLR;
import net.electrisoma.testmod.registry.items.util.renderers.AbstractItemRenderer;

public class TauCannonBEWLR extends AbstractItemBEWLR {
    @Override
    protected AbstractItemRenderer createRenderer() {
        return new TauCannonItemRenderer();
    }
}
