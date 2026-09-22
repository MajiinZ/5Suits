package com.fivesuits.app.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MeldSolverTest {
    @Test
    fun wildCardsFillGapsAndRunEnds() {
        val cards = listOf(card(1, 4), card(2, 6), Card(3, null, 0), Card(4, Suit.STARS, 13))
        val result = MeldSolver.analyze(cards, 13)
        assertEquals(0, result.score)
        assertTrue(result.canGoOut)
        assertEquals(MeldType.RUN, result.melds.single().type)
    }

    @Test
    fun identicalCopiesCanMakeBooksButNotDuplicateRanksInRuns() {
        val book = listOf(card(1, 7), card(2, 7), card(3, 7, Suit.HEARTS))
        assertEquals(0, MeldSolver.analyze(book, 3).score)
        val invalidRun = listOf(card(1, 7), card(2, 7), card(3, 8))
        assertEquals(22, MeldSolver.analyze(invalidRun, 3).score)
    }

    @Test
    fun overlappingCandidatesAreOptimizedGlobally() {
        val cards = listOf(
            card(1, 4), card(2, 5), card(3, 6),
            card(4, 6, Suit.DIAMONDS), card(5, 6, Suit.HEARTS), card(6, 6, Suit.SPADES),
        )
        val result = MeldSolver.analyze(cards, 13)
        assertEquals(0, result.score)
        assertEquals(setOf(MeldType.BOOK, MeldType.RUN), result.melds.map { it.type }.toSet())
        assertEquals(cards.map { it.id }.sorted(), result.melds.flatMap { it.cards }.map { it.id }.sorted())
    }

    @Test
    fun allWildBooksAreLegalAndUnmatchedCardsHaveCorrectValues() {
        assertEquals(0, MeldSolver.analyze(listOf(Card(1, null, 0), card(2, 8), card(3, 8, Suit.STARS)), 8).score)
        assertEquals(70, MeldSolver.analyze(listOf(Card(1, null, 0), card(2, 8)), 8).score)
        assertEquals(25, MeldSolver.analyze(listOf(card(1, 12), card(2, 13)), 8).score)
        assertFalse(MeldSolver.analyze(listOf(card(1, 12)), 8).canGoOut)
    }

    @Test
    fun maximumHandWithManyWildsIsHandledExactly() {
        val cards = (0..5).map { Card(it, null, 0) } +
            (6..13).map { Card(it, Suit.entries[it % 5], 13) }
        val result = MeldSolver.analyze(cards, 13)
        assertEquals(0, result.score)
        assertTrue(result.canGoOut)
        assertEquals(14, result.melds.sumOf { it.cards.size })
    }

    @Test
    fun resultMatchesIndependentExhaustivePartitionOracle() {
        val random = Random(425)
        repeat(80) {
            val cards = List(7) { index ->
                if (random.nextInt(8) == 0) Card(index, null, 0)
                else card(index, random.nextInt(3, 10), Suit.entries[random.nextInt(3)])
            }
            val wildRank = random.nextInt(3, 10)
            val result = MeldSolver.analyze(cards, wildRank)
            assertEquals(oracleScore(cards, wildRank), result.score, "Hand: $cards; wild: $wildRank")
            assertEquals(cards.map { it.id }.sorted(), (result.deadwood + result.melds.flatMap { it.cards }).map { it.id }.sorted())
            assertEquals(result.deadwood.sumOf { it.points(wildRank) }, result.score)
        }
    }

    private fun oracleScore(cards: List<Card>, wildRank: Int): Int {
        if (cards.isEmpty()) return 0
        var best = cards.first().points(wildRank) + oracleScore(cards.drop(1), wildRank)
        // Every partition either leaves the first card unmatched or places it in one meld.
        for (mask in 1 until (1 shl cards.size)) {
            if (mask and 1 == 0 || mask.countOneBits() < 3) continue
            val group = cards.filterIndexed { index, _ -> mask and (1 shl index) != 0 }
            if (oracleMeld(group, wildRank)) {
                best = minOf(best, oracleScore(cards.filterIndexed { index, _ -> mask and (1 shl index) == 0 }, wildRank))
            }
        }
        return best
    }

    private fun oracleMeld(cards: List<Card>, wildRank: Int): Boolean {
        val natural = cards.filterNot { it.isWild(wildRank) }
        if (natural.map { it.rank }.distinct().size <= 1) return true
        if (cards.size > 11 || natural.map { it.suit }.distinct().size > 1) return false
        if (natural.map { it.rank }.distinct().size != natural.size) return false
        // Try every possible legal start/end interval; wild cards occupy the empty slots.
        return (3..(14 - cards.size)).any { start ->
            natural.all { it.rank in start until start + cards.size }
        }
    }

    private fun card(id: Int, rank: Int, suit: Suit = Suit.CLUBS) = Card(id, suit, rank)
}
