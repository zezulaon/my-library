package dev.zezula.books.ui.screen.detail

import androidx.annotation.StringRes
import dev.zezula.books.R

/**
 * Represents a tab in the book detail screen. Tab has its own [tabTitle] (associated via string resource).
 * The [isVisibleOutsideLibrary] flag indicates whether the tab should be visible when the book is not in
 * the user's library (some tabs are unusable when the book is not in the library).
 */
enum class DetailTab(@StringRes val tabTitle: Int, val isVisibleOutsideLibrary: Boolean) {
    Shelves(tabTitle = R.string.screen_detail_tab_shelves, isVisibleOutsideLibrary = false),
    Detail(tabTitle = R.string.screen_detail_tab_book, isVisibleOutsideLibrary = true),
    Reviews(tabTitle = R.string.screen_detail_tab_reviews, isVisibleOutsideLibrary = true),
    Suggestions(tabTitle = R.string.screen_detail_tab_suggestions, isVisibleOutsideLibrary = false),
    Notes(tabTitle = R.string.screen_detail_tab_notes, isVisibleOutsideLibrary = false),
}
