package com.wnp.passwdmanager;

import androidx.fragment.app.Fragment;

public interface FragmentNavigator {
    void navigateToFragment(Fragment frag, boolean backStack);
}
