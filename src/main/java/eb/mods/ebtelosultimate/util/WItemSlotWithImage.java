package eb.mods.ebtelosultimate.util;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class WItemSlotWithImage extends WItemSlot {
    private final TextureHelper textureHelper;
    private TextureHelper textureHelperDescription = null;
    public WItemSlotWithImage(String imageUrl) {
        super(new Inventory() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack getStack(int slot) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot) {
                return null;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {

            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }

            @Override
            public void clear() {

            }
    }, 0, 1, 1, false);
        this.textureHelper = new TextureHelper(imageUrl);
    }

    public WItemSlotWithImage(String imageUrl, String imageUrlDescription) {
        super(new Inventory() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack getStack(int slot) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot) {
                return null;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {

            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }

            @Override
            public void clear() {

            }
        }, 0, 1, 1, false);
        this.textureHelper = new TextureHelper(imageUrl);
        this.textureHelperDescription = new TextureHelper(imageUrlDescription);
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
        textureHelper.bindTexture();
        context.drawTexture(textureHelper.getTexture(), x + 1, y + 1, 0, 0, 16, 16, 16, 16);
    }

    public void checkHover(DrawContext context, int mouseX, int mouseY){
        if (this.isHovered()) {
            if (textureHelperDescription != null) {
                textureHelperDescription.bindTexture();
                context.drawTexture(textureHelperDescription.getTexture(), mouseX + 5, mouseY - 24, 0, 0, textureHelperDescription.Width, textureHelperDescription.Height, textureHelperDescription.Width, textureHelperDescription.Height);
            }
        }
    }
}
