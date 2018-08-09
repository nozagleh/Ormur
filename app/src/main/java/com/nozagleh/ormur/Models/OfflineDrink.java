package com.nozagleh.ormur.Models;

import android.arch.persistence.room.ColumnInfo;

public class OfflineDrink extends Drink {
    @ColumnInfo(name = "is_synced")
    private Boolean isSynced;
    @ColumnInfo(name = "is_offline")
    private Boolean isOffline;

    public OfflineDrink(Boolean isSynced, Boolean isOffline) {
        this.isSynced = isSynced;
        this.isOffline = isOffline;
    }

    public Boolean getSynced() {
        return isSynced;
    }

    public void setSynced(Boolean synced) {
        isSynced = synced;
    }

    public Boolean getOffline() {
        return isOffline;
    }

    public void setOffline(Boolean offline) {
        isOffline = offline;
    }
}
