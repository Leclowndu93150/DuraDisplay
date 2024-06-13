package com.leclowndu93150.duradisplay.renderer;

import com.leclowndu93150.duradisplay.api.CustomDisplayItem;
import com.leclowndu93150.duradisplay.compat.BuiltinCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.leclowndu93150.duradisplay.Main.BUILTIN_COMPATS;

public record DuraDisplay() implements IItemDecorator {
    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.isBarVisible()) {
            // TODO: Port keybinds to 1.21
            //if (KeyBind.ForgeClient.modEnabled) {
                for (BuiltinCompat.CompatSupplier supplier : BUILTIN_COMPATS) {
                    BuiltinCompat compat = supplier.compat(stack);
                    if (compat != null && compat.active()) {
                        double percentage = compat.percentage();
                        renderText(guiGraphics, font, String.format("%.0f%%", percentage), xPosition, yPosition, compat.color());
                        return true;
                    }
                }
            //}

            renderItemBar(stack, xPosition, yPosition, guiGraphics);
        }
        return true;
    }

    private void renderText(GuiGraphics guiGraphics, Font font, String text, int xPosition, int yPosition, int color) {
        PoseStack poseStack = guiGraphics.pose();
        int stringWidth = font.width(text);
        int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
        int y = (yPosition * 2) + 22;
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.0D, 0.0D, 500.0D);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        font.drawInBatch(
                text,
                x,
                y,
                color,
                true,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                15728880,
                font.isBidirectional()
        );
        buffer.endBatch();
        poseStack.popPose();
    }

    private void renderItemBar(ItemStack stack, int xPosition, int yPosition, GuiGraphics guiGraphics) {
        int l = stack.getBarWidth();
        int i = stack.getBarColor();
        int j = xPosition + 2;
        int k = yPosition + 13;
        guiGraphics.fill(RenderType.guiOverlay(), j, k, j + 13, k + 2, -16777216);
        guiGraphics.fill(RenderType.guiOverlay(), j, k, j + l, k + 1, i | 0xFF000000);
    }
}