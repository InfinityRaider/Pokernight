package com.infinityraider.pokernight.container.gui;

import com.infinityraider.pokernight.container.ContainerPokerGameRules;
import com.infinityraider.pokernight.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiContainerPokerGameRules extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/gamerules.png");

    /** buttons */
    private GuiButton buttonStartGame;
    private GuiButton buttonSetItem;
    private GuiButton buttonStackBuyIn;

    /** text inpts */
    private GuiTextField bigBlindInput;
    private GuiTextField itemStackFinder;

    private boolean setItem;

    private boolean allowBuyIn;

    public GuiContainerPokerGameRules(ContainerPokerGameRules container) {
        super(container);
    }

    public ContainerPokerGameRules getContainer() {
        return (ContainerPokerGameRules) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
        //buttons;
        this.buttonStartGame = new GuiButton(this.buttonList.size(), this.guiLeft + 124, this.guiTop + 52, 45, 10, "Start");
        this.buttonList.add(this.buttonStartGame);

        this.buttonSetItem = new GuiButton(this.buttonList.size(), this.guiLeft + 151, this.guiTop + 26, 18, 10, "Set");
        this.buttonList.add(this.buttonSetItem);

        this.buttonStackBuyIn = new GuiButtonStackAdding(this.buttonList.size(), this.guiLeft + 55, this.guiTop + 7, 10, 10, this );
        this.buttonList.add(this.buttonStackBuyIn);

        //input fields
        this.bigBlindInput = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 55, this.guiTop + 20, 20, 10);
        this.bigBlindInput.setValidator(PredicateNumeric.getInstance());

        this.itemStackFinder = new GuiTextField(1, this.fontRendererObj, this.guiLeft + 172, this.guiTop + 7, 54, 10);
        this.itemStackFinder.setVisible(false);

    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button == this.buttonSetItem) {
            this.setItem = true;
            this.itemStackFinder.setVisible(true);
        } else if(button == this.buttonStartGame) {
            this.getContainer().getTable().startGame();
        } else if(button == this.buttonStackBuyIn) {
            this.allowBuyIn = this.getContainer().getTable().trySetAllowStackAdding(!this.allowBuyIn);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.bigBlindInput.mouseClicked(mouseX, mouseY, mouseButton);
        this.itemStackFinder.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
            this.bigBlindInput.textboxKeyTyped(typedChar, keyCode);
        this.itemStackFinder.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.bigBlindInput.drawTextBox();
        this.itemStackFinder.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, 176, 150);
        if(this.setItem) {
            this.drawTexturedModalRect(x + 169, y, 176, 0, 63, 149);
        }
    }

    public static class GuiButtonStackAdding extends GuiButton {
        private final GuiContainerPokerGameRules gui;

        public GuiButtonStackAdding(int buttonId, int x, int y, int width, int height, GuiContainerPokerGameRules gui) {
            super(buttonId, x, y, width, height, "");
            this.gui = gui;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(GuiContainerPokerGameRules.TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                int dv = this.gui.allowBuyIn ? this.height : 0;
                if(this.enabled) {
                    this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    int i = this.getHoverState(this.hovered);
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, (i - 1) * this.width, 149 + dv, this.width, this.height);
                } else {
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 2 * this.width, 149 + dv, this.width, this.height);
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
