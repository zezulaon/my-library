package dev.zezula.books.unit

import dev.zezula.books.util.splitToAuthors
import dev.zezula.books.util.toAuthorNameId
import dev.zezula.books.util.toSortingAuthor
import dev.zezula.books.util.toSortingTitle
import junit.framework.TestCase.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `toSortingTitle() removes leading article The`() {
        val title = "The Lord of the Rings"
        val expected = "Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes leading article A`() {
        val title = "A Lord of the Rings"
        val expected = "Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes leading article An`() {
        val title = "An Lord of the Rings"
        val expected = "Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes leading articles with spaces`() {
        val title = " The Lord of the Rings"
        val expected = "Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes leading articles with spaces and punctuation`() {
        val title = " The Lord of the Rings: The Fellowship of the Ring"
        val expected = "Lord of the Rings: The Fellowship of the Ring"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes just one article`() {
        val title = "The The Lord of the Rings"
        val expected = "The Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingTitle() removes leading articles with different letter case`() {
        val title = "the Lord of the Rings"
        val expected = "Lord of the Rings"
        assertEquals(expected, title.toSortingTitle())
    }

    @Test
    fun `toSortingAuthor(), given single name, should return same name`() {
        val input = "Smith"
        val result = input.toSortingAuthor()
        assertEquals("Smith", result)
    }

    @Test
    fun `toSortingAuthor(), given full name, should return last name`() {
        val input = "John Smith"
        val result = input.toSortingAuthor()
        assertEquals("Smith", result)
    }

    @Test
    fun `toSortingAuthor(), given authors separated by comma and space, should return last name of the first author`() {
        val input = "John Smith, Mike Doe, Jane Doe"
        val result = input.toSortingAuthor()
        assertEquals("Smith", result)
    }

    @Test
    fun `toSortingAuthor(), given authors separated by comma, should return last name of the first author`() {
        val input = "John Smith,Mike Doe,Jane Doe"
        val result = input.toSortingAuthor()
        assertEquals("Smith", result)
    }

    @Test
    fun `toSortingAuthor(), given author with multiple spaces, should trim and return last name`() {
        val input = "   John   Smith   "
        val result = input.toSortingAuthor()
        assertEquals("Smith", result)
    }

    @Test
    fun `toSortingAuthor(), given empty author, should return empty string`() {
        val input = ""
        val result = input.toSortingAuthor()
        assertEquals("", result)
    }

    @Test
    fun `splitToAuthors(), given single author, should return list with single author`() {
        val input = "John Smith"
        val result = input.splitToAuthors()
        assertEquals(listOf("John Smith"), result)
    }

    @Test
    fun `splitToAuthors(), given multiple authors separated by comma, should return list of authors`() {
        val input = "John Smith, Mike Doe, Jane Doe"
        val result = input.splitToAuthors()
        assertEquals(listOf("John Smith", "Mike Doe", "Jane Doe"), result)
    }

    @Test
    fun `splitToAuthors(), given authors with extra spaces, should trim and return list of authors`() {
        val input = "   John Smith   ,   Mike Doe   , Jane Doe   "
        val result = input.splitToAuthors()
        assertEquals(listOf("John Smith", "Mike Doe", "Jane Doe"), result)
    }

    @Test
    fun `splitToAuthors(), given empty author, should return empty list`() {
        val input = ""
        val result = input.splitToAuthors()
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `splitToAuthors(), given multiple commas with no authors in between, should return list without empty authors`() {
        val input = "John Smith,, ,Mike Doe, ,Jane Doe"
        val result = input.splitToAuthors()
        assertEquals(listOf("John Smith", "Mike Doe", "Jane Doe"), result)
    }

    @Test
    fun `splitToAuthors(), given authors separated by multiple types of delimiters, should return list of authors`() {
        val input = "John Smith, Mike Doe,Jane Doe"
        val result = input.splitToAuthors()
        assertEquals(listOf("John Smith", "Mike Doe", "Jane Doe"), result)
    }

    @Test
    fun `toAuthorNameId(), given author with spaces, should return ID without spaces`() {
        val input = "J. R. R. Tolkien"
        val result = input.toAuthorNameId()
        assertEquals("j.r.r.tolkien", result)
    }

    @Test
    fun `toAuthorNameId(), given author with mixed casing, should return ID in lowercase`() {
        val input = "j.R.R. tolKIEn"
        val result = input.toAuthorNameId()
        assertEquals("j.r.r.tolkien", result)
    }

    @Test
    fun `toAuthorNameId(), given author without spaces, should return ID in lowercase`() {
        val input = "JRRTolkien"
        val result = input.toAuthorNameId()
        assertEquals("jrrtolkien", result)
    }

    @Test
    fun `toAuthorNameId(), given empty string, should return empty ID`() {
        val input = ""
        val result = input.toAuthorNameId()
        assertEquals("", result)
    }

    @Test
    fun `toAuthorNameId(), given author with leading and trailing spaces, should trim and return valid ID`() {
        val input = "  J. R. R. Tolkien  "
        val result = input.toAuthorNameId()
        assertEquals("j.r.r.tolkien", result)
    }

    @Test
    fun `toAuthorNameId(), given two different formats of same author name, should return same ID`() {
        val name1 = "J. R. R. Tolkien "
        val name2 = "j.r.r. tolkien"

        val id1 = name1.toAuthorNameId()
        val id2 = name2.toAuthorNameId()

        assertEquals(id1, id2)
    }
}
