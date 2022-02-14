package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.categoryDTOs.CategoryCreateRequestDTO;
import com.example.financetracker.model.pojo.Category;
import com.example.financetracker.model.repositories.CategoryRepository;
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

    public List<Category> getAllCategoriesByUserId(int id){
        return categoryRepository.findAllByUser_UserIdOrUser_UserIdIsNull(id);
    }

    @Transactional
    public Category createCategory(CategoryCreateRequestDTO requestDTO){
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
        return category;
    }
}
