package com.cgp.actilife;


public class MenuItem {
    int viewId;
    int iconId;
    String text;
    Class<?> activityClass; // Classe de l'activité à ouvrir

    public MenuItem(int viewId, int iconId, String text, Class<?> activityClass) {
        this.viewId = viewId;
        this.iconId = iconId;
        this.text = text;
        this.activityClass = activityClass;
    }
}

