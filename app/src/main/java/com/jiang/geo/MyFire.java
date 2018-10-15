package com.jiang.geo;

import android.support.annotation.NonNull;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.Query;

import java.util.Collection;

public class MyFire<T> extends FirebaseArray<T> {

    /**
     * Create a new FirebaseArray with a custom {@link SnapshotParser}.
     *
     * @param query  The Firebase location to watch for data changes. Can also be a slice of a
     *               location, using some combination of {@code limit()}, {@code startAt()}, and
     *               {@code endAt()}.
     * @param parser
     */
    public MyFire(@NonNull Query query, @NonNull SnapshotParser<T> parser) {
        super(query, parser);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {

        return true;
    }
}
