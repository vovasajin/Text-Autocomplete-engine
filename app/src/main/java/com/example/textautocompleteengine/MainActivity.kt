package com.example.textautocompleteengine

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.textautocompleteengine.data.AutocompleteRepository
import com.example.textautocompleteengine.data.SortMode
import com.example.textautocompleteengine.data.WordEntry
import com.example.textautocompleteengine.databinding.ActivityMainBinding
import com.example.textautocompleteengine.databinding.ItemEntryBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: AutocompleteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = AutocompleteRepository(this)

        setupSortModeSpinner()
        setupActions()
        refreshDictionary()
        updateSuggestions()
    }

    private fun setupSortModeSpinner() {
        val labels = SortMode.entries.map { it.displayName }
        binding.spinnerSort.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )
    }

    private fun setupActions() {
        binding.inputPrefix.doAfterTextChanged { updateSuggestions() }
        binding.inputLimit.doAfterTextChanged { updateSuggestions() }
        binding.spinnerSort.setOnItemSelectedListener(SimpleItemSelectedListener { updateSuggestions() })

        binding.buttonAddUpdate.setOnClickListener {
            val word = binding.inputWord.text.toString().trim()
            val frequency = binding.inputFrequency.text.toString().toIntOrNull()

            if (word.isBlank() || frequency == null || frequency < 0) {
                showStatus(getString(R.string.invalid_entry), isError = true)
                return@setOnClickListener
            }

            repository.addWord(word, frequency)
            clearEntryInputs()
            refreshDictionary()
            updateSuggestions()
            showStatus(getString(R.string.entry_saved))
        }

        binding.buttonExactSearch.setOnClickListener {
            val word = binding.inputExact.text.toString().trim()
            if (word.isBlank()) {
                showStatus(getString(R.string.empty_search), isError = true)
                return@setOnClickListener
            }

            val found = repository.containsWord(word)
            showStatus(
                if (found) getString(R.string.word_found) else getString(R.string.word_not_found),
                isError = !found
            )
        }

        binding.buttonRemove.setOnClickListener {
            val word = binding.inputRemove.text.toString().trim()
            if (word.isBlank()) {
                showStatus(getString(R.string.empty_remove), isError = true)
                return@setOnClickListener
            }

            val removed = repository.removeWord(word)
            if (removed) {
                binding.inputRemove.text?.clear()
                refreshDictionary()
                updateSuggestions()
                showStatus(getString(R.string.entry_removed))
            } else {
                showStatus(getString(R.string.word_not_found), isError = true)
            }
        }

        binding.buttonLoad.setOnClickListener {
            repository.reloadFromStorage()
            refreshDictionary()
            updateSuggestions()
            showStatus(getString(R.string.dictionary_loaded))
        }

        binding.buttonSave.setOnClickListener {
            repository.saveToStorage()
            showStatus(getString(R.string.dictionary_saved))
        }

        binding.buttonAbout.setOnClickListener { showAboutDialog() }
    }

    private fun updateSuggestions() {
        val prefix = binding.inputPrefix.text.toString().trim()
        val limit = binding.inputLimit.text.toString().toIntOrNull()?.coerceIn(1, 20) ?: 5
        val sortMode = SortMode.entries[binding.spinnerSort.selectedItemPosition]

        val suggestions = if (prefix.isBlank()) {
            emptyList()
        } else {
            repository.suggest(prefix, limit, sortMode)
        }

        renderEntries(
            container = binding.layoutSuggestions,
            entries = suggestions,
            emptyMessage = if (prefix.isBlank()) {
                getString(R.string.start_typing)
            } else {
                getString(R.string.no_suggestions)
            }
        )
    }

    private fun refreshDictionary() {
        renderEntries(
            container = binding.layoutDictionary,
            entries = repository.getDictionaryEntries(),
            emptyMessage = getString(R.string.dictionary_empty)
        )
    }

    private fun renderEntries(
        container: android.widget.LinearLayout,
        entries: List<WordEntry>,
        emptyMessage: String
    ) {
        container.removeAllViews()

        if (entries.isEmpty()) {
            val textView = TextView(this).apply {
                text = emptyMessage
                setTextColor(getColor(R.color.textMuted))
                textSize = 14f
            }
            container.addView(textView)
            return
        }

        val inflater = LayoutInflater.from(this)
        entries.forEach { entry ->
            val itemBinding = ItemEntryBinding.inflate(inflater, container, false)
            itemBinding.textWord.text = entry.word
            itemBinding.textFrequency.text = getString(R.string.frequency_value, entry.frequency)
            container.addView(itemBinding.root)
        }
    }

    private fun clearEntryInputs() {
        binding.inputWord.text?.clear()
        binding.inputFrequency.setText(getString(R.string.default_frequency))
    }

    private fun showStatus(message: String, isError: Boolean = false) {
        binding.textStatus.text = message
        binding.textStatus.setTextColor(
            getColor(
                if (isError) R.color.error else R.color.success
            )
        )
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(getString(R.string.about_message))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
