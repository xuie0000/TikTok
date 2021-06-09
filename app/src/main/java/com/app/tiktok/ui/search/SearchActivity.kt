package com.app.tiktok.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.transition.Explode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.app.tiktok.R
import com.app.tiktok.databinding.ActivitySearchBinding
import timber.log.Timber

class SearchActivity : AppCompatActivity() {

  private lateinit var binding: ActivitySearchBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.enterTransition = Explode()

    binding = ActivitySearchBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
  }

  private lateinit var searchView: SearchView
  private lateinit var customSuggestionsAdapter: CustomSuggestionsAdapter

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.search_main, menu)
    searchView = menu.findItem(R.id.nav_search).actionView as SearchView

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

    customSuggestionsAdapter = CustomSuggestionsAdapter(this)
    customSuggestionsAdapter.setOnClickListener(object : CustomSuggestionsAdapter.OnClickListener {
      override fun click(view: View, position: Int, key: String): Boolean {

        // do something TODO
        return true
      }
    })

    searchView.suggestionsAdapter = customSuggestionsAdapter

    searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
      override fun onSuggestionSelect(position: Int): Boolean {
        return false
      }

      override fun onSuggestionClick(position: Int): Boolean {
        val info = with(searchView.suggestionsAdapter.cursor) {
          moveToPosition(position)
          getString(1)
        }
        Timber.d("onSuggestionClick info $info")
        searchView.setQuery(info, true)
        return true
      }
    })

    // Get the search close button image view
    val closeButton = searchView.findViewById(R.id.search_close_btn) as ImageView

    // Set on click listener
    closeButton.setOnClickListener {
      //Find EditText view
      val et = findViewById<EditText>(R.id.search_src_text)

      //Clear the text from EditText view
      et.setText("")

      //Clear query
      searchView.setQuery("", false)
      //Collapse the action view
      // searchView.onActionViewCollapsed()
      //Collapse the search widget
      // mSearchMenu.collapseActionView()

//            showSearchResult(false)
    }

    // Do not iconify the widget; expand it by default
    searchView.setIconifiedByDefault(false)
    val search = object : AsyncTask<String, Unit, Int>() {
      override fun doInBackground(vararg params: String?): Int {
        return -1
      }

      override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        searchView.suggestionsAdapter.swapCursor(null)
      }

    }

    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

      override fun onQueryTextSubmit(query: String): Boolean {
//                searchViewModel.saveSearchKey(query)
//                refresh()
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        Timber.d("newText $newText")
        search.cancel(true)
        search.execute(newText)
        return true
      }
    })
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}