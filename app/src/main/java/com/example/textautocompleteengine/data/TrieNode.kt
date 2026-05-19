package com.example.textautocompleteengine.data

data class TrieNode(
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    var isTerminal: Boolean = false,
    var frequency: Int = 0
)
