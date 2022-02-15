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
        if (!userRepository.existsById(requestDTO.getUserId())){
            //TODO: SECURITY -> LOG USER OUT and return OK:
            //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
            //sss.logout(...);
            throw new UnauthorizedException("You have to be logged in to create a category.");
        }
        //TODO: Dao
        if ((categoryRepository.findByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null)
                || (categoryRepository.findByNameAndUser_UserIdIsNull(requestDTO.getName()) != null)){
            throw new BadRequestException("A category with that name already exists.");
        }
        if (!categoryIconRepository.existsById(requestDTO.getCategoryIconId())) {
            throw new BadRequestException("Invalid category icon.");
        }
        if (!transactionTypeRepository.existsById(requestDTO.getTransactionTypeId())) {
            throw new BadRequestException("Invalid transaction type.");
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Category category = modelMapper.map(requestDTO, Category.class);
        category.setUser(userRepository.findByUserId(requestDTO.getUserId()));
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId()).orElse(null));
        category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).orElse(null));
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
        Category category = categoryRepository.findByCategoryId(requestDTO.getCategoryId());
        if (!category.getName().equals(requestDTO.getName())){
            //TODO: Dao
            if ((categoryRepository.findByUser_UserIdAndName(requestDTO.getUserId(), requestDTO.getName()) != null)
                    || (categoryRepository.findByNameAndUser_UserIdIsNull(requestDTO.getName()) != null)){
                throw new BadRequestException("A category with that name already exists.");
            }
        }
        if (!categoryIconRepository.existsById(requestDTO.getCategoryIconId())) {
            throw new BadRequestException("Invalid category icon.");
        }
        if (!transactionTypeRepository.existsById(requestDTO.getTransactionTypeId())) {
            throw new BadRequestException("Invalid transaction type.");
        }
        category.setName(requestDTO.getName());
        category.setCategoryIcon(categoryIconRepository.findById(requestDTO.getCategoryIconId()).orElse(null));
        category.setTransactionType(transactionTypeRepository.findById(requestDTO.getTransactionTypeId()).orElse(null));
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public void deleteCategory(CategoryEditRequestDTO requestDTO){
        Category category = categoryRepository.findByCategoryId(requestDTO.getCategoryId());
        if (category == null) {
            throw new BadRequestException("Invalid category.");
        }
        if (!userRepository.existsById(requestDTO.getUserId()) || requestDTO.getUserId() != category.getUser().getUserId()){
            //TODO: SECURITY -> only for user with the same id; otherwise LOG USER OUT and return OK:
            //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
            //sss.logout(...);
            throw new UnauthorizedException("You have to be logged in to create a category.");
        }
        if (category.getUser() == null) {
            throw new BadRequestException("This category cannot be deleted.");
        }
        categoryRepository.deleteById(category.getCategoryId());
    }

}
