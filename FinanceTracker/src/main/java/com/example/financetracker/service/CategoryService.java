package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryEditRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryIconRepository categoryIconRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ClosedBudgetRepository closedBudgetRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private RecurrentTransactionRepository recurrentTransactionRepository;

    public CategoryResponseDTO createCategory(CategoryCreateRequestDTO requestDTO){
        int userId = MyUserDetailsService.getCurrentUserId();

        if (categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(userId).stream()
                .map(Category::getName).anyMatch(s -> s.equals(requestDTO.getName()))) {
            throw new BadRequestException("A category with that name already exists.");
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Category category = modelMapper.map(requestDTO, Category.class);

        category.setUser(userRepository.findById(userId)
                .orElseThrow(() -> {throw new UnauthorizedException("Invalid user id.");}));
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category icon id.");}));
        category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type id.");}));

        categoryRepository.save(category);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public List<CategoryResponseDTO> getAllCategoriesOfCurrentUser(){
        int userId = MyUserDetailsService.getCurrentUserId();
        return categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(userId).stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
         if (category.getUser() != null && category.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this category.");
        }
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public List<CategoryResponseDTO> getCategoriesOfCurrentUserByTransactionType(int transactionTypeId) {
        int userId = MyUserDetailsService.getCurrentUserId();
        return categoryRepository.findCategoriesByUserIdAndTransactionTypeId(userId, transactionTypeId)
                .stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO editCategory(CategoryEditRequestDTO requestDTO){
        int userId = MyUserDetailsService.getCurrentUserId();
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
        if (category.getUser() == null) {
            throw new BadRequestException("You cannot edit this category.");
        }
        if (category.getUser().getUserId() != userId) {
            throw new ForbiddenException("You do not have access to this category.");
        }
        if (!category.getName().equals(requestDTO.getName())){
            if (categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(userId).stream()
                    .map(Category::getName).anyMatch(s -> s.equals(requestDTO.getName()))) {
                throw new BadRequestException("A category with that name already exists.");
            }
            category.setName(requestDTO.getName());
        }
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category icon id.");}));
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Transactional
    public void deleteCategoryById(int categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
        String otherName = category.getTransactionType().getName().equalsIgnoreCase("income")
                ? "Other Income" : "Other Expense";
        Category otherCategory = categoryRepository.findByName(otherName)
                .orElseThrow(() -> {throw new NotFoundException("\"Other\" category does not exist.");});
        if (category.getUser() == null) {
            throw new BadRequestException("This category cannot be deleted.");
        }
        if (category.getUser().getUserId() != MyUserDetailsService.getCurrentUserId()) {
            throw new ForbiddenException("You do not have access to this category.");
        }
        budgetRepository.findAllByCategoryId(categoryId)
                .forEach(budget -> {
                    budget.getCategories().remove(category);
                    budget.getCategories().add(otherCategory);
                    budgetRepository.save(budget);
                });

        closedBudgetRepository.findAllByCategoryId(categoryId)
                .forEach(closedBudget -> {
                    closedBudget.getClosedBudgetCategories().remove(category);
                    closedBudget.getClosedBudgetCategories().add(otherCategory);
                    closedBudgetRepository.save(closedBudget);
                });

        transactionRepository.findAllByCategoryCategoryId(categoryId)
                .forEach(transaction -> {
                    transaction.setCategory(otherCategory);
                    transactionRepository.save(transaction);
                });

        recurrentTransactionRepository.findAllByCategoryCategoryId(categoryId)
                .forEach(recurrentTransaction -> {
                    recurrentTransaction.setCategory(otherCategory);
                    recurrentTransactionRepository.save(recurrentTransaction);
                });

        categoryRepository.deleteById(categoryId);
    }

}
