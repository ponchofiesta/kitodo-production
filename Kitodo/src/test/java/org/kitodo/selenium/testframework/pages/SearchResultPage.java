/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.selenium.testframework.pages;

import static org.kitodo.selenium.testframework.Browser.getDriver;
import static org.kitodo.selenium.testframework.Browser.getRowsOfTable;
import static org.kitodo.selenium.testframework.Browser.getTableDataByColumn;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchResultPage extends Page<SearchResultPage> {

    private static final String SEARCH_RESULT_TABLE = "searchResultTable";
    private static final String SEARCH_RESULT_FORM = "searchResultForm";
    private static final String FILTER_CONFIGURATION = "configureFilters";



    @SuppressWarnings("unused")
    @FindBy(id = SEARCH_RESULT_FORM + ":" + SEARCH_RESULT_TABLE + "_data")
    private WebElement searchResultTable;

    @SuppressWarnings("unused")
    @FindBy(id = FILTER_CONFIGURATION + ":projectfilter")
    private WebElement projectsDropdown;

    @SuppressWarnings("unused")
    @FindBy(id = FILTER_CONFIGURATION + ":projectfilter_items")
    private WebElement projectsForFiltering;

    public SearchResultPage() {
        super("searchResult.jsf");
    }

    @Override
    public SearchResultPage goTo() {
        return null;
    }

    public int getNumberOfResults() {
        List<String> tableDataByColumn = getTableDataByColumn(searchResultTable, 0);
        if (tableDataByColumn.contains("No records found.")) {
            return 0;
        }
        return getRowsOfTable(searchResultTable).size();
    }

    public String getProjectsForFilter() {
        projectsDropdown.click();
        return projectsForFiltering.getText();
    }

    public void filterByProject() {
        WebElement projectFilter = getDriver().findElementByPartialLinkText("First");
        projectFilter.click();
    }
}
