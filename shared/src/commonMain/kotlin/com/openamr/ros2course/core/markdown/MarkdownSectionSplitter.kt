package com.openamr.ros2course.core.markdown

/**
 * Every teaching module (1-13) in ROS2_COMPLETE_COURSE.md follows one fixed shape:
 *
 * ```
 * ## Module N — Title
 * ### Learning Objectives
 * - bullet
 * - bullet
 * ### N.1 First subsection
 * ... (numbered subsections, the bulk of the teaching content) ...
 * ### Activity N
 * > challenge text
 * ---
 * ```
 *
 * This splitter only relies on that fixed shape (three header markers), so it
 * stays correct even though we don't parse Markdown ourselves — actual
 * rendering of each resulting chunk is left to the Markdown renderer library.
 */
object MarkdownSectionSplitter {

    private val objectivesHeader = Regex("""^###\s+Learning Objectives\s*$""", RegexOption.MULTILINE)
    private val activityHeader = Regex("""^###\s+Activity\b.*$""", RegexOption.MULTILINE)

    /** Returns null if [raw] doesn't contain the expected markers (i.e. it's a reference page). */
    fun splitStructured(raw: String): Triple<String, String, String>? {
        val objectivesMatch = objectivesHeader.find(raw) ?: return null
        val activityMatch = activityHeader.find(raw) ?: return null
        if (activityMatch.range.first <= objectivesMatch.range.first) return null

        // Drop the leading "## Module N — Title" line so the overview tab starts
        // at "### Learning Objectives" itself (the renderer shows the title elsewhere).
        val overview = raw.substring(objectivesMatch.range.first, findNextHeaderAfter(raw, objectivesMatch.range.last))
        val content = raw.substring(findNextHeaderAfter(raw, objectivesMatch.range.last), activityMatch.range.first)
        val activity = raw.substring(activityMatch.range.first)
            .removeSuffix("\n")
            .trimEnd()
            .removeSuffix("---")
            .trimEnd()

        return Triple(overview.trim(), content.trim(), activity.trim())
    }

    /** Finds the start of the next "### " header after [fromIndex], or the string length if none. */
    private fun findNextHeaderAfter(raw: String, fromIndex: Int): Int {
        val nextHeader = Regex("""^###\s""", RegexOption.MULTILINE)
            .findAll(raw)
            .map { it.range.first }
            .firstOrNull { it > fromIndex }
        return nextHeader ?: raw.length
    }
}
