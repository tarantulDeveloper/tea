package kg.tarantula.tea.controllers;

import kg.tarantula.tea.entity.Ingredient;
import kg.tarantula.tea.repository.IngredientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private IngredientRepo ingredientRepo;

    @GetMapping(value = "/")
    public String helloWorld(Model model) {
        List<Ingredient> ingredientList = ingredientRepo.findAll();
        model.addAttribute("ingredients",ingredientList);
        return "HomePage";

    }

    @GetMapping("/add-ingredient")
    public String addIngredientPage() {
        return "AddIngredient";
    }

    @GetMapping("/edit/{id}")
    public String editIngredient(@PathVariable("id") int id, Model model) {
        Ingredient editableIngredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No such ingredient!"));
        model.addAttribute("ingredient", editableIngredient);
        return "edit-ingredient";
    }

    @PostMapping("/")
    public String uploadIngredient(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("photo")MultipartFile photo) throws IOException {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setPrice(price);
        String photoUrl = UUID.randomUUID().toString()+ "." + photo.getOriginalFilename();
        photo.transferTo(new File(uploadPath  + photoUrl));
        ingredient.setPhotoUrl(photoUrl);

        ingredientRepo.save(ingredient);
        return "redirect:/";

    }

    @PostMapping("/{id}")
     public String updateIngredient(@PathVariable("id") int id,
                                    @RequestParam("name") String name,
                                    @RequestParam("price") double price,
                                    @RequestParam("photo") MultipartFile photo) throws IOException {
        Ingredient updatableIngredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No such ingredient!"));
        updatableIngredient.setName(name);
        updatableIngredient.setPrice(price);
        Files.deleteIfExists(Path.of(uploadPath + updatableIngredient.getPhotoUrl()));
        String photoUrl = UUID.randomUUID().toString()+ "." + photo.getOriginalFilename();
        photo.transferTo(new File(uploadPath  + photoUrl));
        updatableIngredient.setPhotoUrl(photoUrl);
        ingredientRepo.save(updatableIngredient);
        return "redirect:/";
    }


@GetMapping("/getImage")
public ResponseEntity<Resource> getImage(@RequestParam("fileName") String fileName) throws IOException {
    Path path = Paths.get(uploadPath + fileName);
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path));
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + fileName);
    headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600");
    return ResponseEntity.ok()
            .headers(headers)
            .contentLength(Files.size(path))
            .body(resource);
}


}
