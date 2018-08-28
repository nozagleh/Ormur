package com.nozagleh.ormur;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nozagleh.ormur.Models.OfflineDrinkDao;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OfflineDbTest {
    private OfflineDrinkDao mDrinkDao;
    private LocalDb mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, LocalDb.class).build();
    }
}
