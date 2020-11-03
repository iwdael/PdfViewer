package com.hacknife.pdfviewer.helper;

import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.state.PatchState;

import java.util.ArrayList;
import java.util.List;

public class PreparePatchQueue extends ArrayList<Patch> {
    public static final String TAG = PreparePatchQueue.class.getSimpleName();

    @Override
    public Patch remove(int index) {
        Patch patch = super.remove(index);
        patch.state = PatchState.PREPARED;
        return patch;
    }


    public void removeInto(Patch p, List<Patch> prepare) {
        int index = indexOf(p);
        if (index != -1) {
            Patch patch = remove(index);
            prepare.add(patch);
        } else {
            Logger.t(TAG).log("未找到：" + p.toString());
        }


    }
}
