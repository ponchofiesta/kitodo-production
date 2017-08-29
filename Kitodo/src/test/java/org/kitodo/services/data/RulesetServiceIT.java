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

package org.kitodo.services.data;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.elasticsearch.index.query.Operator;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.exceptions.DAOException;

/**
 * Tests for RulesetService class.
 */
public class RulesetServiceIT {

    @BeforeClass
    public static void prepareDatabase() throws Exception {
        MockDatabase.insertProcessesFull();
    }

    @AfterClass
    public static void cleanDatabase() {
        // MockDatabase.cleanDatabase();
    }

    @Before
    public void multipleInit() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCountAllRulesets() throws Exception {
        RulesetService rulesetService = new RulesetService();

        Long amount = rulesetService.count();
        assertEquals("Rulesets were not counted correctly!", Long.valueOf(2), amount);
    }

    @Test
    public void shouldCountAllRulesetsAccordingToQuery() throws Exception {
        RulesetService rulesetService = new RulesetService();

        String query = matchQuery("title", "SLUBDD").operator(Operator.AND).toString();
        Long amount = rulesetService.count(query);
        assertEquals("Rulesets were not counted correctly!", Long.valueOf(1), amount);
    }

    @Test
    public void shouldCountAllDatabaseRowsForRulesets() throws Exception {
        RulesetService rulesetService = new RulesetService();

        Long amount = rulesetService.countDatabaseRows();
        assertEquals("Rulesets were not counted correctly!", Long.valueOf(2), amount);
    }

    @Test
    public void shouldFindRuleset() throws Exception {
        RulesetService rulesetService = new RulesetService();

        Ruleset ruleset = rulesetService.getById(1);
        boolean condition = ruleset.getTitle().equals("SLUBDD") && ruleset.getFile().equals("ruleset_slubdd.xml");
        assertTrue("Ruleset was not found in database!", condition);
    }

    @Test
    public void shouldFindAllRulesets() {
        RulesetService rulesetService = new RulesetService();

        List<Ruleset> rulesets = rulesetService.getAll();
        assertEquals("Not all rulesets were found in database!", 2, rulesets.size());
    }

    @Test
    public void shouldFindById() throws Exception {
        RulesetService rulesetService = new RulesetService();

        String actual = rulesetService.findById(1).getTitle();
        String expected = "SLUBDD";
        assertEquals("Ruleset was not found in index!", expected, actual);
    }

    @Test
    public void shouldFindByTitle() throws Exception {
        RulesetService rulesetService = new RulesetService();

        List<JSONObject> rulesets = rulesetService.findByTitle("SLUBDD", true);
        Integer actual = rulesets.size();
        Integer expected = 1;
        assertEquals("Ruleset was not found in index!", expected, actual);
    }

    @Test
    public void shouldFindByFile() throws Exception {
        RulesetService rulesetService = new RulesetService();

        JSONObject ruleset = rulesetService.findByFile("ruleset_slubdd.xml");
        JSONObject jsonObject = (JSONObject) ruleset.get("_source");
        String actual = (String) jsonObject.get("file");
        String expected = "ruleset_slubdd.xml";
        assertEquals("Ruleset was not found in index!", expected, actual);
    }

    @Test
    public void shouldFindByTitleAndFile() throws Exception {
        RulesetService rulesetService = new RulesetService();

        JSONObject ruleset = rulesetService.findByTitleAndFile("SLUBHH", "ruleset_slubhh.xml");
        Integer actual = rulesetService.getIdFromJSONObject(ruleset);
        Integer expected = 2;
        assertEquals("Ruleset was not found in index!", expected, actual);

        ruleset = rulesetService.findByTitleAndFile("SLUBDD", "none");
        actual = rulesetService.getIdFromJSONObject(ruleset);
        expected = 0;
        assertEquals("Ruleset was found in index!", expected, actual);
    }

    @Test
    public void shouldFindByTitleOrFile() throws Exception {
        RulesetService rulesetService = new RulesetService();

        List<JSONObject> ruleset = rulesetService.findByTitleOrFile("SLUBDD", "ruleset_slubhh.xml");
        Integer actual = ruleset.size();
        Integer expected = 2;
        assertEquals("Rulesets were not found in index!", expected, actual);

        ruleset = rulesetService.findByTitleOrFile("default", "ruleset_slubhh.xml");
        actual = ruleset.size();
        expected = 1;
        assertEquals("Ruleset was not found in index!", expected, actual);

        ruleset = rulesetService.findByTitleOrFile("none", "none");
        actual = ruleset.size();
        expected = 0;
        assertEquals("Some rulesets were found in index!", expected, actual);
    }

    @Test
    public void shouldFindAllRulesetsDocuments() throws Exception {
        RulesetService rulesetService = new RulesetService();

        List<JSONObject> rulesets = rulesetService.findAllDocuments();
        assertEquals("Not all rulesets were found in index!", 2, rulesets.size());
    }

    @Test
    public void shouldRemoveRuleset() throws Exception {
        RulesetService rulesetService = new RulesetService();

        Ruleset ruleset = new Ruleset();
        ruleset.setTitle("To Remove");
        rulesetService.save(ruleset);
        Ruleset foundRuleset = rulesetService.getById(3);
        assertEquals("Additional ruleset was not inserted in database!", "To Remove", foundRuleset.getTitle());

        rulesetService.remove(ruleset);
        exception.expect(DAOException.class);
        rulesetService.getById(3);

        ruleset = new Ruleset();
        ruleset.setTitle("To remove");
        rulesetService.save(ruleset);
        foundRuleset = rulesetService.getById(4);
        assertEquals("Additional ruleset was not inserted in database!", "To remove", foundRuleset.getTitle());

        rulesetService.remove(4);
        exception.expect(DAOException.class);
        rulesetService.getById(4);
    }

    @Test
    public void shouldGetPreferences() throws Exception {
        RulesetService rulesetService = new RulesetService();

        Ruleset ruleset = rulesetService.getById(1);
        String actual = rulesetService.getPreferences(ruleset).getVersion();
        // not sure how to really check if Pref is correct
        System.out.println("Preferences: " + actual);
        assertEquals("Preference is incorrect!", "1.1-20091117", actual);
    }
}
