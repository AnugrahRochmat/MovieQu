package anugrah.rochmat.moviequ.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DateUtilTest {

    @Test
    fun `getYearFromReleaseDate returns correct year for valid date`() {
        val result = DateUtil.getYearFromReleaseDate("2025-02-24")
        assertThat(result).contains("2025")
    }

    @Test
    fun `getYearFromReleaseDate returns correct year for invalid date`() {
        val result = DateUtil.getYearFromReleaseDate("invalid-date")
        assertThat(result).contains("")
    }
}