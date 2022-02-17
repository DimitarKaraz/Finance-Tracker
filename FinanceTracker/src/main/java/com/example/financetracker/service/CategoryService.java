package com.example.financetracker.service;

import com.example.financetracker.exceptions.*;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryEditRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
    private BudgetService budgetService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private RecurrentTransactionRepository recurrentTransactionRepository;
    @Autowired
    private RecurrentTransactionService recurrentTransactionService;

    public CategoryResponseDTO getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
//        if (category.getUser() != null && <session.userId != category.getUser().getUserId()>) {
//            throw new UnauthorizedException("You cannot access this category.");
//        }
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public List<CategoryResponseDTO> getAllCategoriesByUserId(int userId){
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Invalid user id.");
        }
        return categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(userId).stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

//    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateRequestDTO requestDTO){
        //TODO: SECURITY -> LOG USER OUT and return OK:

        if (categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(requestDTO.getUserId()).stream()
                .map(Category::getName).anyMatch(s -> s.equals(requestDTO.getName()))) {
            throw new BadRequestException("A category with that name already exists.");
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Category category = modelMapper.map(requestDTO, Category.class);

        category.setUser(userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {throw new UnauthorizedException("You have to be logged in to create a category.");}));
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category icon id.");}));
        category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type id.");}));

        categoryRepository.save(category);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

//    @Transactional
    public CategoryResponseDTO editCategory(CategoryEditRequestDTO requestDTO){
        if (!userRepository.existsById(requestDTO.getUserId())){
            //TODO: SECURITY -> LOG USER OUT and return OK:
            //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
            //sss.logout(...);
            throw new UnauthorizedException("You have to be logged in to create a category.");
        }
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid category id.");});
        if (category.getUser() == null) {
            throw new ForbiddenException("You cannot edit this category.");
        }
        if (category.getUser().getUserId() != requestDTO.getUserId()) {
            //TODO: SECURITY -> LOG USER OUT and return OK:
            //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
            //sss.logout(...);
            throw new UnauthorizedException("You cannot edit this category.");
        }
        if (!category.getName().equals(requestDTO.getName())){
            if (categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(requestDTO.getUserId()).stream()
                    .map(Category::getName).anyMatch(s -> s.equals(requestDTO.getName()))) {
                throw new BadRequestException("A category with that name already exists.");
            }
            category.setName(requestDTO.getName());
        }
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid category icon id.");}));
        category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId())
                .orElseThrow(() -> {throw new BadRequestException("Invalid transaction type id.");}));
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Transactional
    public void deleteCategoryById(int categoryId){
        Category otherCategory = categoryRepository.findById(1)
                .orElseThrow(() -> {throw new NotFoundException("\"Other\" category does not exist.");});
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {throw new NotFoundException("Category does not exist.");});
        if (category.getUser() == null) {
            throw new BadRequestException("This category cannot be deleted.");
        }
        budgetRepository.findAllByCategoryId(categoryId)
                .forEach(budget -> {
                    budget.getCategories().remove(category);
                    budget.getCategories().add(otherCategory);
                    budgetRepository.save(budget);
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

        //TODO: same check for closedBudgets

        categoryRepository.deleteById(categoryId);
    }


}
