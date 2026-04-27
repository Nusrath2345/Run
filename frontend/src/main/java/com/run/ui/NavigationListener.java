package com.run.ui;

// panels call navigateTo() to switch screens
// implemented by MainFrame so panels dont need a direct reference to it
public interface NavigationListener {
    void navigateTo(String panelName);
}
