package io.github.agdavydov81.data;

import android.graphics.Bitmap;

public class LabelInfo {
    public LabelInfo(int nameId, int iconId) {
        this.nameId = nameId;
        this.iconId = iconId;
    }

    public int nameId;
    public int iconId;

//        set(value) {
//            field = value
//            iconBitmap = null
//        }

    Bitmap iconBitmap = null;
}
