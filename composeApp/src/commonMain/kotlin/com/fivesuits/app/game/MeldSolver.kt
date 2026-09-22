package com.fivesuits.app.game

/** Finds the minimum score across every disjoint combination of books and runs. */
object MeldSolver {
    fun analyze(cards: List<Card>, wildRank: Int): HandAnalysis {
        require(cards.size <= 14) { "Hands may contain at most fourteen cards." }
        require(wildRank in 3..13) { "Wild rank must be between three and king." }
        require(cards.map { it.id }.distinct().size == cards.size) { "Card IDs must be unique." }
        require(cards.all { it.isJoker || it.rank in 3..13 }) { "Card rank is outside this deck." }
        if (cards.isEmpty()) return HandAnalysis(emptyList(), emptyList(), 0)

        val size = 1 shl cards.size
        val rankMasks = IntArray(size)
        val suitMasks = IntArray(size)
        val naturalCounts = IntArray(size)
        val cardCounts = IntArray(size)
        val legalTypes = IntArray(size)
        val candidates = Array(cards.size) { mutableListOf<Int>() }

        // A subset is a legal book/run solely from its natural ranks, suits, and wild count.
        for (mask in 1 until size) {
            val bit = mask and -mask
            val index = bit.countTrailingZeroBits()
            val previous = mask xor bit
            val card = cards[index]
            val natural = !card.isWild(wildRank)
            rankMasks[mask] = rankMasks[previous] or if (natural) (1 shl (card.rank - 3)) else 0
            suitMasks[mask] = suitMasks[previous] or if (natural) (1 shl card.suit!!.ordinal) else 0
            naturalCounts[mask] = naturalCounts[previous] + if (natural) 1 else 0
            cardCounts[mask] = cardCounts[previous] + 1
            if (cardCounts[mask] < 3) continue

            val ranks = rankMasks[mask]
            val distinctRanks = ranks.countOneBits()
            val isBook = distinctRanks <= 1
            val span = if (ranks == 0) 0 else 32 - ranks.countLeadingZeroBits() - ranks.countTrailingZeroBits()
            val isRun = cardCounts[mask] <= 11 &&
                suitMasks[mask].countOneBits() <= 1 &&
                naturalCounts[mask] == distinctRanks && span <= cardCounts[mask]
            legalTypes[mask] = when {
                isBook -> 1
                isRun -> 2
                else -> 0
            }
            if (legalTypes[mask] != 0) {
                var remaining = mask
                while (remaining != 0) {
                    val member = remaining and -remaining
                    candidates[member.countTrailingZeroBits()].add(mask)
                    remaining = remaining xor member
                }
            }
        }

        val scores = IntArray(size) { -1 }
        val choices = IntArray(size)
        scores[0] = 0

        fun solve(available: Int): Int {
            if (scores[available] >= 0) return scores[available]
            val first = available and -available
            val index = first.countTrailingZeroBits()
            var best = cards[index].points(wildRank) + solve(available xor first)
            var chosen = 0
            for (meld in candidates[index]) {
                if (meld and available != meld) continue
                val score = solve(available xor meld)
                if (score < best) {
                    best = score
                    chosen = meld
                    if (best == 0) break
                }
            }
            scores[available] = best
            choices[available] = chosen
            return best
        }

        val score = solve(size - 1)
        var remaining = size - 1
        val melds = mutableListOf<Meld>()
        val deadwood = mutableListOf<Card>()
        while (remaining != 0) {
            val choice = choices[remaining]
            if (choice == 0) {
                val first = remaining and -remaining
                deadwood.add(cards[first.countTrailingZeroBits()])
                remaining = remaining xor first
            } else {
                melds.add(Meld(
                    cards = cards.filterIndexed { index, _ -> choice and (1 shl index) != 0 },
                    type = if (legalTypes[choice] == 1) MeldType.BOOK else MeldType.RUN,
                ))
                remaining = remaining xor choice
            }
        }
        return HandAnalysis(melds, deadwood, score)
    }
}
