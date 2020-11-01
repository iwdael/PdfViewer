package com.hacknife.pdfviewer.helper;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.loader.ThumbnailPool;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.loader.PatchTask;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;
import com.hacknife.pdfviewer.state.ScaleMode;

public class TaskFactory {
    public static final String TAG = TaskFactory.class.getSimpleName();
    private int pageSize;

    public static void createThumbnailTask(Configurator configurator) {
//        Size packSize = configurator.packSize();
//        int pageCount = configurator.core().pageCount();
//        ScaleMode mode = configurator.scaleMode();
//        int pageNumber = configurator.pageNumber();
//        int thumbnailCount = configurator.thumbnailCount();
//        int thumbnailPatchCount = configurator.thumbnailPatchCount();
//        PageCache pageCache = configurator.pageCache();
//        float thumbnailScale = configurator.thumbnailScale();
//        int page = pageNumber - (thumbnailCount / 3);
//        if (page < 0) page = 0;
//        if (thumbnailCount > pageCount) thumbnailCount = pageCount;
//        int pageSize = (mode == ScaleMode.WIDTH ? packSize.width : packSize.height);
//        int patchSize = (int) (pageSize * thumbnailScale);
//        for (; page < thumbnailCount; page++) {
//            PDF pdf = configurator.pageCache().getPage(page);
//            int patchY = 0;
//            int patchHeight = (int) ((mode == ScaleMode.WIDTH ? pdf.size.widthScaleTo(pageSize).height : pdf.size.heightScaleTo(pageSize).height) * thumbnailScale);
//            Logger.t(TAG).log("create page task|page  scale :%s , page des:%s", pdf.size.toScaleString(), new SizeF(pageSize * thumbnailScale, patchHeight));
//            while (patchY < patchHeight) {
//                for (int patchIndex = 0; patchIndex < thumbnailPatchCount; patchIndex++) {
//                    int patchX = patchIndex * patchSize;
//                    configurator.thumbnailPool().push(new PatchTask(new Patch(patchSize, patchSize), pageCache.getPage(page), thumbnailScale, patchX, patchY, pageSize, mode, configurator));
//                }
//                patchY += patchSize;
//            }
//        }
        int thumbnailCount = configurator.thumbnailCount();

        //缩略图起始位置
        int thumbnailClosure = (int) (configurator.pageNumber() + (thumbnailCount / 3f * 2f));
        if (thumbnailClosure > configurator.core().pageCount())
            thumbnailClosure = configurator.core().pageCount();
        //缩略图结束位置
        int thumbnailCommence = configurator.pageNumber() - (thumbnailCount / 3);
        if (thumbnailCommence <= 0) thumbnailCommence = 0;

        Size packSize = configurator.packSize();
        ScaleMode mode = configurator.scaleMode();
        PageCache pageCache = configurator.pageCache();
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        float thumbnailScale = configurator.thumbnailScale();
        //容器大小
        int pageSize = (mode == ScaleMode.WIDTH ? packSize.width : packSize.height);
        //缩略图碎片
        int thumbnailPatchCount = configurator.thumbnailPatchCount();
        //缩略图大小
        float thumbnailLandscapeSize = pageSize * thumbnailScale;
        int patchSize = (int) (thumbnailLandscapeSize / thumbnailPatchCount);

        for (int page = thumbnailCommence; page < thumbnailClosure; page++) {

            PDF pdf = pageCache.getPage(page);
            SizeF size = pdf.size;

            int thumbnailPortraitSize = (int) (size.height / size.width * thumbnailLandscapeSize);
            for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {

                for (int landscape = 0; landscape < thumbnailPatchCount; landscape++) {
                    thumbnailPool.push(new PatchTask(new Patch(patchSize, patchSize), pdf, thumbnailScale, landscape * patchSize, portraitSize,pageSize, mode, configurator));
                }

                portraitSize += patchSize;
            }
        }

    }
}
