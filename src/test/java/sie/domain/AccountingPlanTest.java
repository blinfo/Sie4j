package sie.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.Helper;
import static sie.domain.Entity.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public class AccountingPlanTest extends Helper {

    @Test
    public void test_AccountingPlan_accounts() {
        Integer expectedNumber = 202;
        Document document = getDocument(4, 'E');
        assertTrue(document.optAccountingPlan().isPresent());
        AccountingPlan accountingPlan = document.optAccountingPlan().get();
        assertEquals(expectedNumber, Integer.valueOf(accountingPlan.accounts().size()));
    }

    @Test
    public void test_AccountingPlan_account() {
        String expectedNumber = "1240";
        String expectedLabel = "Bilar och andra transportmedel";
        String expectedSruCode = "7215";
        BigDecimal expectedFirstOpeningBalance = new BigDecimal(10000.00).setScale(SCALE, ROUNDING_MODE);
        Document document = getDocument(4, 'E');
        assertTrue(document.optAccountingPlan().isPresent());
        Account account = document.optAccountingPlan().get().accounts().get(5);
        assertEquals(expectedNumber, account.number());
        assertTrue(account.optLabel().isPresent());
        assertEquals(expectedLabel, account.optLabel().get());
        assertTrue(account.optType().isEmpty());
        assertTrue(account.optUnit().isEmpty());
        assertEquals(1, account.sruCodes().size());
        assertEquals(expectedSruCode, account.sruCodes().get(0));
        assertEquals(expectedFirstOpeningBalance, account.openingBalances().get(0).amount());
    }

    @Test
    public void test_Account_balances() {
        String accountNumber = "1249";
        BigDecimal firstOpeningBalance = new BigDecimal(-71667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondOpeningBalance = new BigDecimal(-51667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal firstClosingBalance = new BigDecimal(-91667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondClosingBalance = new BigDecimal(-71667).setScale(SCALE, ROUNDING_MODE);
        Document document = getDocument(4, 'E');
        assertTrue(document.optAccountingPlan().isPresent());
        Account account = document.optAccountingPlan().get().accounts().get(6);
        assertEquals(accountNumber, account.number());
        assertEquals(firstOpeningBalance, account.openingBalances().get(0).amount());
        assertEquals(secondOpeningBalance, account.openingBalances().get(1).amount());
        assertEquals(firstClosingBalance, account.closingBalances().get(0).amount());
        assertEquals(secondClosingBalance, account.closingBalances().get(1).amount());
        assertEquals(account.openingBalances().get(0).amount(), account.closingBalances().get(1).amount());
    }

    @Test
    public void test_Account_results() {
        String accountNumber = "3010";
        Document document = getDocument(4, 'E');
        assertTrue(document.optAccountingPlan().isPresent());
        Account account = document.optAccountingPlan().get().accounts().get(75);
        BigDecimal firstResult = new BigDecimal(-6700).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondResult = new BigDecimal(-85064.80).setScale(SCALE, ROUNDING_MODE);
        assertEquals(accountNumber, account.number());
        assertEquals(firstResult, account.results().get(0).amount());
        assertEquals(secondResult, account.results().get(1).amount());
    }

    @Test
    public void test_Account_periodicalBudgets() {
        String accountNumber = "1119";
        Document document = getDocument(4, 'E');
        assertTrue(document.optAccountingPlan().isPresent());
        Account account = document.optAccountingPlan().get().accounts().get(1);
        BigDecimal firstBudgetAmount = new BigDecimal(-1666).setScale(SCALE, ROUNDING_MODE);
        YearMonth firstBudgetPeriod = YearMonth.parse("2017-01");
        BigDecimal lastBudgetAmount = new BigDecimal(-1667).setScale(SCALE, ROUNDING_MODE);
        YearMonth lastBudgetPeriod = YearMonth.parse("2017-12");
        int last = account.getPeriodicalBudgets().size() - 1;
        assertEquals(accountNumber, account.number());
        assertEquals(firstBudgetAmount, account.getPeriodicalBudgets().get(0).amount());
        assertEquals(firstBudgetPeriod, account.getPeriodicalBudgets().get(0).period());
        assertEquals(lastBudgetAmount, account.getPeriodicalBudgets().get(last).amount());
        assertEquals(lastBudgetPeriod, account.getPeriodicalBudgets().get(last).period());
    }
}
