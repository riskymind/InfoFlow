package com.asterisk.infoflow.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.asterisk.infoflow.R
import com.asterisk.infoflow.databinding.ActivityMainBinding
import com.asterisk.infoflow.ui.breaking_news.BreakingNewsFragment
import com.asterisk.infoflow.ui.saved_news.SaveNewsFragment
import com.asterisk.infoflow.ui.search_news.SearchNewsFragment
import com.asterisk.infoflow.utils.Constants.BREAKING_NEWS_FRAGMENT_TAG
import com.asterisk.infoflow.utils.Constants.KEY_SELECTED_INDEX
import com.asterisk.infoflow.utils.Constants.SAVED_NEWS_FRAGMENT_TAG
import com.asterisk.infoflow.utils.Constants.SEARCH_NEWS_FRAGMENT_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var breakingNewsFragment: BreakingNewsFragment
    private lateinit var searchNewsFragment: SearchNewsFragment
    private lateinit var saveNewsFragment: SaveNewsFragment

    private val fragments: Array<Fragment>
        get() = arrayOf(
            breakingNewsFragment,
            searchNewsFragment,
            saveNewsFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }

        transaction.commit()

        title = when(selectedFragment) {
            is BreakingNewsFragment -> getString(R.string.title_breaking_news)
            is SearchNewsFragment -> getString(R.string.title_search_news)
            is SaveNewsFragment -> getString(R.string.title_save)
            else -> ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            breakingNewsFragment = BreakingNewsFragment()
            searchNewsFragment = SearchNewsFragment()
            saveNewsFragment = SaveNewsFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, breakingNewsFragment, BREAKING_NEWS_FRAGMENT_TAG)
                .add(R.id.fragment_container, searchNewsFragment, SEARCH_NEWS_FRAGMENT_TAG)
                .add(R.id.fragment_container, saveNewsFragment, SAVED_NEWS_FRAGMENT_TAG)
                .commit()
        } else {
            breakingNewsFragment =
                supportFragmentManager.findFragmentByTag(BREAKING_NEWS_FRAGMENT_TAG) as BreakingNewsFragment

            searchNewsFragment =
                supportFragmentManager.findFragmentByTag(SEARCH_NEWS_FRAGMENT_TAG) as SearchNewsFragment

            saveNewsFragment =
                supportFragmentManager.findFragmentByTag(SAVED_NEWS_FRAGMENT_TAG) as SaveNewsFragment

            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
        }

        selectFragment(selectedFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_breaking -> breakingNewsFragment
                R.id.nav_search -> searchNewsFragment
                R.id.nav_save -> saveNewsFragment
                else -> throw IllegalArgumentException("Unknown item")
            }

            selectFragment(fragment)
            true
        }
    }

    override fun onBackPressed() {
        if (selectedIndex != 0) {
            binding.bottomNav.selectedItemId = R.id.nav_breaking
        } else {
            super.onBackPressed()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }
}
