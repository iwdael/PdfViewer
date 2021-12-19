package com.iwdael.pdfviewer.core.pdfium.util;

import com.iwdael.pdfviewer.core.model.Bookmark;
import com.iwdael.pdfviewer.core.model.Link;
import com.iwdael.pdfviewer.core.model.Meta;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;

public class InformationConverter {
    public static Meta metaConvert(PdfDocument.Meta m) {
        Meta meta = new Meta();
        meta.setAuthor(m.getAuthor());
        meta.setCreationDate(m.getCreationDate());
        meta.setCreator(m.getCreator());
        meta.setKeywords(m.getKeywords());
        meta.setModDate(m.getModDate());
        meta.setProducer(m.getProducer());
        meta.setSubject(m.getSubject());
        meta.setTitle(m.getTitle());
        return meta;
    }

    public static List<Bookmark> bookmarkConvert(List<PdfDocument.Bookmark> contents) {
        List<Bookmark> bookmarks = new ArrayList<>(contents.size());
        for (PdfDocument.Bookmark content : contents) {
            Bookmark bookmark = new Bookmark();
            bookmark.setChildren(bookmarkConvert(content.getChildren()));
            bookmark.setmNativePtr(content.getPageIdx());
            bookmark.setPageIdx(content.getPageIdx());
            bookmark.setTitle(content.getTitle());
            bookmarks.add(bookmark);
        }
        return bookmarks;
    }

    public static List<Link> linkConvert(List<PdfDocument.Link> contents) {
        List<Link> links = new ArrayList<>(contents.size());
        for (PdfDocument.Link content : contents) {
            Link link = new Link();
            link.setBounds(content.getBounds());
            link.setDestPageIdx(content.getDestPageIdx());
            link.setUri(content.getUri());
            links.add(link);
        }
        return links;
    }
}
