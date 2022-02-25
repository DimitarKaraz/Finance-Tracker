package com.example.financetracker.service;

import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.budgetDTOs.BudgetResponseDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.dto.closedBudgetDTOs.ClosedBudgetResponseDTO;
import com.example.financetracker.model.pojo.Budget;
import com.example.financetracker.model.pojo.ClosedBudget;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.ClosedBudgetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class ClosedBudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${default.page.size}")
    private int pageSize;

    @Transactional
    public BudgetResponseDTO openClosedBudgetById(int closedBudgetId) {
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                        .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        Budget budget = modelMapper.map(closedBudget, Budget.class);
        budget.setBudgetId(0);
        budget.setStartDate(LocalDate.now());
        budget.setCategories(closedBudget.getClosedBudgetCategories());
        closedBudgetRepository.deleteById(closedBudgetId);
        budgetRepository.save(budget);
        return BudgetService.convertToBudgetResponseDTO(modelMapper, budget);
    }

    public Map<String, Object> getAllClosedBudgetsOfCurrentUser(int pageNo){
        int userId = MyUserDetailsService.getCurrentUserId();
        Page<ClosedBudget> closedBudgetsPage = closedBudgetRepository.findAllByAccount_User_UserId(userId,
                PageRequest.of(pageNo, pageSize, Sort.by("interval_intervalId").ascending()));

        return covertToMap(closedBudgetsPage, pageNo);
    }

    public ClosedBudgetResponseDTO getClosedBudgetById(int closedBudgetId){
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        return convertToClosedBudgetResponseDTO(modelMapper, closedBudget);
    }

    public void deleteClosedBudgetById(int closedBudgetId) {
        ClosedBudget closedBudget = closedBudgetRepository.findById(closedBudgetId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid closed budget id.");});
        if (closedBudget.getAccount().getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this closed budget.");
        }
        closedBudgetRepository.deleteById(closedBudgetId);
    }

    static ClosedBudgetResponseDTO convertToClosedBudgetResponseDTO(@Autowired ModelMapper modelMapper, ClosedBudget closedBudget) {
        ClosedBudgetResponseDTO responseDTO = modelMapper.map(closedBudget, ClosedBudgetResponseDTO.class);
        responseDTO.setCategoryResponseDTOs(closedBudget.getClosedBudgetCategories().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toSet()));
        responseDTO.setCurrency(closedBudget.getAccount().getCurrency());
        return responseDTO;
    }

    private Map<String, Object> covertToMap(Page<ClosedBudget> closedBudgetsPage, int pageNo) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalItems", closedBudgetsPage.getTotalElements());
        map.put("currentPage", pageNo);
        map.put("totalPages", closedBudgetsPage.getTotalPages());
        map.put("Budgets", closedBudgetsPage.getContent().stream()
                .map(budget -> convertToClosedBudgetResponseDTO(modelMapper, budget))
                .collect(Collectors.toList()));
        return map;
    }
}
