package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.Options.Trace;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;

import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

/**
 * Solution by Evelyn Rong for Exercise 2 (Web) Implemented using Playwright and
 * JUnit. Ran as a JUnit test.
 **/
@UsePlaywright(Exercise2.CustomOptions.class)
public class Exercise2 {
	// These options let the test run in headed mode and produces trace.zip files in
	// "test-results" folder, which can be viewed with https://trace.playwright.dev/
	public static class CustomOptions implements OptionsFactory {
		@Override
		public Options getOptions() {
			return new Options().setHeadless(false).setTrace(Trace.ON);
		}
	}

	@Test
	void scenario1(Page page) {
		/* 1. Go to Mercari top page (https://jp.mercari.com/) */
		page.navigate("https://jp.mercari.com/");

		/* 2. Click on the search bar */
		page.getByRole(AriaRole.SEARCH, new Page.GetByRoleOptions().setName("検索")).click();

		/* 3. Click on "Select by category" (カテゴリーからさがす) */
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("カテゴリーからさがす")).click();

		/* 4. Select "Books, Music & Games" as the tier 1 category (本・音楽・ゲーム) */

		// The category specified in the instructions does not seem to exist anymore.
		// However, there does exist a category 「本・雑誌・漫画」 that contains the necessary
		// sub-categories.
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("本・雑誌・漫画")).click();

		/* 5. Select "Books" as the tier 2 category (本) */

		// Using the same locator method as the previous two links will result in
		// multiple elements being found, failing the strictness criteria.
		// Since there is only one element with the exact text 「本」, I have used that
		// criteria instead.
		page.getByText("本", new Page.GetByTextOptions().setExact(true)).click();

		/* 6. Select "Computers & Technology" as the tier 3 category (コンピュータ/IT) */
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("コンピュータ・IT")).click();

		/* 7. Verify the search conditions on the left sidebar are set correctly */
		Locator selectBox = page.getByRole(AriaRole.COMBOBOX);

		// Wait for page to load; default timeout is too short
		selectBox.nth(2).waitFor(new Locator.WaitForOptions().setTimeout(20000));

		// The category selection boxes seem to contain the text of all of their values
		// simultaneously.
		// So instead of asserting the text, I am asserting that it has the correct
		// value selected.

		// <option value="5">本・雑誌・漫画</option>
		assertThat(selectBox.nth(1)).hasValue("5");

		// <option value="72">本</option>
		assertThat(selectBox.nth(2)).hasValue("72");
		assertThat(page.getByLabel("コンピュータ・IT")).isChecked();
	}

	@Test
	void scenario2(Page page) {
		/*
		 * Pre-condition: Create 2 browsing history entries. The latest one should be
		 * the categories in Scenario 1.
		 */

		// This portion of the code fulfills the precondition. Since Playwright makes
		// each test case have a separate context by default, it would make sense for
		// the setup of the test case to be included within the test case itself.
		// Also, test cases should be able to be run independently as a principle.

		// Search for long flared skirts:
		page.navigate("https://jp.mercari.com/");
		Locator searchBar = page.getByRole(AriaRole.SEARCH, new Page.GetByRoleOptions().setName("検索"));
		searchBar.click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("カテゴリーからさがす")).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("ファッション")).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("レディース")).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("スカート")).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("ロングスカート")).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("フレアスカート")).click();

		// Clear the current search parameters (page might take a while to load):
		Locator clearSearch = page.getByLabel("close");
		clearSearch.waitFor(new Locator.WaitForOptions().setTimeout(20000));
		clearSearch.click();

		// Search for Computer/IT books:
		searchBar.click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("カテゴリーからさがす")).click();

		// The category specified in the instructions does not seem to exist anymore.
		// However, there does exist a category 「本・雑誌・漫画」 that contains the necessary
		// sub-categories.
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("本・雑誌・漫画")).click();
		page.getByText("本", new Page.GetByTextOptions().setExact(true)).click();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("コンピュータ・IT")).click();
		clearSearch.click();

		/* 1. Go to Mercari top page (https://jp.mercari.com/) */
		page.navigate("https://jp.mercari.com/");

		/* 2. Click on the search bar */
		searchBar.click();

		/* 2. Verify there are 2 browsing histories */
		Locator searchHistoryList = page.getByTestId("search-history").getByRole(AriaRole.LISTITEM);
		assertThat(searchHistoryList).hasCount(2);

		/*
		 * 3. Verify the latest browsing history is showing correctly (Computers &
		 * Technology / コンピュータ/IT)
		 */
		assertThat(searchHistoryList.nth(0)).hasText("コンピュータ・IT");

		/* 4. Click on the latest browsing histories */
		searchHistoryList.nth(0).click();

		/* 5. Verify the search conditions on the left sidebar are set correctly */
		Locator selectBox = page.getByRole(AriaRole.COMBOBOX);

		// Wait for page to load; default timeout is too short
		selectBox.nth(2).waitFor(new Locator.WaitForOptions().setTimeout(20000));

		// The category selection boxes seem to contain the text of all of their values
		// simultaneously.
		// So instead of asserting the text, I am asserting that it has the correct
		// value selected.

		// <option value="5">本・雑誌・漫画</option>
		assertThat(selectBox.nth(1)).hasValue("5");

		// <option value="72">本</option>
		assertThat(selectBox.nth(2)).hasValue("72");
		assertThat(page.getByLabel("コンピュータ・IT")).isChecked();

		/* 6. Input "javascript" in the search bar and search with the keyword */

		// Click only the portion of the bar that is empty
		Locator searchField = page.getByLabel("検索キーワードを入力");
		searchField.click();
		searchField.fill("javascript");
		searchField.press("Enter");

		// Wait for the search result heading to appear, to make sure that the search is
		// recorded.
		page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("javascript の検索結果"))
				.waitFor(new Locator.WaitForOptions().setTimeout(20000));

		/* 7. Go back to Mercari top page (https://jp.mercari.com/) */
		page.navigate("https://jp.mercari.com/");

		/* 8. Verify there are 3 browsing histories */
		searchBar.click();
		assertThat(searchHistoryList).hasCount(3);

		/*
		 * 9. Verify the latest browsing history is showing correctly (javascript,
		 * Computers & Technology / javascript, コンピュータ/IT)
		 */
		assertThat(searchHistoryList.nth(0)).hasText("javascript, コンピュータ・IT");
	}
}
