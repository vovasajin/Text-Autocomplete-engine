package com.example.textautocompleteengine.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AutocompleteRepository(
    private val context: Context
) {

    private val storageFile = File(context.filesDir, FILE_NAME)
    private var trie = Trie()

    init {
        reloadFromStorage()
    }

    fun addWord(word: String, frequency: Int) {
        trie.add(word, frequency)
        saveToStorage()
    }

    fun removeWord(word: String): Boolean {
        val removed = trie.remove(word)
        if (removed) {
            saveToStorage()
        }
        return removed
    }

    fun containsWord(word: String): Boolean = trie.contains(word)

    fun suggest(prefix: String, limit: Int, sortMode: SortMode): List<WordEntry> =
        trie.suggest(prefix, limit, sortMode)

    fun getDictionaryEntries(): List<WordEntry> =
        trie.toList(SortMode.ALPHABETICAL)

    fun saveToStorage() {
        val jsonArray = JSONArray()
        trie.toList(SortMode.ALPHABETICAL).forEach { entry ->
            jsonArray.put(
                JSONObject()
                    .put("word", entry.word)
                    .put("frequency", entry.frequency)
            )
        }
        storageFile.writeText(jsonArray.toString(2))
    }

    fun reloadFromStorage() {
        trie = Trie()

        val items = if (storageFile.exists()) {
            parseEntries(storageFile.readText())
        } else {
            defaultEntries()
        }

        items.forEach { trie.add(it.word, it.frequency) }

        if (!storageFile.exists()) {
            saveToStorage()
        }
    }

    private fun parseEntries(rawJson: String): List<WordEntry> {
        return runCatching {
            val jsonArray = JSONArray(rawJson)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(index)
                    add(
                        WordEntry(
                            word = item.getString("word"),
                            frequency = item.getInt("frequency")
                        )
                    )
                }
            }
        }.getOrElse {
            defaultEntries()
        }
    }

    private fun defaultEntries(): List<WordEntry> = listOf(
        WordEntry("algorithm", 18),
        WordEntry("application", 20),
        WordEntry("array", 14),
        WordEntry("autocomplete", 35),
        WordEntry("binary tree", 12),
        WordEntry("data structure", 26),
        WordEntry("debug", 10),
        WordEntry("dictionary", 15),
        WordEntry("graph", 11),
        WordEntry("javascript", 17),
        WordEntry("kotlin", 19),
        WordEntry("mobile keyboard", 16),
        WordEntry("queue", 9),
        WordEntry("search engine", 23),
        WordEntry("stack", 8),
        WordEntry("string", 13),
        WordEntry("trie", 30)
    )

    companion object {
        private const val FILE_NAME = "dictionary.json"
    }
}
