package dev.zezula.books.ui.screen.detail

import androidx.annotation.StringRes
import dev.zezula.books.R

enum class DetailTab(val tabIndex: Int, @StringRes val tabName: Int) {
    Shelves(0, R.string.screen_detail_tab_shelves),
    Detail(1, R.string.screen_detail_tab_book),
    Reviews(2, R.string.screen_detail_tab_reviews)
}