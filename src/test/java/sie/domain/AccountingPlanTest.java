package sie.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.Helper;
import static sie.domain.Entity.ROUNDING_MODE;
import static sie.domain.Entity.SCALE;

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
        assertTrue("Accounting plan should exist", document.getAccountingPlan().isPresent());
        AccountingPlan accountingPlan = document.getAccountingPlan().get();
        assertEquals("Accounting plan should contain " + expectedNumber + " accounts", expectedNumber, Integer.valueOf(accountingPlan.getAccounts().size()));
    }

    @Test
    public void test_AccountingPlan_account() {
        String expectedNumber = "1240";
        String expectedLabel = "Bilar och andra transportmedel";
        String expectedSruCode = "7215";
        BigDecimal expectedFirstOpeningBalance = new BigDecimal(10000.00).setScale(SCALE, ROUNDING_MODE);
        Document document = getDocument(4, 'E');
        assertTrue("AccountingPlan should exist", document.getAccountingPlan().isPresent());
        Account account = document.getAccountingPlan().get().getAccounts().get(5);
        assertEquals("Account should have number " + expectedNumber, expectedNumber, account.getNumber());
        assertTrue("Account should have a label", account.getLabel().isPresent());
        assertEquals("Account label should be" + expectedLabel, expectedLabel, account.getLabel().get());
        assertTrue("Account should not have Type", !account.getType().isPresent());
        assertTrue("Account should not have Unit", !account.getUnit().isPresent());
        assertEquals("Account should have one SRU code", 1, account.getSruCodes().size());
        assertEquals("SRU code should be " + expectedSruCode, expectedSruCode, account.getSruCodes().get(0));
        assertEquals("First opening balance should be " + expectedFirstOpeningBalance, expectedFirstOpeningBalance, account.getOpeningBalances().get(0).getAmount());
    }

    @Test
    public void test_Account_balances() {
        String accountNumber = "1249";
        BigDecimal firstOpeningBalance = new BigDecimal(-71667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondOpeningBalance = new BigDecimal(-51667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal firstClosingBalance = new BigDecimal(-91667).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondClosingBalance = new BigDecimal(-71667).setScale(SCALE, ROUNDING_MODE);
        Document document = getDocument(4, 'E');
        assertTrue("AccountingPlan should exist", document.getAccountingPlan().isPresent());
        Account account = document.getAccountingPlan().get().getAccounts().get(6);
        assertEquals("Account number should be " + accountNumber, accountNumber, account.getNumber());
        assertEquals("First opening balance should be " + firstOpeningBalance, firstOpeningBalance, account.getOpeningBalances().get(0).getAmount());
        assertEquals("Second opening balance should be " + secondOpeningBalance, secondOpeningBalance, account.getOpeningBalances().get(1).getAmount());
        assertEquals("First closing balance should be " + firstClosingBalance, firstClosingBalance, account.getClosingBalances().get(0).getAmount());
        assertEquals("Second closing balance should be " + secondClosingBalance, secondClosingBalance, account.getClosingBalances().get(1).getAmount());
        assertTrue("First opening balance should be same as second closing balance",
                account.getOpeningBalances().get(0).getAmount().equals(account.getClosingBalances().get(1).getAmount()));
    }

    @Test
    public void test_Account_results() {
        String accountNumber = "3010";
        Document document = getDocument(4, 'E');
        assertTrue("AccountingPlan should exist", document.getAccountingPlan().isPresent());
        Account account = document.getAccountingPlan().get().getAccounts().get(75);
        BigDecimal firstResult = new BigDecimal(-6700).setScale(SCALE, ROUNDING_MODE);
        BigDecimal secondResult = new BigDecimal(-85064.80).setScale(SCALE, ROUNDING_MODE);
        assertEquals("Account number should be " + accountNumber, accountNumber, account.getNumber());
        assertEquals("First result should be " + firstResult, firstResult, account.getResults().get(0).getAmount());
        assertEquals("Second result should be " + secondResult, secondResult, account.getResults().get(1).getAmount());
    }

    @Test
    public void test_Account_periodicalBudgets() {
        String accountNumber = "1119";
        Document document = getDocument(4, 'E');
        assertTrue("AccountingPlan should exist", document.getAccountingPlan().isPresent());
        Account account = document.getAccountingPlan().get().getAccounts().get(1);
        BigDecimal firstBudgetAmount = new BigDecimal(-1666).setScale(SCALE, ROUNDING_MODE);
        YearMonth firstBudgetPeriod = YearMonth.parse("2017-01");
        BigDecimal lastBudgetAmount = new BigDecimal(-1667).setScale(SCALE, ROUNDING_MODE);
        YearMonth lastBudgetPeriod = YearMonth.parse("2017-12");
        int last = account.getPeriodicalBudgets().size() - 1;
        assertEquals("Account number should be " + accountNumber, accountNumber, account.getNumber());
        assertEquals("First budgeted amount should be " + firstBudgetAmount, firstBudgetAmount, account.getPeriodicalBudgets().get(0).getAmount());
        assertEquals("First budgeted period should be " + firstBudgetPeriod, firstBudgetPeriod, account.getPeriodicalBudgets().get(0).getPeriod());
        assertEquals("Last budgeted amount should be " + lastBudgetAmount, lastBudgetAmount, account.getPeriodicalBudgets().get(last).getAmount());
        assertEquals("Last budgeted period should be " + lastBudgetPeriod, lastBudgetPeriod, account.getPeriodicalBudgets().get(last).getPeriod());
    }
}
