package com.alec.Bud_Cal.controller;

import com.alec.Bud_Cal.model.Expense;
import com.alec.Bud_Cal.model.Income;
import com.alec.Bud_Cal.model.User;
import com.alec.Bud_Cal.service.AuthService;
import com.alec.Bud_Cal.service.ExpenseService;
import com.alec.Bud_Cal.service.IncomeService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BudgetController {

    private static final String SESSION_USER_EMAIL = "userEmail";

    private final AuthService authService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public BudgetController(AuthService authService, IncomeService incomeService, ExpenseService expenseService) {
        this.authService = authService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        User user = userOpt.get();
        String userId = user.getUserId();

        List<Income> incomes = incomeService.getIncomesByUserId(userId);
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);

        BigDecimal totalIncome = incomeService.getTotalIncome(userId);
        BigDecimal totalExpenses = expenseService.getTotalExpenses(userId);
        BigDecimal netIncome = totalIncome.subtract(totalExpenses);

        model.addAttribute("userName", user.getName());
        model.addAttribute("incomes", incomes);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("netIncome", netIncome);
        model.addAttribute("newIncome", new Income());
        model.addAttribute("newExpense", new Expense());

        return "dashboard";
    }

    @GetMapping("/allocation-calculator")
    public String showAllocationCalculator(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        User user = userOpt.get();
        String userId = user.getUserId();

        BigDecimal totalIncome = incomeService.getTotalIncome(userId);
        BigDecimal totalExpenses = expenseService.getTotalExpenses(userId);

        model.addAttribute("userName", user.getName());
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);

        return "allocation-calculator";
    }

    @PostMapping("/income")
    public String addIncome(@ModelAttribute Income income, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        income.setUserId(userOpt.get().getUserId());
        incomeService.saveIncome(income);

        redirectAttributes.addFlashAttribute("successMessage", "Income added successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/income/{id}/edit")
    public String editIncome(@PathVariable String id, @ModelAttribute Income income, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        Optional<Income> existingIncome = incomeService.getIncomeById(id);
        if (existingIncome.isPresent() && existingIncome.get().getUserId().equals(userOpt.get().getUserId())) {
            income.setIncomeId(id);
            income.setUserId(userOpt.get().getUserId());
            income.setCreatedAt(existingIncome.get().getCreatedAt());
            incomeService.saveIncome(income);
            redirectAttributes.addFlashAttribute("successMessage", "Income updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Income not found or access denied.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/income/{id}/delete")
    public String deleteIncome(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        boolean deleted = incomeService.deleteIncomeByIdAndUserId(id, userOpt.get().getUserId());
        System.out.println("Delete income request received. id=" + id + ", userEmail=" + userEmail
                + ", resolvedUserId=" + userOpt.get().getUserId()
                + ", deleted=" + deleted);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Income deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Income not found or access denied.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/expense")
    public String addExpense(@ModelAttribute Expense expense, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        expense.setUserId(userOpt.get().getUserId());
        expenseService.saveExpense(expense);

        redirectAttributes.addFlashAttribute("successMessage", "Expense added successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/expense/{id}/edit")
    public String editExpense(@PathVariable String id, @ModelAttribute Expense expense, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        Optional<Expense> existingExpense = expenseService.getExpenseById(id);
        if (existingExpense.isPresent() && existingExpense.get().getUserId().equals(userOpt.get().getUserId())) {
            expense.setExpenseId(id);
            expense.setUserId(userOpt.get().getUserId());
            expense.setCreatedAt(existingExpense.get().getCreatedAt());
            expenseService.saveExpense(expense);
            redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Expense not found or access denied.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/expense/{id}/delete")
    public String deleteExpense(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute(SESSION_USER_EMAIL);
        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = authService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        boolean deleted = expenseService.deleteExpenseByIdAndUserId(id, userOpt.get().getUserId());
        System.out.println("Delete expense request received. id=" + id + ", userEmail=" + userEmail
                + ", resolvedUserId=" + userOpt.get().getUserId()
                + ", deleted=" + deleted);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Expense not found or access denied.");
        }

        return "redirect:/dashboard";
    }
}
