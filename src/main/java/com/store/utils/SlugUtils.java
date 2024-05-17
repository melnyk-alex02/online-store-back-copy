package com.store.utils;

import com.github.slugify.Slugify;

public class SlugUtils {
    private static final Slugify slugify = new Slugify();

    public static String generateSlug(String title) {
        return slugify.slugify(title);
    }
}