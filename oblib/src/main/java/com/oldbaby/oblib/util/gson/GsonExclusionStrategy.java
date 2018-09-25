package com.oldbaby.oblib.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(GsonExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }
}