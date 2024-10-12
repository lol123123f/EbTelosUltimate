package eb.mods.ebtelosultimate.gui;

import eb.mods.ebtelosultimate.util.WItemSlotWithImage;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.client.gui.DrawContext;

public class PlayerInventoryScreen extends CottonClientScreen{
    final LightweightGuiDescription description;

    public PlayerInventoryScreen(LightweightGuiDescription description) {
        super(description);
        this.description = description;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);
        //assert class_310.method_1551().field_1724 != null;
        WGridPanel root = (WGridPanel) description.getRootPanel();
        root.streamChildren().forEach(widget -> {
            if (widget instanceof WItemSlotWithImage) {
                WItemSlotWithImage slot = (WItemSlotWithImage) widget;
                slot.checkHover(context, mouseX, mouseY);
            }
        });
    }
}
