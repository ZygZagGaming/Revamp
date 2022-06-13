package com.zygzag.revamp.client.render.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class EnderBookEditScreen extends Screen {
    private static final int TEXT_WIDTH = 114;
    private static final int TEXT_HEIGHT = 128;
    private static final int IMAGE_WIDTH = 192;
    private static final int IMAGE_HEIGHT = 192;
    private static final Component EDIT_TITLE_LABEL = new TranslatableComponent("book.editTitle");
    private static final Component FINALIZE_WARNING_LABEL = new TranslatableComponent("book.finalizeWarning");
    private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
    private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
    private final Player owner;
    private final ItemStack book;
    private boolean isModified;
    private int frameTick;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private String title = "";
    private final TextFieldHelper pageEdit = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, (p_98179_) -> {
        return p_98179_.length() < 1024 && font.wordWrapHeight(p_98179_, 114) <= 128;
    });
    private final TextFieldHelper titleEdit = new TextFieldHelper(() -> {
        return title;
    }, (p_98175_) -> {
        title = p_98175_;
    }, this::getClipboard, this::setClipboard, (p_98170_) -> {
        return p_98170_.length() < 16;
    });
    private long lastClickTime;
    private int lastIndex = -1;
    private PageButton forwardButton;
    private PageButton backButton;
    private Button doneButton;
    private final InteractionHand hand;
    @Nullable
    private EnderBookEditScreen.DisplayCache displayCache = EnderBookEditScreen.DisplayCache.EMPTY;
    private Component pageMsg = TextComponent.EMPTY;
    private final Component ownerText;

    private final int openId;

    public EnderBookEditScreen(Player player, ItemStack stack, InteractionHand hand, int documentId) {
        super(NarratorChatListener.NO_TITLE);
        owner = player;
        book = stack;
        this.hand = hand;
        this.openId = documentId;
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            BookViewScreen.loadPages(compoundtag, pages::add);
        }

        if (pages.isEmpty()) {
            pages.add("");
        }

        ownerText = (new TranslatableComponent("book.byAuthor", player.getName())).withStyle(ChatFormatting.DARK_GRAY);
    }

    private void setClipboard(String p_98148_) {
        if (minecraft != null) {
            TextFieldHelper.setClipboardContents(minecraft, p_98148_);
        }
    }

    private String getClipboard() {
        return minecraft != null ? TextFieldHelper.getClipboardContents(minecraft) : "";
    }

    private int getNumPages() {
        return pages.size();
    }

    public void tick() {
        super.tick();
        ++frameTick;
    }

    protected void init() {
        clearDisplayCache();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        doneButton = addRenderableWidget(new Button(width + 2, 196, 98, 20, CommonComponents.GUI_DONE, (button) -> {
            minecraft.setScreen(null);
            saveChanges(false);
        }));
        int i = (width - 192) / 2;
        int j = 2;
        forwardButton = addRenderableWidget(new PageButton(i + 116, 159, true, (button) -> {
            pageForward();
        }, true));
        backButton = addRenderableWidget(new PageButton(i + 43, 159, false, (button) -> {
            pageBack();
        }, true));
        updateButtonVisibility();
    }

    private void pageBack() {
        if (currentPage > 0) {
            --currentPage;
        }

        updateButtonVisibility();
        clearDisplayCacheAfterPageChange();
    }

    private void pageForward() {
        if (currentPage < getNumPages() - 1) {
            ++currentPage;
        } else {
            appendPageToBook();
            if (currentPage < getNumPages() - 1) {
                ++currentPage;
            }
        }

        updateButtonVisibility();
        clearDisplayCacheAfterPageChange();
    }

    public void removed() {
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void updateButtonVisibility() {
        backButton.visible = currentPage > 0;
        forwardButton.visible = true;
        doneButton.visible = true;
    }

    private void eraseEmptyTrailingPages() {
        ListIterator<String> listiterator = pages.listIterator(pages.size());

        while (listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
            listiterator.remove();
        }

    }

    private void saveChanges(boolean writeonly) {
        if (isModified) {
            eraseEmptyTrailingPages();
            updateLocalCopy(writeonly);
            int i = hand == InteractionHand.MAIN_HAND ? owner.getInventory().selected : 40;
            minecraft.getConnection().send(new ServerboundEditBookPacket(i, pages, writeonly ? Optional.of(title.trim()) : Optional.empty()));
        }
    }

    private void updateLocalCopy(boolean writeonly) {
        ListTag listtag = new ListTag();
        pages.stream().map(StringTag::valueOf).forEach(listtag::add);
        if (!pages.isEmpty()) {
            book.addTagElement("pages", listtag);
        }

        if (writeonly) {
            book.addTagElement("author", StringTag.valueOf(owner.getGameProfile().getName()));
            book.addTagElement("title", StringTag.valueOf(title.trim()));
        }

    }

    private void appendPageToBook() {
        if (getNumPages() < 100) {
            pages.add("");
            isModified = true;
        }
    }

    public boolean keyPressed(int key, int a, int b) {
        if (super.keyPressed(key, a, b)) {
            return true;
        } else {
            boolean flag = bookKeyPressed(key, a, b);
            if (flag) {
                clearDisplayCache();
            }
            return flag;
        }
    }

    public boolean charTyped(char character, int a) {
        if (super.charTyped(character, a)) {
            return true;
        } else if (SharedConstants.isAllowedChatCharacter(character)) {
            pageEdit.insertText(Character.toString(character));
            clearDisplayCache();
            return true;
        } else {
            return false;
        }
    }

    private boolean bookKeyPressed(int key, int a, int b) {
        if (Screen.isSelectAll(key)) {
            pageEdit.selectAll();
            return true;
        } else if (Screen.isCopy(key)) {
            pageEdit.copy();
            return true;
        } else if (Screen.isPaste(key)) {
            pageEdit.paste();
            return true;
        } else if (Screen.isCut(key)) {
            pageEdit.cut();
            return true;
        } else {
            switch (key) {
                case 257:
                case 335: // enter and return?
                    pageEdit.insertText("\n");
                    return true;
                case 259: // backspace
                    pageEdit.removeCharsFromCursor(-1);
                    return true;
                case 261: // delete
                    pageEdit.removeCharsFromCursor(1);
                    return true;
                case 262: // right arrow
                    pageEdit.moveByChars(1, Screen.hasShiftDown());
                    return true;
                case 263: // left arrow
                    pageEdit.moveByChars(-1, Screen.hasShiftDown());
                    return true;
                case 264:
                    keyDown();
                    return true;
                case 265:
                    keyUp();
                    return true;
                case 266:
                    backButton.onPress();
                    return true;
                case 267:
                    forwardButton.onPress();
                    return true;
                case 268:
                    keyHome();
                    return true;
                case 269:
                    keyEnd();
                    return true;
                default:
                    return false;
            }
        }
    }

    private void keyUp() {
        changeLine(-1);
    }

    private void keyDown() {
        changeLine(1);
    }

    private void changeLine(int line) {
        int i = pageEdit.getCursorPos();
        int j = getDisplayCache().changeLine(i, line);
        pageEdit.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyHome() {
        int i = pageEdit.getCursorPos();
        int j = getDisplayCache().findLineStart(i);
        pageEdit.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyEnd() {
        EnderBookEditScreen.DisplayCache bookeditscreen$displaycache = getDisplayCache();
        int i = pageEdit.getCursorPos();
        int j = bookeditscreen$displaycache.findLineEnd(i);
        pageEdit.setCursorPos(j, Screen.hasShiftDown());
    }

    private String getCurrentPageText() {
        return currentPage >= 0 && currentPage < pages.size() ? pages.get(currentPage) : "";
    }

    private void setCurrentPageText(String text) {
        if (currentPage >= 0 && currentPage < pages.size()) {
            pages.set(currentPage, text);
            isModified = true;
            clearDisplayCache();
        }

    }

    public void render(PoseStack stack, int a, int b, float c) {
        renderBackground(stack);
        setFocused(null);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
        int i = (width - 192) / 2;
        int j = 2;
        blit(stack, i, 2, 0, 0, 192, 192);
        int j1 = font.width(pageMsg);
        font.draw(stack, pageMsg, (float)(i - j1 + 192 - 44), 18.0F, 0);
        EnderBookEditScreen.DisplayCache bookeditscreen$displaycache = getDisplayCache();

        for (EnderBookEditScreen.LineInfo bookeditscreen$lineinfo : bookeditscreen$displaycache.lines) {
            font.draw(stack, bookeditscreen$lineinfo.asComponent, (float)bookeditscreen$lineinfo.x, (float)bookeditscreen$lineinfo.y, -16777216);
        }

        renderHighlight(bookeditscreen$displaycache.selection);
        renderCursor(stack, bookeditscreen$displaycache.cursor, bookeditscreen$displaycache.cursorAtEnd);

        super.render(stack, a, b, c);
    }

    private void renderCursor(PoseStack stack, EnderBookEditScreen.Pos2i pos, boolean flag) {
        if (frameTick / 6 % 2 == 0) {
            pos = convertLocalToScreen(pos);
            if (!flag) {
                GuiComponent.fill(stack, pos.x, pos.y - 1, pos.x + 1, pos.y + 9, -16777216);
            } else {
                font.draw(stack, "_", (float)pos.x, (float)pos.y, 0);
            }
        }

    }

    private void renderHighlight(Rect2i[] highlight) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (Rect2i rect2i : highlight) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            bufferbuilder.vertex(i, l, 0.0D).endVertex();
            bufferbuilder.vertex(k, l, 0.0D).endVertex();
            bufferbuilder.vertex(k, j, 0.0D).endVertex();
            bufferbuilder.vertex(i, j, 0.0D).endVertex();
        }

        tesselator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private EnderBookEditScreen.Pos2i convertScreenToLocal(EnderBookEditScreen.Pos2i pos) {
        return new EnderBookEditScreen.Pos2i(pos.x - (width - 192) / 2 - 36, pos.y - 32);
    }

    private EnderBookEditScreen.Pos2i convertLocalToScreen(EnderBookEditScreen.Pos2i pos) {
        return new EnderBookEditScreen.Pos2i(pos.x + (width - 192) / 2 + 36, pos.y + 32);
    }

    public boolean mouseClicked(double x, double y, int n) {
        if (!super.mouseClicked(x, y, n)) {
            if (n == 0) {
                long i = Util.getMillis();
                DisplayCache bookeditscreen$displaycache = getDisplayCache();
                int j = bookeditscreen$displaycache.getIndexAtPosition(font, convertScreenToLocal(new Pos2i((int) x, (int) y)));
                if (j >= 0) {
                    if (j == lastIndex && i - lastClickTime < 250L) {
                        if (!pageEdit.isSelecting()) {
                            selectWord(j);
                        } else {
                            pageEdit.selectAll();
                        }
                    } else {
                        pageEdit.setCursorPos(j, Screen.hasShiftDown());
                    }

                    clearDisplayCache();
                }

                lastIndex = j;
                lastClickTime = i;
            }

        }
        return true;
    }

    private void selectWord(int i) {
        String s = getCurrentPageText();
        pageEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, i, false), StringSplitter.getWordPosition(s, 1, i, false));
    }

    public boolean mouseDragged(double x, double y, int n, double a, double b) {
        if (!super.mouseDragged(x, y, n, a, b)) {
            if (n == 0) {
                DisplayCache bookeditscreen$displaycache = getDisplayCache();
                int i = bookeditscreen$displaycache.getIndexAtPosition(font, convertScreenToLocal(new Pos2i((int) x, (int) y)));
                pageEdit.setCursorPos(i, true);
                clearDisplayCache();
            }

        }
        return true;
    }

    private EnderBookEditScreen.DisplayCache getDisplayCache() {
        if (displayCache == null) {
            displayCache = rebuildDisplayCache();
            pageMsg = new TranslatableComponent("book.pageIndicator", currentPage + 1, getNumPages());
        }

        return displayCache;
    }

    private void clearDisplayCache() {
        displayCache = null;
    }

    private void clearDisplayCacheAfterPageChange() {
        pageEdit.setCursorToEnd();
        clearDisplayCache();
    }

    private EnderBookEditScreen.DisplayCache rebuildDisplayCache() {
        String s = getCurrentPageText();
        if (s.isEmpty()) {
            return EnderBookEditScreen.DisplayCache.EMPTY;
        } else {
            int i = pageEdit.getCursorPos();
            int j = pageEdit.getSelectionPos();
            IntList intlist = new IntArrayList();
            List<EnderBookEditScreen.LineInfo> list = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            StringSplitter stringsplitter = font.getSplitter();
            stringsplitter.splitLines(s, 114, Style.EMPTY, true, (style, n, n2) -> {
                int k3 = mutableint.getAndIncrement();
                String s2 = s.substring(n, n2);
                mutableboolean.setValue(s2.endsWith("\n"));
                String s3 = StringUtils.stripEnd(s2, " \n");
                int l3 = k3 * 9;
                EnderBookEditScreen.Pos2i bookeditscreen$pos2i1 = convertLocalToScreen(new EnderBookEditScreen.Pos2i(0, l3));
                intlist.add(n);
                list.add(new EnderBookEditScreen.LineInfo(style, s3, bookeditscreen$pos2i1.x, bookeditscreen$pos2i1.y));
            });
            int[] aint = intlist.toIntArray();
            boolean flag = i == s.length();
            EnderBookEditScreen.Pos2i bookeditscreen$pos2i;
            if (flag && mutableboolean.isTrue()) {
                bookeditscreen$pos2i = new EnderBookEditScreen.Pos2i(0, list.size() * 9);
            } else {
                int k = findLineFromPos(aint, i);
                int l = font.width(s.substring(aint[k], i));
                bookeditscreen$pos2i = new EnderBookEditScreen.Pos2i(l, k * 9);
            }

            List<Rect2i> list1 = Lists.newArrayList();
            if (i != j) {
                int l2 = Math.min(i, j);
                int i1 = Math.max(i, j);
                int j1 = findLineFromPos(aint, l2);
                int k1 = findLineFromPos(aint, i1);
                if (j1 == k1) {
                    int l1 = j1 * 9;
                    int i2 = aint[j1];
                    list1.add(createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
                } else {
                    int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
                    list1.add(createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));

                    for(int j3 = j1 + 1; j3 < k1; ++j3) {
                        int j2 = j3 * 9;
                        String s1 = s.substring(aint[j3], aint[j3 + 1]);
                        int k2 = (int)stringsplitter.stringWidth(s1);
                        list1.add(createSelection(new EnderBookEditScreen.Pos2i(0, j2), new EnderBookEditScreen.Pos2i(k2, j2 + 9)));
                    }

                    list1.add(createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
                }
            }

            return new EnderBookEditScreen.DisplayCache(s, bookeditscreen$pos2i, flag, aint, list.toArray(new EnderBookEditScreen.LineInfo[0]), list1.toArray(new Rect2i[0]));
        }
    }

    public int getDocumentId() {
        return openId;
    }

    public void setPageText(int pageId, String text) {
        pages.set(pageId, text);
        isModified = true;
        clearDisplayCache();
    }

    public void insertPageText(int pageId, int index, String text) {
        String page = pages.get(pageId);
        String newPage;
        if (index >= page.length()) {
            newPage = page + " ".repeat(index - page.length() + 1) + text;
        } else {
            newPage = page.substring(0, index) + text + page.substring(index);
        }
        pages.set(index, newPage);
        isModified = true;
        clearDisplayCache();
    }

    public void removePageText(int pageId, int index, int length) {
        String page = pages.get(pageId);
        String newPage;
        if (index >= page.length()) newPage = page;
        else {
            if (index + length >= page.length()) newPage = page.substring(0, index);
            else newPage = page.substring(0, index) + page.substring(index + length);
        }
        pages.set(pageId, newPage);
        isModified = true;
        clearDisplayCache();
    }

    public void makeSureThatPageExists(int index) {
        if (index >= pages.size()) {
            while (index >= pages.size()) pages.add("");
            isModified = true;
            clearDisplayCache();
        }
    }

    static int findLineFromPos(int[] p_98150_, int p_98151_) {
        int i = Arrays.binarySearch(p_98150_, p_98151_);
        return i < 0 ? -(i + 2) : i;
    }

    private Rect2i createPartialLineSelection(String p_98120_, StringSplitter p_98121_, int p_98122_, int p_98123_, int p_98124_, int p_98125_) {
        String s = p_98120_.substring(p_98125_, p_98122_);
        String s1 = p_98120_.substring(p_98125_, p_98123_);
        EnderBookEditScreen.Pos2i bookeditscreen$pos2i = new EnderBookEditScreen.Pos2i((int)p_98121_.stringWidth(s), p_98124_);
        EnderBookEditScreen.Pos2i bookeditscreen$pos2i1 = new EnderBookEditScreen.Pos2i((int)p_98121_.stringWidth(s1), p_98124_ + 9);
        return createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
    }

    private Rect2i createSelection(EnderBookEditScreen.Pos2i p_98117_, EnderBookEditScreen.Pos2i p_98118_) {
        EnderBookEditScreen.Pos2i bookeditscreen$pos2i = convertLocalToScreen(p_98117_);
        EnderBookEditScreen.Pos2i bookeditscreen$pos2i1 = convertLocalToScreen(p_98118_);
        int i = Math.min(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int j = Math.max(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int k = Math.min(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        int l = Math.max(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        return new Rect2i(i, k, j - i, l - k);
    }


    @OnlyIn(Dist.CLIENT)
    static class DisplayCache {
        static final EnderBookEditScreen.DisplayCache EMPTY = new EnderBookEditScreen.DisplayCache("", new EnderBookEditScreen.Pos2i(0, 0), true, new int[]{0}, new EnderBookEditScreen.LineInfo[]{new EnderBookEditScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        final EnderBookEditScreen.Pos2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final EnderBookEditScreen.LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String p_98201_, EnderBookEditScreen.Pos2i p_98202_, boolean p_98203_, int[] p_98204_, EnderBookEditScreen.LineInfo[] p_98205_, Rect2i[] p_98206_) {
            fullText = p_98201_;
            cursor = p_98202_;
            cursorAtEnd = p_98203_;
            lineStarts = p_98204_;
            lines = p_98205_;
            selection = p_98206_;
        }

        public int getIndexAtPosition(Font p_98214_, EnderBookEditScreen.Pos2i p_98215_) {
            int i = p_98215_.y / 9;
            if (i < 0) {
                return 0;
            } else if (i >= lines.length) {
                return fullText.length();
            } else {
                EnderBookEditScreen.LineInfo bookeditscreen$lineinfo = lines[i];
                return lineStarts[i] + p_98214_.getSplitter().plainIndexAtWidth(bookeditscreen$lineinfo.contents, p_98215_.x, bookeditscreen$lineinfo.style);
            }
        }

        public int changeLine(int p_98211_, int p_98212_) {
            int i = EnderBookEditScreen.findLineFromPos(lineStarts, p_98211_);
            int j = i + p_98212_;
            int k;
            if (0 <= j && j < lineStarts.length) {
                int l = p_98211_ - lineStarts[i];
                int i1 = lines[j].contents.length();
                k = lineStarts[j] + Math.min(l, i1);
            } else {
                k = p_98211_;
            }

            return k;
        }

        public int findLineStart(int p_98209_) {
            int i = EnderBookEditScreen.findLineFromPos(lineStarts, p_98209_);
            return lineStarts[i];
        }

        public int findLineEnd(int p_98219_) {
            int i = EnderBookEditScreen.findLineFromPos(lineStarts, p_98219_);
            return lineStarts[i] + lines[i].contents.length();
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class LineInfo {
        final Style style;
        final String contents;
        final Component asComponent;
        final int x;
        final int y;

        public LineInfo(Style p_98232_, String p_98233_, int p_98234_, int p_98235_) {
            style = p_98232_;
            contents = p_98233_;
            x = p_98234_;
            y = p_98235_;
            asComponent = (new TextComponent(p_98233_)).setStyle(p_98232_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Pos2i {
        public final int x;
        public final int y;

        Pos2i(int p_98249_, int p_98250_) {
            x = p_98249_;
            y = p_98250_;
        }
    }
}
