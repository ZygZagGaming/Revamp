package com.zygzag.revamp.common.capability;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ServerLevelEnderBookHandler {
    public Level world;
    public List<Document> documents;

    public ServerLevelEnderBookHandler(Level world, List<Document> documents) {
        this.world = world;
        this.documents = documents;
    }

    public void addText(int documentId, int pageId, int index, String text) {
        documents.get(documentId).insertText(pageId, index, text);
    }

    public void deleteText(int documentId, int pageId, int index, int length) {
        documents.get(documentId).deleteText(pageId, index, length);
    }

    public void setPageText(int documentId, int pageId, String text) {
        documents.get(documentId).setPage(pageId, text);
    }
}
