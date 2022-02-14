package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryEditRequestDTO;
import com.example.financetracker.model.dto.categoryDTOs.CategoryResponseDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.repositories.CategoryIconRepository;
import com.example.financetracker.model.repositories.CategoryRepository;
import com.example.financetracker.model.repositories.TransactionTypeRepository;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    public List<Category> getAllCategoriesByUserId(int id){
        return categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(id);
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateRequestDTO requestDTO){
        //todo review entire method, might be buggy
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Category category = modelMapper.map(requestDTO, Category.class);
        category.setUser(userRepository.findByUserId(requestDTO.getUserId()));
        if (userRepository.findByUserId(category.getUser().getUserId()) == null){
            throw new UnauthorizedException("You have to be logged in to create a category.");
        }
        if ((categoryRepository.findByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null)
                || (categoryRepository.findByNameAndUser_UserIdIsNull(requestDTO.getName()) != null)){
            throw new BadRequestException("A category with that name already exists.");
        }
        categoryRepository.save(category);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Transactional
    public CategoryResponseDTO editCategory(CategoryEditRequestDTO requestDTO){
        Category category = categoryRepository.findByCategoryId(requestDTO.getCategoryId());
        if ((category.getUser() == null) || (category.getUser().getUserId() != requestDTO.getUserId())){
            throw new UnauthorizedException("You do not have permission to edit this category.");
        }
        if (!category.getName().equals(requestDTO.getName())){
            if ((categoryRepository.findByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null)
                    || (categoryRepository.findByNameAndUser_UserIdIsNull(requestDTO.getName()) != null)){
                throw new BadRequestException("A category with that name already exists.");
            }
        }
        if (categoryIconRepository.findById(requestDTO.getCategoryIconId()).isPresent()){
            category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId()).get());
        }
        category.setName(requestDTO.getName());
        if (transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).isPresent()){
            category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).get());
        }
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public void deleteCategory(CategoryEditRequestDTO requestDTO){
        Category category = categoryRepository.findByCategoryId(requestDTO.getCategoryId());
        if ((category.getUser() == null ) || (category.getUser().getUserId() != requestDTO.getUserId())){
            throw new UnauthorizedException("You don't have permission to delete this category.");
        }
        categoryRepository.deleteById(category.getCategoryId());
    }

}
