package app.masterwork.believable.magic;

import java.util.List;

/**
 * Describes a magic school and its menu sections.
 */
public interface MagicSchool {
    /**
     * @return stable id used for lookups
     */
    String id();

    /**
     * @return display name for UI
     */
    String name();

    /**
     * @return short description of the school
     */
    String description();

    /**
     * @return submenu tab keys shown under the school entry
     */
    List<String> subsectionTabKeys();
}
