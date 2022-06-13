package com.zygzag.revamp.common.capability;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record ServerLevelEnderBookHandler(Level world, List<Document> documents) {
    public void addText(int documentId, int pageId, int index, String text) {
        documents.get(documentId).insertText(pageId, index, text);
    }

    public void deleteText(int documentId, int pageId, int index, int length) {
        documents.get(documentId).deleteText(pageId, index, length);
    }

    public void setPageText(int documentId, int pageId, String text) {
        documents.get(documentId).setPage(pageId, text);
    }

    public static class Document {
        public List<String> pageData = new ArrayList<>();
        public void insertText(int pageId, int index, String text) {
            String page = pageData.get(pageId);
            String newPage;
            if (index >= page.length()) {
                newPage = page + " ".repeat(index - page.length() + 1) + text;
            } else {
                newPage = page.substring(0, index) + text + page.substring(index);
            }
            pageData.set(index, newPage);
        }

        public void deleteText(int pageId, int index, int length) {
            String page = pageData.get(pageId);
            String newPage;
            if (index >= page.length()) newPage = page;
            else {
                if (index + length >= page.length()) newPage = page.substring(0, index);
                else newPage = page.substring(0, index) + page.substring(index + length);
            }
            pageData.set(pageId, newPage);
        }

        public void setPage(int pageId, String text) {
            pageData.set(pageId, text);
        }
    }
}
