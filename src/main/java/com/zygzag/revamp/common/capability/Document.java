package com.zygzag.revamp.common.capability;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class Document {
    public List<String> pageData;

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pageData.size());
        for (String page : pageData) buf.writeUtf(page);
    }

    public static Document decode(FriendlyByteBuf buf) {
        int n = buf.readInt();
        List<String> l = new ArrayList<>();
        for (int i = 0; i < n; i++) l.add(buf.readUtf());
        return new Document(l);
    }

    public Document() {
        this(new ArrayList<>(List.of("")));
    }

    public Document(List<String> pageData) {
        this.pageData = pageData;
        removeEmptyEndingPages();
    }

    public void removeEmptyEndingPages() {
        while (pageData.size() > 1 && pageData.get(pageData.size() - 1).equals("")) pageData.remove(pageData.size() - 1);
    }

    public void insertText(int pageId, int index, String text) {
        while (pageId >= pageData.size()) pageData.add("");
        String page = pageData.get(pageId);
        String newPage;
        if (index >= page.length()) {
            newPage = page + " ".repeat(index - page.length()) + text;
        } else {
            newPage = page.substring(0, index) + text + page.substring(index);
        }
        pageData.set(pageId, newPage);
        removeEmptyEndingPages();
    }

    public void deleteText(int pageId, int index, int length) {
        while (pageId >= pageData.size()) pageData.add("");
        if (index < 0) return;
        String page = pageData.get(pageId);
        String newPage;
        if (index >= page.length()) newPage = page;
        else {
            if (index + length >= page.length()) newPage = page.substring(0, index);
            else newPage = page.substring(0, index) + page.substring(index + length);
        }
        pageData.set(pageId, newPage);
        removeEmptyEndingPages();
    }

    public void setPage(int pageId, String text) {
        while (pageId >= pageData.size()) pageData.add("");
        pageData.set(pageId, text);
        removeEmptyEndingPages();
    }

    public ListTag serializeNBT() {
        ListTag list = new ListTag();
        for (String page : pageData) list.add(StringTag.valueOf(page));
        return list;
    }

    public static Document valueOf(ListTag list) {
        List<String> pages = new ArrayList<>();
        for (Tag elem : list) pages.add(elem.getAsString());
        return new Document(pages);
    }
}
