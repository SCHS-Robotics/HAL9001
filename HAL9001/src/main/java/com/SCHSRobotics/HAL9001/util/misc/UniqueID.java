package com.SCHSRobotics.HAL9001.util.misc;

import org.jetbrains.annotations.NotNull;

public class UniqueID {
    private static int globalIdentifier = 0;
    private int localIdentifier;
    private String id;
    public UniqueID(String id) {
        this.id = id;
        localIdentifier = globalIdentifier;
        globalIdentifier++;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  UniqueID) {
            UniqueID otherId = (UniqueID) obj;
            return otherId.localIdentifier == this.localIdentifier;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return localIdentifier;
    }

    @NotNull
    @Override
    public String toString() {
        return id;
    }
}
