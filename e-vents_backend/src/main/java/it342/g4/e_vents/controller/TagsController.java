package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.service.CategoryService;
import it342.g4.e_vents.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/tags")
public class TagsController {

    private final TagsService tagsService;
    private final CategoryService categoryService;

    @Autowired
    public TagsController(TagsService tagsService, CategoryService categoryService) {
        this.tagsService = tagsService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showTagsDashboard(Model model) {
        List<Tags> tags = tagsService.getAllTags();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("tags", tags);
        model.addAttribute("categories", categories);
        model.addAttribute("tag", new Tags());
        
        return "admin/tags";
    }

    @PostMapping("/save")
    public String saveTag(@ModelAttribute Tags tag, RedirectAttributes redirectAttributes) {
        tagsService.saveTag(tag);
        redirectAttributes.addFlashAttribute("successMessage", "Tag saved successfully");
        return "redirect:/admin/tags";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Tags tag = tagsService.getTagById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tag ID: " + id));
        
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("tag", tag);
        model.addAttribute("categories", categories);
        
        return "admin/tag-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tagsService.deleteTag(id);
        redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully");
        return "redirect:/admin/tags";
    }

    @GetMapping("/by-category/{categoryId}")
    @ResponseBody
    public List<Tags> getTagsByCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
        
        return tagsService.getTagsByCategory(category);
    }
}
