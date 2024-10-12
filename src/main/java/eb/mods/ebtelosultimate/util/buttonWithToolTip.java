package eb.mods.ebtelosultimate.util;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class buttonWithToolTip extends WButton {

    @FunctionalInterface
    public interface CallableWithBtn<buttonWithToolTip, InputResult> {
        InputResult call(buttonWithToolTip btn) throws Exception;
    }

    private final String toolTip;
    private final CallableWithBtn<buttonWithToolTip, InputResult> onClickFunction;
    public buttonWithToolTip(String toolTip, CallableWithBtn<buttonWithToolTip, InputResult>  onClick) {
        super(Text.literal(""));
        this.toolTip = toolTip;
        this.onClickFunction = onClick;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
        // description of the button
        if (this.isHovered()) {
            ;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void addTooltip(TooltipBuilder builder) {
        builder.add(Text.literal(this.toolTip));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public InputResult onClick(int x, int y, int button) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        if (this.isEnabled() && isWithinBounds(x, y)) {
            try {
                InputResult result = this.onClickFunction.call(this);
                if (result == InputResult.PROCESSED) {
                    MinecraftClient.getInstance().setScreen(null); // close the screen
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return InputResult.IGNORED;
    }

    public String getToolTip() {
        return this.toolTip;
    }
}
