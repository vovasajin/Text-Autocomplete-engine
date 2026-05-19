package com.example.textautocompleteengine.data

import java.util.Comparator

class Trie {

    private val root = TrieNode()

    fun add(word: String, frequency: Int = 1) {
        var current = root
        for (character in word) {
            current = current.children.getOrPut(character) { TrieNode() }
        }
        current.isTerminal = true
        current.frequency = frequency
    }

    fun contains(word: String): Boolean {
        val node = findNode(word)
        return node?.isTerminal == true
    }

    fun remove(word: String): Boolean {
        if (!contains(word)) {
            return false
        }

        removeRecursive(root, word, 0)
        return true
    }

    fun suggest(prefix: String, limit: Int, sortMode: SortMode): List<WordEntry> {
        val startNode = findNode(prefix) ?: return emptyList()
        val entries = mutableListOf<WordEntry>()
        collectEntries(startNode, prefix, entries)

        val comparator: Comparator<WordEntry> = when (sortMode) {
            SortMode.FREQUENCY -> compareByDescending<WordEntry> { entry -> entry.frequency }
                .thenBy { entry -> entry.word.lowercase() }

            SortMode.ALPHABETICAL -> compareBy<WordEntry> { entry -> entry.word.lowercase() }
                .thenByDescending { entry -> entry.frequency }
        }

        return entries.sortedWith(comparator).take(limit)
    }

    fun toList(sortMode: SortMode): List<WordEntry> =
        suggest(prefix = "", limit = Int.MAX_VALUE, sortMode = sortMode)

    private fun collectEntries(node: TrieNode, currentWord: String, entries: MutableList<WordEntry>) {
        if (node.isTerminal) {
            entries += WordEntry(currentWord, node.frequency)
        }

        node.children.forEach { entry ->
            val character = entry.key
            val childNode = entry.value
            collectEntries(childNode, currentWord + character, entries)
        }
    }

    private fun findNode(value: String): TrieNode? {
        var current = root
        for (character in value) {
            current = current.children[character] ?: return null
        }
        return current
    }

    private fun removeRecursive(node: TrieNode, word: String, depth: Int): Boolean {
        if (depth == word.length) {
            node.isTerminal = false
            node.frequency = 0
            return node.children.isEmpty()
        }

        val character = word[depth]
        val childNode = node.children[character] ?: return false
        val shouldDeleteChild = removeRecursive(childNode, word, depth + 1)

        if (shouldDeleteChild) {
            node.children.remove(character)
        }

        return node.children.isEmpty() && !node.isTerminal
    }
}
