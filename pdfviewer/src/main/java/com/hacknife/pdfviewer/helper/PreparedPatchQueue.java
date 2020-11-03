package com.hacknife.pdfviewer.helper;

import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.state.PatchState;

import java.util.ArrayList;
import java.util.List;

public class PreparedPatchQueue extends ArrayList<Patch> {

    @Override
    public Patch remove(int index) {
        Patch patch = super.remove(index);
        patch.state = PatchState.PREPARE;
        return patch;
    }


    public Patch removeInto(int index, List<Patch> prepare) {
        Patch patch = remove(index);
        prepare.add(patch);
        return patch;
    }
}
