package eb.mods.ebtelosultimate.gui;

import eb.mods.ebtelosultimate.command.ClassView.ClassView;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import java.util.*;

public class PlayerInventoryGui extends LightweightGuiDescription {
    public PlayerInventoryGui(List<ClassView.SlotData> slots) {
        WGridPanel root = new WGridPanel();
        root.setSize(100, 140);
        root.setInsets(Insets.ROOT_PANEL);
        root.validate(this);

        for (ClassView.SlotData slot : slots) {
            root.add(slot.slot, slot.x, slot.y);
        }

        setRootPanel(root);

    }




}
