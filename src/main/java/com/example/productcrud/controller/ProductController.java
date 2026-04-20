package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.ProductService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = getCurrentUser(userDetails);
        long totalProducts = productService.countByOwner(currentUser);
        long activeProducts = productService.countActiveByOwner(currentUser);
        long inactiveProducts = productService.countInactiveByOwner(currentUser);
        long totalInventoryValue = productService.sumInventoryValueByOwner(currentUser);
        List<ProductRepository.CategoryCountProjection> categoryStats =
                productService.countProductsByCategory(currentUser);
        List<Product> lowStockProducts = productService.findLowStockProducts(currentUser);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("inactiveProducts", inactiveProducts);
        model.addAttribute("totalInventoryValue", totalInventoryValue);
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("isEmptyProducts", totalProducts == 0);
        return "index";
    }

    @GetMapping("/products")
    public String listProducts(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer category,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        User currentUser = getCurrentUser(userDetails);
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        Category selectedCategory = Category.fromIndex(category);
        int currentPage = Math.max(page, 0);
        PageRequest pageable = PageRequest.of(currentPage, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = productService.findAllByOwnerAndFilters(
                currentUser, normalizedKeyword, selectedCategory, pageable);

        if (currentPage > 0 && productPage.getTotalPages() > 0 && currentPage >= productPage.getTotalPages()) {
            currentPage = productPage.getTotalPages() - 1;
            pageable = PageRequest.of(currentPage, 10, Sort.by(Sort.Direction.DESC, "id"));
            productPage = productService.findAllByOwnerAndFilters(
                    currentUser, normalizedKeyword, selectedCategory, pageable);
        }

        long startItem = productPage.getTotalElements() == 0 ? 0 : ((long) productPage.getNumber() * productPage.getSize()) + 1;
        long endItem = productPage.getTotalElements() == 0
                ? 0
                : startItem + productPage.getNumberOfElements() - 1;
        List<Integer> pageNumbers = IntStream.range(0, productPage.getTotalPages())
                .boxed()
                .toList();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("categories", Category.values());
        model.addAttribute("keyword", normalizedKeyword);
        model.addAttribute("selectedCategory", selectedCategory == null ? null : category);
        model.addAttribute("hasFilter", !normalizedKeyword.isBlank() || selectedCategory != null);
        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detailProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        return productService.findByIdAndOwner(id, currentUser)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Produk tidak ditemukan.");
                    return "redirect:/products";
                });
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        model.addAttribute("product", product);
        model.addAttribute("categories", Category.values());
        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (product.getId() != null) {
            // Edit: pastikan produk milik user ini
            boolean isOwner = productService.findByIdAndOwner(product.getId(), currentUser).isPresent();
            if (!isOwner) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produk tidak ditemukan.");
                return "redirect:/products";
            }
        }

        product.setOwner(currentUser);
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        return productService.findByIdAndOwner(id, currentUser)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("categories", Category.values());
                    return "product/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Produk tidak ditemukan.");
                    return "redirect:/products";
                });
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        boolean isOwner = productService.findByIdAndOwner(id, currentUser).isPresent();

        if (isOwner) {
            productService.deleteByIdAndOwner(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Produk tidak ditemukan.");
        }

        return "redirect:/products";
    }
}
