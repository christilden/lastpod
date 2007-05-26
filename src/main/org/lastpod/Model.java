package org.lastpod;

import java.util.List;

public interface Model {
    List getRecentlyPlayed();

    void setRecentlyPlayed(List recentlyPlayed);

    /**
     * A utility function to clear the recently played track list.
     * @param recentlyPlayed
     */
    void clearRecentlyPlayed();

    void selectAll();

    void unselectAll();

    /**
     * Parses the play counts and track information from the iPod.
     */
    void parsePlayCounts(UI userInterface);

    /**
     * Submits the tracks to Last.fm
     * @param userInterface  The application's user interface.
     * @return  A status message upon completion.
     */
    Object submitTracks(UI userInterface);
}
